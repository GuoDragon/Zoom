package com.example.zoom.data

import android.content.Context
import android.util.Log
import com.example.zoom.common.constants.JoinHistoryActionTypes
import com.example.zoom.common.constants.MeetingActionTypes
import com.example.zoom.common.constants.RuntimeSignalFileNames
import com.example.zoom.common.constants.RuntimeSignalPrefixes
import com.example.zoom.model.ChatThreadStateSignal
import com.example.zoom.model.ClipboardActionSignal
import com.example.zoom.model.DirectMessageSignal
import com.example.zoom.model.InstantMeetingSessionSignal
import com.example.zoom.model.JoinMeetingHistoryAction
import com.example.zoom.model.JoinMeetingHistoryEntry
import com.example.zoom.model.JoinMeetingHistorySignal
import com.example.zoom.model.Meeting
import com.example.zoom.model.MeetingActionSignal
import com.example.zoom.model.MeetingPreferencesSignal
import com.example.zoom.model.Message
import com.example.zoom.model.RuntimeSignalMeta
import com.example.zoom.model.ScheduledMeetingSignal
import com.example.zoom.model.User
import com.example.zoom.model.UserProfileSignal
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

object DataRepository {
    private const val TAG = "DataRepository"
    private const val CURRENT_USER_ID = "user001"
    private const val AMBER_CAMPBELL_USER_ID = "user032"
    private const val DEFAULT_CURRENT_MEETING_ID = "mtg016"
    private const val PERSONAL_MEETING_NUMBER = "9948881080"
    private const val TASK4_JOIN_MEETING_NUMBER = "389257198"
    private const val TASK9_SEED_MEETING_TOPIC = "[GUIA] [GUIA-07] Project Sync"
    private const val DEFAULT_MEETING_PASSCODE = "qwjU5X"
    private const val AUTOMATION_TIME_ZONE_ID = "Asia/Shanghai"
    private const val RUNTIME_SCHEMA_VERSION = 1
    private const val AUTOMATION_SEED_MEETING_COUNT = 3
    private const val THREAD_TYPE_DIRECT = "DIRECT"
    private const val THREAD_TYPE_MEETING = "MEETING"

    private data class ActiveScreenShareSession(
        val meetingId: String,
        val shareCode: String
    )

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()

    private var users: List<User> = emptyList()
    private var meetings: List<Meeting> = emptyList()
    private var messages: List<Message> = emptyList()

    private var scheduledMeetingSignals: MutableList<ScheduledMeetingSignal> = mutableListOf()
    private var instantMeetingSessions: MutableList<InstantMeetingSessionSignal> = mutableListOf()
    private var runtimeChatMessages: MutableList<Message> = mutableListOf()
    private var runtimeDirectMessages: MutableList<DirectMessageSignal> = mutableListOf()
    private var runtimeJoinHistoryEntries: MutableList<JoinMeetingHistoryEntry> = mutableListOf()
    private var runtimeJoinHistoryActions: MutableList<JoinMeetingHistoryAction> = mutableListOf()
    private var runtimeMeetingActions: MutableList<MeetingActionSignal> = mutableListOf()
    private var runtimeClipboardActions: MutableList<ClipboardActionSignal> = mutableListOf()
    private var runtimeChatThreadStates: MutableList<ChatThreadStateSignal> = mutableListOf()
    private lateinit var meetingPreferencesSignal: MeetingPreferencesSignal
    private lateinit var profileSignal: UserProfileSignal

    private var activeScreenShareSession: ActiveScreenShareSession? = null
    private lateinit var appContext: Context
    private var initialized = false
    private var currentMeetingId: String = DEFAULT_CURRENT_MEETING_ID
    private val runtimeDataVersion = MutableStateFlow(0)

    fun init(context: Context) {
        if (initialized) return

        appContext = context.applicationContext
        val assets = appContext.assets

        users = gson.fromJson(
            assets.open("data/users.json").bufferedReader(StandardCharsets.UTF_8).use { it.readText() },
            object : TypeToken<List<User>>() {}.type
        )
        meetings = gson.fromJson(
            assets.open("data/meetings.json").bufferedReader(StandardCharsets.UTF_8).use { it.readText() },
            object : TypeToken<List<Meeting>>() {}.type
        )
        messages = gson.fromJson(
            assets.open("data/messages.json").bufferedReader(StandardCharsets.UTF_8).use { it.readText() },
            object : TypeToken<List<Message>>() {}.type
        )

        resetRuntimeSignalData()
        initialized = true
    }

    fun getUsers(): List<User> = users.map(::mergeUserProfile)

    fun getContacts(): List<User> = getUsers().filterNot { it.userId == CURRENT_USER_ID }

    fun getContactCount(): Int = getContacts().size

    fun getMeetings(): List<Meeting> {
        return meetings + scheduledMeetingSignals.map(::signalToMeeting) + instantMeetingSessions.map(::instantSessionToMeeting)
    }

    fun getMessages(): List<Message> = messages + runtimeChatMessages

    fun observeMeetingDataVersion(): StateFlow<Int> = runtimeDataVersion

    fun getScheduledMeetingSignals(): List<ScheduledMeetingSignal> = scheduledMeetingSignals.toList()

    fun getScheduledMeetingSignalById(signalId: String): ScheduledMeetingSignal? {
        return scheduledMeetingSignals.firstOrNull { it.signalId == signalId }
    }

    fun getMeetingActionSignals(): List<MeetingActionSignal> = runtimeMeetingActions.toList()

    fun getJoinHistoryEntries(): List<JoinMeetingHistoryEntry> = runtimeJoinHistoryEntries.toList()

    fun getRuntimeSignalFilePaths(): Map<String, String> = mapOf(
        RuntimeSignalFileNames.RUNTIME_SCHEDULED_MEETINGS to runtimeScheduledMeetingFile().absolutePath,
        RuntimeSignalFileNames.RUNTIME_INSTANT_MEETINGS to runtimeInstantMeetingFile().absolutePath,
        RuntimeSignalFileNames.RUNTIME_CHAT_MESSAGES to runtimeChatMessageFile().absolutePath,
        RuntimeSignalFileNames.RUNTIME_DIRECT_MESSAGES to runtimeDirectMessageFile().absolutePath,
        RuntimeSignalFileNames.RUNTIME_JOIN_HISTORY to runtimeJoinHistoryFile().absolutePath,
        RuntimeSignalFileNames.RUNTIME_MEETING_ACTIONS to runtimeMeetingActionFile().absolutePath,
        RuntimeSignalFileNames.RUNTIME_CLIPBOARD_ACTIONS to runtimeClipboardActionFile().absolutePath,
        RuntimeSignalFileNames.RUNTIME_PROFILE_STATE to runtimeProfileStateFile().absolutePath,
        RuntimeSignalFileNames.RUNTIME_MEETING_PREFERENCES to runtimeMeetingPreferencesFile().absolutePath,
        RuntimeSignalFileNames.RUNTIME_CHAT_THREAD_STATES to runtimeChatThreadStateFile().absolutePath,
        RuntimeSignalFileNames.RUNTIME_META to runtimeMetaFile().absolutePath
    )

    fun getScheduledMeetingSignalFilePath(): String = runtimeScheduledMeetingFile().absolutePath

    fun getCurrentUser(): User = mergeUserProfile(users.first { it.userId == CURRENT_USER_ID })

    fun getUserProfileSignal(): UserProfileSignal = profileSignal

    fun getMeetingPreferencesSignal(): MeetingPreferencesSignal = meetingPreferencesSignal

    fun getChatThreadStates(): List<ChatThreadStateSignal> = runtimeChatThreadStates.toList()

    fun getUnreadDirectThreadCount(): Int {
        return runtimeChatThreadStates.count { it.threadType == THREAD_TYPE_DIRECT && it.unreadCount > 0 }
    }

    fun updateMeetingPreferences(
        autoConnectAudioOn: Boolean = meetingPreferencesSignal.autoConnectAudioOn,
        autoTurnOnCameraOn: Boolean = meetingPreferencesSignal.autoTurnOnCameraOn
    ) {
        meetingPreferencesSignal = meetingPreferencesSignal.copy(
            autoConnectAudioOn = autoConnectAudioOn,
            autoTurnOnCameraOn = autoTurnOnCameraOn,
            updatedAt = System.currentTimeMillis()
        )
        persistMeetingPreferencesSignal()
        bumpRuntimeDataVersion()
    }

    fun updateCurrentUserAvailability(availability: String, statusText: String = profileSignal.statusText) {
        profileSignal = profileSignal.copy(
            availability = availability,
            statusText = statusText,
            updatedAt = System.currentTimeMillis()
        )
        persistProfileSignal()
        bumpRuntimeDataVersion()
    }

    fun updateCurrentUserDisplayName(displayName: String) {
        profileSignal = profileSignal.copy(
            displayName = displayName,
            updatedAt = System.currentTimeMillis()
        )
        persistProfileSignal()
        bumpRuntimeDataVersion()
    }

    fun updateCurrentUserStatusText(statusText: String) {
        profileSignal = profileSignal.copy(
            statusText = statusText,
            updatedAt = System.currentTimeMillis()
        )
        persistProfileSignal()
        bumpRuntimeDataVersion()
    }

    fun getUpcomingMeetings(): List<Meeting> {
        val now = System.currentTimeMillis()
        val staticUpcomingMeetings = meetings.filter { it.startTime > now }
        val runtimeMeetings = scheduledMeetingSignals.map(::signalToMeeting)
        return (staticUpcomingMeetings + runtimeMeetings).sortedBy { it.startTime }
    }

    fun getMeetingsByDate(dateMillis: Long): List<Meeting> {
        val dayStart = dateMillis - (dateMillis % (24 * 60 * 60 * 1000))
        val dayEnd = dayStart + 24 * 60 * 60 * 1000
        return getMeetings().filter { it.startTime in dayStart until dayEnd }
    }

    fun getChatList(): List<Message> {
        return getMessages()
            .groupBy { it.meetingId }
            .mapValues { it.value.maxByOrNull { msg -> msg.timestamp }!! }
            .values
            .sortedByDescending { it.timestamp }
    }

    fun getDirectChatThreads(): List<DirectMessageSignal> {
        return runtimeDirectMessages
            .groupBy { it.partnerUserId }
            .mapValues { it.value.maxByOrNull { signal -> signal.timestamp }!! }
            .values
            .sortedByDescending { it.timestamp }
    }

    fun getDirectMessagesForPartner(partnerUserId: String): List<DirectMessageSignal> {
        return runtimeDirectMessages
            .filter { it.partnerUserId == partnerUserId }
            .sortedBy { it.timestamp }
    }

    fun sendDirectMessage(partnerUserId: String, content: String): DirectMessageSignal {
        val currentUser = getCurrentUser()
        val timestamp = System.currentTimeMillis()
        val threadId = directThreadId(partnerUserId)
        val signal = DirectMessageSignal(
            messageId = "${RuntimeSignalPrefixes.DIRECT_MESSAGE_ID_PREFIX}${timestamp}_${runtimeDirectMessages.size + 1}",
            threadId = threadId,
            partnerUserId = partnerUserId,
            senderId = currentUser.userId,
            senderName = currentUser.username,
            content = content,
            timestamp = timestamp
        )
        runtimeDirectMessages.add(signal)
        upsertChatThreadState(
            threadId = threadId,
            threadType = THREAD_TYPE_DIRECT,
            partnerUserId = partnerUserId,
            lastMessageAt = timestamp,
            outgoing = true
        )
        persistRuntimeDirectMessages()
        persistRuntimeChatThreadStates()
        bumpRuntimeDataVersion()
        return signal
    }

    fun getUserById(userId: String): User? = users.find { it.userId == userId }?.let(::mergeUserProfile)

    fun searchMessages(query: String): List<Message> {
        if (query.isBlank()) return emptyList()
        val q = query.lowercase()
        return getMessages().filter {
            it.content.lowercase().contains(q) || it.senderName.lowercase().contains(q)
        }
    }

    fun searchMeetings(query: String): List<Meeting> {
        if (query.isBlank()) return emptyList()
        val q = query.lowercase().trim()
        val queryDigits = q.filter(Char::isDigit)
        return getMeetings().filter { meeting ->
            val meetingNumber = getMeetingNumber(meeting.meetingId)
            val meetingDigits = meetingNumber.filter(Char::isDigit)
            meeting.topic.lowercase().contains(q) ||
                meetingNumber.lowercase().contains(q) ||
                (queryDigits.isNotEmpty() && meetingDigits.contains(queryDigits))
        }
    }

    fun searchUsers(query: String): List<User> {
        if (query.isBlank()) return emptyList()
        val q = query.lowercase()
        return getUsers().filter {
            it.username.lowercase().contains(q) ||
                (it.email?.lowercase()?.contains(q) == true)
        }
    }

    fun searchChats(query: String): List<Message> {
        if (query.isBlank()) return emptyList()
        val q = query.lowercase()
        val chatList = getChatList()
        return chatList.filter { msg ->
            val meeting = getMeetingById(msg.meetingId)
            meeting?.topic?.lowercase()?.contains(q) == true ||
                msg.content.lowercase().contains(q)
        }
    }

    fun getMeetingById(meetingId: String): Meeting? {
        return meetings.find { it.meetingId == meetingId }
            ?: scheduledMeetingSignals.firstOrNull { it.signalId == meetingId }?.let(::signalToMeeting)
            ?: instantMeetingSessions.firstOrNull { it.signalId == meetingId }?.let(::instantSessionToMeeting)
    }

    fun getMeetingNumber(meetingId: String): String {
        instantMeetingSessions.firstOrNull { it.signalId == meetingId }?.let { return it.meetingNumber }
        scheduledMeetingSignals.firstOrNull { it.signalId == meetingId }?.let {
            return if (it.usePersonalMeetingId) PERSONAL_MEETING_NUMBER else it.meetingNumber
        }
        return meetingId.filter(Char::isDigit).ifBlank { meetingId }
    }

    fun getMeetingPasscode(meetingId: String): String {
        instantMeetingSessions.firstOrNull { it.signalId == meetingId }?.let { return it.passcode }
        scheduledMeetingSignals.firstOrNull { it.signalId == meetingId }?.let { return it.passcode }
        return DEFAULT_MEETING_PASSCODE
    }

    fun getMeetingInviteLink(meetingId: String): String {
        return "https://zoom.us/j/${getMeetingNumber(meetingId)}"
    }

    fun getMessagesByMeetingId(meetingId: String): List<Message> {
        return getMessages().filter { it.meetingId == meetingId }.sortedBy { it.timestamp }
    }

    fun setCurrentMeeting(meetingId: String?) {
        val normalized = meetingId?.takeIf { it.isNotBlank() } ?: DEFAULT_CURRENT_MEETING_ID
        val resolved = getMeetingById(normalized)?.meetingId
        currentMeetingId = resolved ?: DEFAULT_CURRENT_MEETING_ID
    }

    fun getCurrentMeeting(): Meeting {
        return getMeetingById(currentMeetingId)
            ?: meetings.firstOrNull { it.meetingId == DEFAULT_CURRENT_MEETING_ID }
            ?: getMeetings().first()
    }

    fun getParticipantsForMeeting(meetingId: String): List<User> {
        val baseParticipantIds = when {
            instantMeetingSessions.any { it.signalId == meetingId } -> {
                instantMeetingSessions.first { it.signalId == meetingId }.participantIds
            }
            else -> getMeetingById(meetingId)?.participantIds.orEmpty()
        }
        val invitedParticipantIds = runtimeMeetingActions
            .filter { it.meetingId == meetingId && it.actionType == MeetingActionTypes.INVITE_CONTACTS }
            .flatMap { it.targetUserIds }

        return (baseParticipantIds + invitedParticipantIds)
            .distinct()
            .mapNotNull(::getUserById)
    }

    fun prepareHostMeetingSession(
        usePersonalMeetingId: Boolean,
        videoOn: Boolean,
        topic: String? = null,
        waitingRoomEnabled: Boolean = false,
        allowJoinBeforeHost: Boolean = true
    ): String {
        val currentUser = getCurrentUser()
        val createdAt = System.currentTimeMillis()
        val meetingNumber = if (usePersonalMeetingId) {
            PERSONAL_MEETING_NUMBER
        } else {
            generateMeetingNumber(createdAt, instantMeetingSessions.size + if (videoOn) 1 else 0)
        }
        val resolvedTopic = topic?.trim().takeUnless { it.isNullOrBlank() }
            ?: "${currentUser.username}'s Zoom Meeting"
        val signal = InstantMeetingSessionSignal(
            signalId = "${RuntimeSignalPrefixes.INSTANT_MEETING_ID_PREFIX}${createdAt}_${instantMeetingSessions.size + 1}",
            meetingNumber = meetingNumber,
            topic = resolvedTopic,
            source = "HOST",
            participantIds = listOf(currentUser.userId),
            passcode = DEFAULT_MEETING_PASSCODE,
            waitingRoomEnabled = waitingRoomEnabled,
            allowJoinBeforeHost = allowJoinBeforeHost,
            usePersonalMeetingId = usePersonalMeetingId,
            createdAt = createdAt
        )
        instantMeetingSessions.add(signal)
        currentMeetingId = signal.signalId
        persistRuntimeInstantMeetings()
        bumpRuntimeDataVersion()
        return signal.signalId
    }

    fun prepareJoinMeetingSession(meetingNumber: String, title: String? = null): String {
        val currentUser = getCurrentUser()
        val createdAt = System.currentTimeMillis()
        val defaultFallbackParticipantIds = getContacts().map { it.userId }
        val fallbackParticipantIds = if (meetingNumber == TASK4_JOIN_MEETING_NUMBER) {
            // Ensure task 4's target participant can appear in the meeting member list.
            (listOf(AMBER_CAMPBELL_USER_ID) + defaultFallbackParticipantIds)
                .distinct()
                .take(5)
        } else {
            defaultFallbackParticipantIds.take(5)
        }
        val signal = InstantMeetingSessionSignal(
            signalId = "${RuntimeSignalPrefixes.INSTANT_MEETING_ID_PREFIX}${createdAt}_${instantMeetingSessions.size + 1}",
            meetingNumber = meetingNumber,
            topic = title ?: "Zoom Meeting $meetingNumber",
            source = "JOIN",
            participantIds = listOf(currentUser.userId) + fallbackParticipantIds,
            passcode = DEFAULT_MEETING_PASSCODE,
            usePersonalMeetingId = false,
            createdAt = createdAt
        )
        instantMeetingSessions.add(signal)
        currentMeetingId = signal.signalId
        persistRuntimeInstantMeetings()
        bumpRuntimeDataVersion()
        return signal.signalId
    }

    fun addScheduledMeetingSignal(
        topic: String,
        startTime: Long,
        durationMinutes: Int,
        timeZoneId: String,
        repeat: String,
        calendar: String,
        encryption: String,
        inviteeUserIds: List<String>,
        passcode: String,
        waitingRoomEnabled: Boolean,
        allowJoinBeforeHost: Boolean = true,
        hostVideoOn: Boolean = true,
        participantVideoOn: Boolean = true,
        usePersonalMeetingId: Boolean
    ): ScheduledMeetingSignal {
        val createdAt = System.currentTimeMillis()
        val signal = ScheduledMeetingSignal(
            signalId = "${RuntimeSignalPrefixes.RUNTIME_MEETING_ID_PREFIX}${createdAt}_${scheduledMeetingSignals.size + 1}",
            meetingNumber = if (usePersonalMeetingId) PERSONAL_MEETING_NUMBER else generateMeetingNumber(createdAt, scheduledMeetingSignals.size + 1),
            topic = topic,
            startTime = startTime,
            durationMinutes = durationMinutes,
            timeZoneId = timeZoneId,
            repeat = repeat,
            calendar = calendar,
            encryption = encryption,
            inviteeUserIds = inviteeUserIds.distinct(),
            passcode = passcode,
            waitingRoomEnabled = waitingRoomEnabled,
            allowJoinBeforeHost = allowJoinBeforeHost,
            hostVideoOn = hostVideoOn,
            participantVideoOn = participantVideoOn,
            usePersonalMeetingId = usePersonalMeetingId,
            createdAt = createdAt
        )
        scheduledMeetingSignals.add(signal)
        persistRuntimeScheduledMeetingSignals()
        bumpRuntimeDataVersion()
        return signal
    }

    fun updateScheduledMeetingSignal(
        signalId: String,
        topic: String,
        startTime: Long,
        durationMinutes: Int,
        timeZoneId: String,
        repeat: String,
        calendar: String,
        encryption: String,
        inviteeUserIds: List<String>,
        passcode: String,
        waitingRoomEnabled: Boolean,
        allowJoinBeforeHost: Boolean = true,
        hostVideoOn: Boolean = true,
        participantVideoOn: Boolean = true,
        usePersonalMeetingId: Boolean
    ): ScheduledMeetingSignal? {
        val index = scheduledMeetingSignals.indexOfFirst { it.signalId == signalId }
        if (index < 0) return null
        val original = scheduledMeetingSignals[index]
        val updatedSignal = original.copy(
            topic = topic,
            startTime = startTime,
            durationMinutes = durationMinutes,
            timeZoneId = timeZoneId,
            repeat = repeat,
            calendar = calendar,
            encryption = encryption,
            inviteeUserIds = inviteeUserIds.distinct(),
            passcode = passcode,
            waitingRoomEnabled = waitingRoomEnabled,
            allowJoinBeforeHost = allowJoinBeforeHost,
            hostVideoOn = hostVideoOn,
            participantVideoOn = participantVideoOn,
            usePersonalMeetingId = usePersonalMeetingId,
            meetingNumber = if (usePersonalMeetingId) PERSONAL_MEETING_NUMBER else original.meetingNumber
        )
        scheduledMeetingSignals[index] = updatedSignal
        persistRuntimeScheduledMeetingSignals()
        bumpRuntimeDataVersion()
        return updatedSignal
    }

    fun updateScheduledMeetingInvitees(
        signalId: String,
        inviteeUserIds: List<String>
    ): ScheduledMeetingSignal? {
        val index = scheduledMeetingSignals.indexOfFirst { it.signalId == signalId }
        if (index < 0) return null
        val updatedSignal = scheduledMeetingSignals[index].copy(
            inviteeUserIds = inviteeUserIds.distinct()
        )
        scheduledMeetingSignals[index] = updatedSignal
        persistRuntimeScheduledMeetingSignals()
        bumpRuntimeDataVersion()
        return updatedSignal
    }

    fun cancelScheduledMeeting(signalId: String): Boolean {
        val removedMeeting = scheduledMeetingSignals.removeAll { it.signalId == signalId }
        if (!removedMeeting) return false
        persistRuntimeScheduledMeetingSignals()
        bumpRuntimeDataVersion()
        return true
    }

    fun addRuntimeChatMessage(meetingId: String, content: String): Message {
        val currentUser = getCurrentUser()
        val timestamp = System.currentTimeMillis()
        val threadId = "meeting_$meetingId"
        val message = Message(
            messageId = "${RuntimeSignalPrefixes.RUNTIME_MESSAGE_ID_PREFIX}${timestamp}_${runtimeChatMessages.size + 1}",
            meetingId = meetingId,
            senderId = currentUser.userId,
            senderName = currentUser.username,
            content = content,
            timestamp = timestamp
        )
        runtimeChatMessages.add(message)
        upsertChatThreadState(
            threadId = threadId,
            threadType = THREAD_TYPE_MEETING,
            meetingId = meetingId,
            lastMessageAt = timestamp,
            outgoing = true
        )
        persistRuntimeChatMessages()
        persistRuntimeChatThreadStates()
        bumpRuntimeDataVersion()
        return message
    }

    fun recordJoinHistoryUsed(meetingNumber: String, title: String? = null) {
        val now = System.currentTimeMillis()
        val existingIndex = runtimeJoinHistoryEntries.indexOfFirst { it.meetingNumber == meetingNumber }
        val resolvedTitle = when {
            !title.isNullOrBlank() -> title
            existingIndex >= 0 -> runtimeJoinHistoryEntries[existingIndex].title
            else -> "${getCurrentUser().username}'s Zoom Meeting"
        }

        val updatedEntry = JoinMeetingHistoryEntry(
            title = resolvedTitle,
            meetingNumber = meetingNumber,
            lastUsedAt = now
        )

        if (existingIndex >= 0) {
            runtimeJoinHistoryEntries.removeAt(existingIndex)
        }
        runtimeJoinHistoryEntries.add(0, updatedEntry)

        runtimeJoinHistoryActions.add(
            JoinMeetingHistoryAction(
                actionId = "${RuntimeSignalPrefixes.JOIN_HISTORY_ACTION_ID_PREFIX}${now}_${runtimeJoinHistoryActions.size + 1}",
                actionType = JoinHistoryActionTypes.USED,
                meetingNumber = meetingNumber,
                title = resolvedTitle,
                occurredAt = now
            )
        )

        persistRuntimeJoinHistorySignal()
        bumpRuntimeDataVersion()
    }

    fun clearJoinHistoryEntries() {
        runtimeJoinHistoryEntries.clear()
        val now = System.currentTimeMillis()
        runtimeJoinHistoryActions.add(
            JoinMeetingHistoryAction(
                actionId = "${RuntimeSignalPrefixes.JOIN_HISTORY_ACTION_ID_PREFIX}${now}_${runtimeJoinHistoryActions.size + 1}",
                actionType = JoinHistoryActionTypes.CLEARED,
                occurredAt = now
            )
        )
        persistRuntimeJoinHistorySignal()
        bumpRuntimeDataVersion()
    }

    fun inviteContactsToMeeting(meetingId: String, selectedContactIds: Set<String>) {
        if (selectedContactIds.isEmpty()) {
            recordMeetingAction(
                actionType = MeetingActionTypes.INVITE_CONTACTS,
                meetingId = meetingId,
                note = "No contacts selected"
            )
            return
        }
        val instantIndex = instantMeetingSessions.indexOfFirst { it.signalId == meetingId }
        if (instantIndex >= 0) {
            val session = instantMeetingSessions[instantIndex]
            instantMeetingSessions[instantIndex] = session.copy(
                participantIds = (session.participantIds + selectedContactIds).distinct()
            )
            persistRuntimeInstantMeetings()
        }
        recordMeetingAction(
            actionType = MeetingActionTypes.INVITE_CONTACTS,
            meetingId = meetingId,
            targetUserIds = selectedContactIds.toList(),
            note = "Selected contacts: ${selectedContactIds.joinToString(",")}" 
        )
        bumpRuntimeDataVersion()
    }

    fun recordCurrentMeetingStarted(
        microphoneOn: Boolean,
        cameraOn: Boolean,
        audioOption: String
    ): MeetingActionSignal {
        val meetingId = getCurrentMeeting().meetingId
        return recordMeetingAction(
            actionType = MeetingActionTypes.MEETING_STARTED,
            meetingId = meetingId,
            note = buildMeetingLifecycleNote(meetingId),
            microphoneOn = microphoneOn,
            cameraOn = cameraOn,
            audioOption = audioOption
        )
    }

    fun recordCurrentMeetingExited(exitAction: String): MeetingActionSignal {
        val meetingId = getCurrentMeeting().meetingId
        return recordMeetingAction(
            actionType = MeetingActionTypes.MEETING_EXITED,
            meetingId = meetingId,
            note = buildMeetingLifecycleNote(meetingId),
            exitAction = exitAction
        )
    }

    fun recordCurrentMeetingMediaStateChanged(
        microphoneOn: Boolean,
        cameraOn: Boolean,
        audioOption: String,
        mediaChangeSource: String
    ): MeetingActionSignal {
        val meetingId = getCurrentMeeting().meetingId
        return recordMeetingAction(
            actionType = MeetingActionTypes.MEETING_MEDIA_STATE_CHANGED,
            meetingId = meetingId,
            note = buildMeetingLifecycleNote(meetingId),
            microphoneOn = microphoneOn,
            cameraOn = cameraOn,
            audioOption = audioOption,
            mediaChangeSource = mediaChangeSource
        )
    }

    fun recordMeetingAction(
        actionType: String,
        meetingId: String,
        targetUserIds: List<String> = emptyList(),
        note: String = "",
        emoji: String = "",
        microphoneOn: Boolean? = null,
        cameraOn: Boolean? = null,
        audioOption: String = "",
        exitAction: String = "",
        mediaChangeSource: String = "",
        screenSharingEnabled: Boolean? = null,
        shareCode: String = ""
    ): MeetingActionSignal {
        val timestamp = System.currentTimeMillis()
        val action = MeetingActionSignal(
            actionId = "${RuntimeSignalPrefixes.MEETING_ACTION_ID_PREFIX}${timestamp}_${runtimeMeetingActions.size + 1}",
            meetingId = meetingId,
            meetingNumber = getMeetingNumber(meetingId),
            actionType = actionType,
            targetUserIds = targetUserIds,
            note = note,
            emoji = emoji,
            microphoneOn = microphoneOn,
            cameraOn = cameraOn,
            audioOption = audioOption,
            exitAction = exitAction,
            mediaChangeSource = mediaChangeSource,
            screenSharingEnabled = screenSharingEnabled,
            shareCode = shareCode,
            occurredAt = timestamp
        )
        runtimeMeetingActions.add(action)
        persistRuntimeMeetingActions()
        bumpRuntimeDataVersion()
        return action
    }

    fun recordClipboardAction(
        type: String,
        meetingId: String,
        text: String
    ): ClipboardActionSignal {
        val timestamp = System.currentTimeMillis()
        val action = ClipboardActionSignal(
            type = type,
            meetingId = meetingId,
            meetingNumber = getMeetingNumber(meetingId),
            text = text,
            createdAt = timestamp
        )
        runtimeClipboardActions.add(action)
        if (runtimeClipboardActions.size > 100) {
            runtimeClipboardActions = runtimeClipboardActions.takeLast(100).toMutableList()
        }
        persistRuntimeClipboardActions()
        bumpRuntimeDataVersion()
        return action
    }

    fun recordCopiedInviteLink(meetingId: String, inviteLink: String = getMeetingInviteLink(meetingId)): MeetingActionSignal {
        recordClipboardAction(
            type = MeetingActionTypes.COPY_INVITE_LINK,
            meetingId = meetingId,
            text = inviteLink
        )
        return recordMeetingAction(
            actionType = MeetingActionTypes.COPY_INVITE_LINK,
            meetingId = meetingId,
            note = inviteLink
        )
    }

    fun setCurrentMeetingScreenShareEnabled(enabled: Boolean, note: String = "Share toggled from meeting") {
        val meetingId = getCurrentMeeting().meetingId
        if (enabled) {
            activeScreenShareSession = ActiveScreenShareSession(
                meetingId = meetingId,
                shareCode = activeScreenShareSession?.shareCode.orEmpty()
            )
        } else if (activeScreenShareSession?.meetingId == meetingId) {
            activeScreenShareSession = null
        }
        recordMeetingAction(
            actionType = MeetingActionTypes.SCREEN_SHARE_STATUS_CHANGED,
            meetingId = meetingId,
            note = note,
            screenSharingEnabled = enabled,
            shareCode = activeScreenShareSession?.shareCode.orEmpty()
        )
    }

    fun startShareScreenSession(shareCode: String): MeetingActionSignal {
        val normalizedShareCode = shareCode.filter { it.isDigit() }.take(8)
        stopActiveScreenShareSession(note = "Replaced by a new Share Page session")
        val meetingId = prepareHostMeetingSession(usePersonalMeetingId = false, videoOn = false)
        activeScreenShareSession = ActiveScreenShareSession(
            meetingId = meetingId,
            shareCode = normalizedShareCode
        )
        return recordMeetingAction(
            actionType = MeetingActionTypes.SCREEN_SHARE_STATUS_CHANGED,
            meetingId = meetingId,
            note = "Screen share started from Share Page",
            screenSharingEnabled = true,
            shareCode = normalizedShareCode
        )
    }

    fun stopCurrentScreenShareSessionIfActive(): MeetingActionSignal? {
        val activeSession = activeScreenShareSession ?: return null
        val currentMeetingId = getCurrentMeeting().meetingId
        if (activeSession.meetingId != currentMeetingId) return null
        activeScreenShareSession = null
        return recordMeetingAction(
            actionType = MeetingActionTypes.SCREEN_SHARE_STATUS_CHANGED,
            meetingId = activeSession.meetingId,
            note = "Screen share stopped after leaving the meeting",
            screenSharingEnabled = false,
            shareCode = activeSession.shareCode
        )
    }

    fun isRuntimeScheduledMeeting(meetingId: String): Boolean {
        return meetingId.startsWith(RuntimeSignalPrefixes.RUNTIME_MEETING_ID_PREFIX)
    }

    private fun resetRuntimeSignalData() {
        scheduledMeetingSignals = buildAutomationSeedScheduledMeetings()
        instantMeetingSessions = mutableListOf()
        runtimeChatMessages = mutableListOf()
        runtimeDirectMessages = mutableListOf()
        runtimeChatThreadStates = mutableListOf()
        runtimeJoinHistoryEntries = defaultJoinHistoryEntries()
        runtimeJoinHistoryActions = mutableListOf()
        runtimeMeetingActions = mutableListOf()
        runtimeClipboardActions = mutableListOf()
        meetingPreferencesSignal = MeetingPreferencesSignal(
            autoConnectAudioOn = true,
            autoTurnOnCameraOn = false,
            updatedAt = System.currentTimeMillis()
        )
        profileSignal = UserProfileSignal(
            displayName = currentUserAsset().username,
            availability = "Available",
            statusText = "What is your status?",
            updatedAt = System.currentTimeMillis()
        )
        activeScreenShareSession = null
        currentMeetingId = DEFAULT_CURRENT_MEETING_ID

        persistRuntimeScheduledMeetingSignals()
        persistRuntimeInstantMeetings()
        persistRuntimeChatMessages()
        persistRuntimeDirectMessages()
        persistRuntimeChatThreadStates()
        persistRuntimeJoinHistorySignal()
        persistRuntimeMeetingActions()
        persistRuntimeClipboardActions()
        persistMeetingPreferencesSignal()
        persistProfileSignal()
        persistRuntimeMeta()
        bumpRuntimeDataVersion()
    }

    private fun defaultJoinHistoryEntries(): MutableList<JoinMeetingHistoryEntry> {
        return mutableListOf(
            JoinMeetingHistoryEntry(
                title = "CL L's Zoom Meeting",
                meetingNumber = "994888108",
                lastUsedAt = null
            ),
            JoinMeetingHistoryEntry(
                title = "CL L's Zoom Meeting",
                meetingNumber = "820112935",
                lastUsedAt = null
            ),
            JoinMeetingHistoryEntry(
                title = "CL L's Zoom Meeting",
                meetingNumber = "867558037",
                lastUsedAt = null
            ),
            JoinMeetingHistoryEntry(
                title = "CL L's Zoom Meeting",
                meetingNumber = "834821295",
                lastUsedAt = null
            )
        )
    }

    private fun buildAutomationSeedScheduledMeetings(): MutableList<ScheduledMeetingSignal> {
        val zoneId = if (ZoneId.getAvailableZoneIds().contains(AUTOMATION_TIME_ZONE_ID)) {
            ZoneId.of(AUTOMATION_TIME_ZONE_ID)
        } else {
            ZoneId.systemDefault()
        }
        val now = ZonedDateTime.now(zoneId)
        val tomorrow = now.toLocalDate().plusDays(1)
        val nextMayFirst = nextUpcomingMayFirst(now.toLocalDate())
        val baseCreatedAt = System.currentTimeMillis()

        val seeds = listOf(
            buildAutomationSeedMeeting(
                index = 1,
                topic = "Daily Standup",
                startDate = tomorrow,
                startTime = LocalTime.of(8, 0),
                durationMinutes = 30,
                createdAt = baseCreatedAt,
                zoneId = zoneId
            ),
            buildAutomationSeedMeeting(
                index = 2,
                topic = "Lunch Sync",
                startDate = tomorrow,
                startTime = LocalTime.of(12, 0),
                durationMinutes = 60,
                createdAt = baseCreatedAt,
                zoneId = zoneId
            ),
            buildAutomationSeedMeeting(
                index = 3,
                topic = TASK9_SEED_MEETING_TOPIC,
                startDate = nextMayFirst,
                startTime = LocalTime.of(9, 0),
                durationMinutes = 45,
                createdAt = baseCreatedAt,
                zoneId = zoneId
            )
        )

        return seeds.toMutableList()
    }

    private fun buildAutomationSeedMeeting(
        index: Int,
        topic: String,
        startDate: LocalDate,
        startTime: LocalTime,
        durationMinutes: Int,
        createdAt: Long,
        zoneId: ZoneId
    ): ScheduledMeetingSignal {
        val startDateTime = ZonedDateTime.of(startDate, startTime, zoneId)
        val signalCreatedAt = createdAt + index
        return ScheduledMeetingSignal(
            signalId = "${RuntimeSignalPrefixes.RUNTIME_MEETING_ID_PREFIX}${signalCreatedAt}_seed_$index",
            meetingNumber = generateMeetingNumber(signalCreatedAt, index),
            topic = topic,
            startTime = startDateTime.toInstant().toEpochMilli(),
            durationMinutes = durationMinutes,
            timeZoneId = zoneId.id,
            repeat = "None",
            calendar = "iCalendar",
            encryption = "Enhanced",
            inviteeUserIds = emptyList(),
            passcode = DEFAULT_MEETING_PASSCODE,
            waitingRoomEnabled = false,
            allowJoinBeforeHost = true,
            hostVideoOn = true,
            participantVideoOn = true,
            usePersonalMeetingId = false,
            createdAt = signalCreatedAt
        )
    }

    private fun nextUpcomingMayFirst(today: LocalDate): LocalDate {
        val currentYearCandidate = LocalDate.of(today.year, 5, 1)
        return if (!currentYearCandidate.isBefore(today)) {
            currentYearCandidate
        } else {
            LocalDate.of(today.year + 1, 5, 1)
        }
    }

    private fun signalToMeeting(signal: ScheduledMeetingSignal): Meeting {
        val currentUserId = getCurrentUser().userId
        val participantIds = listOf(currentUserId) + signal.inviteeUserIds
        val endTime = signal.startTime + signal.durationMinutes * 60_000L
        return Meeting(
            meetingId = signal.signalId,
            topic = signal.topic,
            startTime = signal.startTime,
            endTime = endTime,
            participantIds = participantIds.distinct()
        )
    }

    private fun instantSessionToMeeting(signal: InstantMeetingSessionSignal): Meeting {
        return Meeting(
            meetingId = signal.signalId,
            topic = signal.topic,
            startTime = signal.createdAt,
            endTime = null,
            participantIds = signal.participantIds.distinct()
        )
    }

    private fun mergeUserProfile(user: User): User {
        return if (user.userId == CURRENT_USER_ID) {
            user.copy(username = profileSignal.displayName)
        } else {
            user
        }
    }

    private fun currentUserAsset(): User = users.first { it.userId == CURRENT_USER_ID }

    private fun persistRuntimeScheduledMeetingSignals() {
        persistJson(
            file = runtimeScheduledMeetingFile(),
            payload = scheduledMeetingSignals,
            label = RuntimeSignalFileNames.RUNTIME_SCHEDULED_MEETINGS
        )
    }

    private fun persistRuntimeInstantMeetings() {
        persistJson(
            file = runtimeInstantMeetingFile(),
            payload = instantMeetingSessions,
            label = RuntimeSignalFileNames.RUNTIME_INSTANT_MEETINGS
        )
    }

    private fun persistRuntimeChatMessages() {
        persistJson(
            file = runtimeChatMessageFile(),
            payload = runtimeChatMessages,
            label = RuntimeSignalFileNames.RUNTIME_CHAT_MESSAGES
        )
    }

    private fun persistRuntimeDirectMessages() {
        persistJson(
            file = runtimeDirectMessageFile(),
            payload = runtimeDirectMessages,
            label = RuntimeSignalFileNames.RUNTIME_DIRECT_MESSAGES
        )
    }

    private fun persistRuntimeChatThreadStates() {
        persistJson(
            file = runtimeChatThreadStateFile(),
            payload = runtimeChatThreadStates,
            label = RuntimeSignalFileNames.RUNTIME_CHAT_THREAD_STATES
        )
    }

    private fun persistRuntimeJoinHistorySignal() {
        val payload = JoinMeetingHistorySignal(
            entries = runtimeJoinHistoryEntries,
            actions = runtimeJoinHistoryActions
        )
        persistJson(
            file = runtimeJoinHistoryFile(),
            payload = payload,
            label = RuntimeSignalFileNames.RUNTIME_JOIN_HISTORY
        )
    }

    private fun persistRuntimeMeetingActions() {
        persistJson(
            file = runtimeMeetingActionFile(),
            payload = runtimeMeetingActions,
            label = RuntimeSignalFileNames.RUNTIME_MEETING_ACTIONS
        )
    }

    private fun persistRuntimeClipboardActions() {
        persistJson(
            file = runtimeClipboardActionFile(),
            payload = runtimeClipboardActions,
            label = RuntimeSignalFileNames.RUNTIME_CLIPBOARD_ACTIONS
        )
    }

    private fun persistProfileSignal() {
        persistJson(
            file = runtimeProfileStateFile(),
            payload = profileSignal,
            label = RuntimeSignalFileNames.RUNTIME_PROFILE_STATE
        )
    }

    private fun persistMeetingPreferencesSignal() {
        persistJson(
            file = runtimeMeetingPreferencesFile(),
            payload = meetingPreferencesSignal,
            label = RuntimeSignalFileNames.RUNTIME_MEETING_PREFERENCES
        )
    }

    private fun persistRuntimeMeta() {
        persistJson(
            file = runtimeMetaFile(),
            payload = RuntimeSignalMeta(
                schemaVersion = RUNTIME_SCHEMA_VERSION,
                seedScheduledMeetingCount = AUTOMATION_SEED_MEETING_COUNT,
                contactCount = getContacts().size,
                generatedAt = System.currentTimeMillis()
            ),
            label = RuntimeSignalFileNames.RUNTIME_META
        )
    }

    private fun persistJson(file: File, payload: Any, label: String) {
        try {
            file.parentFile?.mkdirs()
            file.writeText(gson.toJson(payload), Charsets.UTF_8)
            Log.d(TAG, "Persisted $label to ${file.absolutePath}")
        } catch (error: Exception) {
            Log.e(TAG, "Failed to persist $label to ${file.absolutePath}", error)
            throw IllegalStateException("Failed to persist $label", error)
        }
    }

    private fun stopActiveScreenShareSession(note: String): MeetingActionSignal? {
        val activeSession = activeScreenShareSession ?: return null
        activeScreenShareSession = null
        return recordMeetingAction(
            actionType = MeetingActionTypes.SCREEN_SHARE_STATUS_CHANGED,
            meetingId = activeSession.meetingId,
            note = note,
            screenSharingEnabled = false,
            shareCode = activeSession.shareCode
        )
    }

    private fun buildMeetingLifecycleNote(meetingId: String): String {
        instantMeetingSessions.firstOrNull { it.signalId == meetingId }?.let { session ->
            return "Instant meeting source=${session.source}"
        }
        scheduledMeetingSignals.firstOrNull { it.signalId == meetingId }?.let {
            return "Scheduled meeting"
        }
        return "Static meeting"
    }

    private fun generateMeetingNumber(seed: Long, index: Int): String {
        val normalized = ((seed % 900_000_000L) + 100_000_000L + index).toString()
        return normalized.takeLast(9)
    }

    private fun upsertChatThreadState(
        threadId: String,
        threadType: String,
        partnerUserId: String = "",
        meetingId: String = "",
        lastMessageAt: Long,
        outgoing: Boolean
    ) {
        val index = runtimeChatThreadStates.indexOfFirst { it.threadId == threadId }
        if (index >= 0) {
            val original = runtimeChatThreadStates[index]
            runtimeChatThreadStates[index] = original.copy(
                unreadCount = if (outgoing) original.unreadCount else original.unreadCount + 1,
                lastMessageAt = lastMessageAt
            )
            return
        }
        runtimeChatThreadStates.add(
            ChatThreadStateSignal(
                threadId = threadId,
                threadType = threadType,
                partnerUserId = partnerUserId,
                meetingId = meetingId,
                unreadCount = if (outgoing) 0 else 1,
                lastMessageAt = lastMessageAt
            )
        )
    }

    private fun directThreadId(partnerUserId: String): String = "direct_$partnerUserId"

    private fun runtimeScheduledMeetingFile(): File {
        return File(appContext.filesDir, RuntimeSignalFileNames.RUNTIME_SCHEDULED_MEETINGS)
    }

    private fun runtimeInstantMeetingFile(): File {
        return File(appContext.filesDir, RuntimeSignalFileNames.RUNTIME_INSTANT_MEETINGS)
    }

    private fun runtimeChatMessageFile(): File {
        return File(appContext.filesDir, RuntimeSignalFileNames.RUNTIME_CHAT_MESSAGES)
    }

    private fun runtimeDirectMessageFile(): File {
        return File(appContext.filesDir, RuntimeSignalFileNames.RUNTIME_DIRECT_MESSAGES)
    }

    private fun runtimeChatThreadStateFile(): File {
        return File(appContext.filesDir, RuntimeSignalFileNames.RUNTIME_CHAT_THREAD_STATES)
    }

    private fun runtimeJoinHistoryFile(): File {
        return File(appContext.filesDir, RuntimeSignalFileNames.RUNTIME_JOIN_HISTORY)
    }

    private fun runtimeMeetingActionFile(): File {
        return File(appContext.filesDir, RuntimeSignalFileNames.RUNTIME_MEETING_ACTIONS)
    }

    private fun runtimeClipboardActionFile(): File {
        return File(appContext.filesDir, RuntimeSignalFileNames.RUNTIME_CLIPBOARD_ACTIONS)
    }

    private fun runtimeProfileStateFile(): File {
        return File(appContext.filesDir, RuntimeSignalFileNames.RUNTIME_PROFILE_STATE)
    }

    private fun runtimeMeetingPreferencesFile(): File {
        return File(appContext.filesDir, RuntimeSignalFileNames.RUNTIME_MEETING_PREFERENCES)
    }

    private fun runtimeMetaFile(): File {
        return File(appContext.filesDir, RuntimeSignalFileNames.RUNTIME_META)
    }

    private fun bumpRuntimeDataVersion() {
        runtimeDataVersion.value = runtimeDataVersion.value + 1
    }
}
