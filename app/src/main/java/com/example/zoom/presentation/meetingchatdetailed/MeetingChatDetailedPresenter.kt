package com.example.zoom.presentation.meetingchatdetailed

import com.example.zoom.data.DataRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MeetingChatDetailedPresenter(
    private val view: MeetingChatDetailedContract.View
) : MeetingChatDetailedContract.Presenter {
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.US)

    override fun loadData() {
        val currentUserId = DataRepository.getCurrentUser().userId
        val meetingId = DataRepository.getCurrentMeeting().meetingId
        val messages = DataRepository.getMessagesByMeetingId(meetingId)

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

    override fun sendMessage(content: String): ChatMessageUi? {
        val trimmedContent = content.trim()
        if (trimmedContent.isBlank()) return null

        val meetingId = DataRepository.getCurrentMeeting().meetingId
        val savedMessage = DataRepository.addRuntimeChatMessage(
            meetingId = meetingId,
            content = trimmedContent
        )
        val senderInitials = savedMessage.senderName
            .split(" ")
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .take(2)
            .joinToString("")
            .ifBlank { "?" }

        return ChatMessageUi(
            messageId = savedMessage.messageId,
            senderName = savedMessage.senderName,
            senderInitials = senderInitials,
            content = savedMessage.content,
            timestamp = timeFormat.format(Date(savedMessage.timestamp)),
            isSelf = true
        )
    }
}
