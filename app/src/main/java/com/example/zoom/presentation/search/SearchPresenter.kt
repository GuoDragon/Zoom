package com.example.zoom.presentation.search

import com.example.zoom.data.DataRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SearchPresenter(private val view: SearchContract.View) : SearchContract.Presenter {

    private var currentState: SearchUiState? = null

    override fun loadData() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        currentState = SearchUiState(
            tabs = listOf(
                "Top results",
                "Messages",
                "Chats and channels",
                "Meetings",
                "Contacts",
                "Files",
                "Docs",
                "Whiteboards",
                "Mail"
            ),
            dateOptions = listOf(
                "Any time",
                "Today",
                "Yesterday",
                "Last 7 days",
                "Last 30 days",
                "This year ($currentYear)",
                "Custom range"
            ),
            typeOptions = listOf("Any", "Recurring", "Non-recurring")
        )
        view.showContent(currentState!!)
    }

    override fun search(query: String) {
        val base = currentState ?: return
        if (query.isBlank()) {
            view.showContent(base.copy(
                messageResults = emptyList(),
                meetingResults = emptyList(),
                contactResults = emptyList(),
                chatResults = emptyList()
            ))
            return
        }

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        val timeFormat = SimpleDateFormat("h:mm a", Locale.US)

        val messages = DataRepository.searchMessages(query).map { msg ->
            val meeting = DataRepository.getMeetingById(msg.meetingId)
            MessageResult(
                messageId = msg.messageId,
                meetingId = msg.meetingId,
                senderName = msg.senderName,
                senderInitial = msg.senderName.firstOrNull()?.uppercase() ?: "?",
                meetingTopic = meeting?.topic ?: msg.meetingId,
                contentPreview = msg.content,
                timeLabel = formatTimestamp(msg.timestamp, dateFormat, timeFormat)
            )
        }

        val meetings = DataRepository.searchMeetings(query).map { m ->
            val dateTimeStr = "${dateFormat.format(Date(m.startTime))} ${timeFormat.format(Date(m.startTime))}"
            MeetingResult(
                meetingId = m.meetingId,
                topic = m.topic,
                dateTimeLabel = dateTimeStr,
                participantCount = m.participantIds.size
            )
        }

        val contacts = DataRepository.searchUsers(query).map { u ->
            ContactResult(
                userId = u.userId,
                username = u.username,
                email = u.email ?: "",
                initial = u.username.firstOrNull()?.uppercase() ?: "?"
            )
        }

        val chats = DataRepository.searchChats(query).map { msg ->
            val meeting = DataRepository.getMeetingById(msg.meetingId)
            ChatResult(
                meetingId = msg.meetingId,
                chatName = meeting?.topic ?: msg.meetingId,
                lastMessage = msg.content,
                timeLabel = formatTimestamp(msg.timestamp, dateFormat, timeFormat)
            )
        }

        view.showContent(base.copy(
            messageResults = messages,
            meetingResults = meetings,
            contactResults = contacts,
            chatResults = chats
        ))
    }

    private fun formatTimestamp(
        timestamp: Long,
        dateFormat: SimpleDateFormat,
        timeFormat: SimpleDateFormat
    ): String {
        val now = Calendar.getInstance()
        val msgCal = Calendar.getInstance().apply { timeInMillis = timestamp }
        return if (now.get(Calendar.YEAR) == msgCal.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == msgCal.get(Calendar.DAY_OF_YEAR)
        ) {
            timeFormat.format(Date(timestamp))
        } else {
            dateFormat.format(Date(timestamp))
        }
    }
}
