package com.example.zoom.presentation.meetingchatdetailed

import com.example.zoom.data.DataRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MeetingChatDetailedPresenter(
    private val view: MeetingChatDetailedContract.View
) : MeetingChatDetailedContract.Presenter {
    override fun loadData() {
        val currentUserId = "user001"
        val meetingId = "mtg016"
        val messages = DataRepository.getMessagesByMeetingId(meetingId)
        val timeFormat = SimpleDateFormat("h:mm a", Locale.US)

        val chatMessages = messages.map { msg ->
            val initials = msg.senderName
                .split(" ")
                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                .take(2)
                .joinToString("")
                .ifBlank { "?" }

            ChatMessageUi(
                messageId = msg.messageId,
                senderName = msg.senderName,
                senderInitials = initials,
                content = msg.content,
                timestamp = timeFormat.format(Date(msg.timestamp)),
                isSelf = msg.senderId == currentUserId
            )
        }

        view.showContent(MeetingChatDetailedUiState(messages = chatMessages))
    }
}
