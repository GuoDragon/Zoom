package com.example.zoom.data

import android.content.Context
import com.example.zoom.model.Meeting
import com.example.zoom.model.Message
import com.example.zoom.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataRepository {
    private val gson = Gson()
    private var users: List<User> = emptyList()
    private var meetings: List<Meeting> = emptyList()
    private var messages: List<Message> = emptyList()
    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        val assets = context.assets
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
        initialized = true
    }

    fun getUsers(): List<User> = users
    fun getMeetings(): List<Meeting> = meetings
    fun getMessages(): List<Message> = messages

    fun getCurrentUser(): User = users.first { it.userId == "user001" }

    fun getUpcomingMeetings(): List<Meeting> {
        val now = System.currentTimeMillis()
        return meetings.filter { it.startTime > now }.sortedBy { it.startTime }
    }

    fun getMeetingsByDate(dateMillis: Long): List<Meeting> {
        val dayStart = dateMillis - (dateMillis % (24 * 60 * 60 * 1000))
        val dayEnd = dayStart + 24 * 60 * 60 * 1000
        return meetings.filter { it.startTime in dayStart until dayEnd }
    }

    fun getChatList(): List<Message> {
        return messages.groupBy { it.meetingId }
            .mapValues { it.value.maxByOrNull { msg -> msg.timestamp }!! }
            .values.sortedByDescending { it.timestamp }
    }

    fun getUserById(userId: String): User? = users.find { it.userId == userId }
}
