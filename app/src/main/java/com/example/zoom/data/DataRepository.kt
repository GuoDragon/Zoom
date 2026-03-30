package com.example.zoom.data

import android.content.Context
import com.example.zoom.model.Meeting
import com.example.zoom.model.Message
import com.example.zoom.model.ScheduledMeetingSignal
import com.example.zoom.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

object DataRepository {
    private val gson = Gson()
    private var users: List<User> = emptyList()
    private var meetings: List<Meeting> = emptyList()
    private var messages: List<Message> = emptyList()
    private var scheduledMeetingSignals: MutableList<ScheduledMeetingSignal> = mutableListOf()
    private lateinit var appContext: Context
    private var initialized = false
    private val meetingDataVersion = MutableStateFlow(0)

    private const val runtimeSignalFileName = "scheduled_meetings_runtime.json"
    private const val runtimeMeetingIdPrefix = "scheduled_"

    fun init(context: Context) {
        if (initialized) return
        appContext = context.applicationContext
        val assets = appContext.assets
        users = gson.fromJson(
            assets.open("data/users.json").bufferedReader().use { it.readText() },
            object : TypeToken<List<User>>() {}.type
        )
        meetings = gson.fromJson(
            assets.open("data/meetings.json").bufferedReader().use { it.readText() },
            object : TypeToken<List<Meeting>>() {}.type
        )
        messages = gson.fromJson(
            assets.open("data/messages.json").bufferedReader().use { it.readText() },
            object : TypeToken<List<Message>>() {}.type
        )
        resetRuntimeScheduledMeetingSignals()
        initialized = true
    }

    fun getUsers(): List<User> = users
    fun getMeetings(): List<Meeting> = meetings + scheduledMeetingSignals.map { signalToMeeting(it) }
    fun getMessages(): List<Message> = messages
    fun observeMeetingDataVersion(): StateFlow<Int> = meetingDataVersion
    fun getScheduledMeetingSignals(): List<ScheduledMeetingSignal> = scheduledMeetingSignals.toList()
    fun getScheduledMeetingSignalFilePath(): String = runtimeSignalFile().absolutePath

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
        return messages.groupBy { it.meetingId }
            .mapValues { it.value.maxByOrNull { msg -> msg.timestamp }!! }
            .values.sortedByDescending { it.timestamp }
    }

    fun getUserById(userId: String): User? = users.find { it.userId == userId }

    fun searchMessages(query: String): List<Message> {
        if (query.isBlank()) return emptyList()
        val q = query.lowercase()
        return messages.filter {
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
            val meeting = meetings.find { it.meetingId == msg.meetingId }
            meeting?.topic?.lowercase()?.contains(q) == true ||
                    msg.content.lowercase().contains(q)
        }
    }

    fun getMeetingById(meetingId: String): Meeting? {
        return meetings.find { it.meetingId == meetingId }
            ?: scheduledMeetingSignals.firstOrNull { it.signalId == meetingId }?.let { signalToMeeting(it) }
    }

    fun getMessagesByMeetingId(meetingId: String): List<Message> =
        messages.filter { it.meetingId == meetingId }.sortedBy { it.timestamp }

    fun getCurrentMeeting(): Meeting = meetings.first { it.meetingId == "mtg016" }

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
            signalId = "$runtimeMeetingIdPrefix${System.currentTimeMillis()}_${scheduledMeetingSignals.size + 1}",
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
        meetingDataVersion.value = meetingDataVersion.value + 1
        return signal
    }

    fun isRuntimeScheduledMeeting(meetingId: String): Boolean {
        return meetingId.startsWith(runtimeMeetingIdPrefix)
    }

    private fun resetRuntimeScheduledMeetingSignals() {
        scheduledMeetingSignals = mutableListOf()
        persistRuntimeScheduledMeetingSignals()
        meetingDataVersion.value = meetingDataVersion.value + 1
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
            runtimeSignalFile().writeText(gson.toJson(scheduledMeetingSignals))
        }
    }

    private fun runtimeSignalFile(): File {
        return File(appContext.filesDir, runtimeSignalFileName)
    }
}
