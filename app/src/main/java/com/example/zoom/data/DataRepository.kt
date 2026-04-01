package com.example.zoom.data

import android.content.Context
import com.example.zoom.common.constants.JoinHistoryActionTypes
import com.example.zoom.common.constants.MeetingActionTypes
import com.example.zoom.common.constants.RuntimeSignalFileNames
import com.example.zoom.common.constants.RuntimeSignalPrefixes
import com.example.zoom.model.JoinMeetingHistoryAction
import com.example.zoom.model.JoinMeetingHistoryEntry
import com.example.zoom.model.JoinMeetingHistorySignal
import com.example.zoom.model.Meeting
import com.example.zoom.model.MeetingActionSignal
import com.example.zoom.model.Message
import com.example.zoom.model.ScheduledMeetingSignal
import com.example.zoom.model.User
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.nio.charset.StandardCharsets

object DataRepository {
    private const val DEFAULT_CURRENT_MEETING_ID = "mtg016"

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
    private var runtimeChatMessages: MutableList<Message> = mutableListOf()
    private var runtimeJoinHistoryEntries: MutableList<JoinMeetingHistoryEntry> = mutableListOf()
    private var runtimeJoinHistoryActions: MutableList<JoinMeetingHistoryAction> = mutableListOf()
    private var runtimeMeetingActions: MutableList<MeetingActionSignal> = mutableListOf()
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

    fun getUsers(): List<User> = users

    fun getMeetings(): List<Meeting> = meetings + scheduledMeetingSignals.map { signalToMeeting(it) }

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
        RuntimeSignalFileNames.RUNTIME_CHAT_MESSAGES to runtimeChatMessageFile().absolutePath,
        RuntimeSignalFileNames.RUNTIME_JOIN_HISTORY to runtimeJoinHistoryFile().absolutePath,
        RuntimeSignalFileNames.RUNTIME_MEETING_ACTIONS to runtimeMeetingActionFile().absolutePath
    )

    fun getScheduledMeetingSignalFilePath(): String = runtimeScheduledMeetingFile().absolutePath

    fun getCurrentUser(): User = users.first { it.userId == "user001" }

    fun getUpcomingMeetings(): List<Meeting> {
        val now = System.currentTimeMillis()
        val staticUpcomingMeetings = meetings.filter { it.startTime > now }
        val runtimeMeetings = scheduledMeetingSignals.map { signalToMeeting(it) }
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

    fun getUserById(userId: String): User? = users.find { it.userId == userId }

    fun searchMessages(query: String): List<Message> {
        if (query.isBlank()) return emptyList()
        val q = query.lowercase()
        return getMessages().filter {
            it.content.lowercase().contains(q) || it.senderName.lowercase().contains(q)
        }
    }

    fun searchMeetings(query: String): List<Meeting> {
        if (query.isBlank()) return emptyList()
        val q = query.lowercase()
        return getMeetings().filter { it.topic.lowercase().contains(q) }
    }

    fun searchUsers(query: String): List<User> {
        if (query.isBlank()) return emptyList()
        val q = query.lowercase()
        return users.filter {
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
            ?: scheduledMeetingSignals.firstOrNull { it.signalId == meetingId }?.let { signalToMeeting(it) }
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
        val meeting = getMeetingById(meetingId) ?: return emptyList()
        return meeting.participantIds.mapNotNull { getUserById(it) }
    }

    fun addScheduledMeetingSignal(
        topic: String,
        startTime: Long,
        durationMinutes: Int,
        timeZoneId: String,
        repeat: String,
        calendar: String,
        encryption: String,
        inviteeUserIds: List<String>
    ): ScheduledMeetingSignal {
        val signal = ScheduledMeetingSignal(
            signalId = "${RuntimeSignalPrefixes.RUNTIME_MEETING_ID_PREFIX}${System.currentTimeMillis()}_${scheduledMeetingSignals.size + 1}",
            topic = topic,
            startTime = startTime,
            durationMinutes = durationMinutes,
            timeZoneId = timeZoneId,
            repeat = repeat,
            calendar = calendar,
            encryption = encryption,
            inviteeUserIds = inviteeUserIds,
            createdAt = System.currentTimeMillis()
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
        inviteeUserIds: List<String>
    ): ScheduledMeetingSignal? {
        val index = scheduledMeetingSignals.indexOfFirst { it.signalId == signalId }
        if (index < 0) return null
        val updatedSignal = scheduledMeetingSignals[index].copy(
            topic = topic,
            startTime = startTime,
            durationMinutes = durationMinutes,
            timeZoneId = timeZoneId,
            repeat = repeat,
            calendar = calendar,
            encryption = encryption,
            inviteeUserIds = inviteeUserIds.distinct()
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

    fun addRuntimeChatMessage(meetingId: String, content: String): Message {
        val currentUser = getCurrentUser()
        val timestamp = System.currentTimeMillis()
        val message = Message(
            messageId = "${RuntimeSignalPrefixes.RUNTIME_MESSAGE_ID_PREFIX}${timestamp}_${runtimeChatMessages.size + 1}",
            meetingId = meetingId,
            senderId = currentUser.userId,
            senderName = currentUser.username,
            content = content,
            timestamp = timestamp
        )
        runtimeChatMessages.add(message)
        persistRuntimeChatMessages()
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

    fun recordMeetingAction(
        actionType: String,
        meetingId: String,
        targetUserIds: List<String> = emptyList(),
        note: String = "",
        screenSharingEnabled: Boolean? = null,
        shareCode: String = ""
    ): MeetingActionSignal {
        val timestamp = System.currentTimeMillis()
        val action = MeetingActionSignal(
            actionId = "${RuntimeSignalPrefixes.MEETING_ACTION_ID_PREFIX}${timestamp}_${runtimeMeetingActions.size + 1}",
            meetingId = meetingId,
            actionType = actionType,
            targetUserIds = targetUserIds,
            note = note,
            screenSharingEnabled = screenSharingEnabled,
            shareCode = shareCode,
            occurredAt = timestamp
        )
        runtimeMeetingActions.add(action)
        persistRuntimeMeetingActions()
        bumpRuntimeDataVersion()
        return action
    }

    fun startShareScreenSession(shareCode: String): MeetingActionSignal {
        val normalizedShareCode = shareCode.filter { it.isDigit() }.take(8)
        stopActiveScreenShareSession(note = "Replaced by a new Share Page session")
        setCurrentMeeting(null)
        val meetingId = getCurrentMeeting().meetingId
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
        val currentMeetingId = getCurrentMeeting().meetingId
        val activeSession = activeScreenShareSession ?: return null
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
        scheduledMeetingSignals = mutableListOf()
        runtimeChatMessages = mutableListOf()
        runtimeJoinHistoryEntries = defaultJoinHistoryEntries()
        runtimeJoinHistoryActions = mutableListOf()
        runtimeMeetingActions = mutableListOf()
        activeScreenShareSession = null
        currentMeetingId = DEFAULT_CURRENT_MEETING_ID

        persistRuntimeScheduledMeetingSignals()
        persistRuntimeChatMessages()
        persistRuntimeJoinHistorySignal()
        persistRuntimeMeetingActions()
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

    private fun persistRuntimeScheduledMeetingSignals() {
        runCatching {
            runtimeScheduledMeetingFile().writeText(gson.toJson(scheduledMeetingSignals), Charsets.UTF_8)
        }
    }

    private fun persistRuntimeChatMessages() {
        runCatching {
            runtimeChatMessageFile().writeText(gson.toJson(runtimeChatMessages), Charsets.UTF_8)
        }
    }

    private fun persistRuntimeJoinHistorySignal() {
        runCatching {
            val payload = JoinMeetingHistorySignal(
                entries = runtimeJoinHistoryEntries,
                actions = runtimeJoinHistoryActions
            )
            runtimeJoinHistoryFile().writeText(gson.toJson(payload), Charsets.UTF_8)
        }
    }

    private fun persistRuntimeMeetingActions() {
        runCatching {
            runtimeMeetingActionFile().writeText(gson.toJson(runtimeMeetingActions), Charsets.UTF_8)
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

    private fun runtimeScheduledMeetingFile(): File {
        return File(appContext.filesDir, RuntimeSignalFileNames.RUNTIME_SCHEDULED_MEETINGS)
    }

    private fun runtimeChatMessageFile(): File {
        return File(appContext.filesDir, RuntimeSignalFileNames.RUNTIME_CHAT_MESSAGES)
    }

    private fun runtimeJoinHistoryFile(): File {
        return File(appContext.filesDir, RuntimeSignalFileNames.RUNTIME_JOIN_HISTORY)
    }

    private fun runtimeMeetingActionFile(): File {
        return File(appContext.filesDir, RuntimeSignalFileNames.RUNTIME_MEETING_ACTIONS)
    }

    private fun bumpRuntimeDataVersion() {
        runtimeDataVersion.value = runtimeDataVersion.value + 1
    }
}
