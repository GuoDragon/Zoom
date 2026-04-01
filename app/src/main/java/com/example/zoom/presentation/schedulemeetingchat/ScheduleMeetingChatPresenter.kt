package com.example.zoom.presentation.schedulemeetingchat

import com.example.zoom.data.DataRepository
import com.example.zoom.model.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScheduleMeetingChatPresenter(
    private val view: ScheduleMeetingChatContract.View
) : ScheduleMeetingChatContract.Presenter {
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.US)

    override fun loadData(meetingId: String) {
        val meeting = DataRepository.getMeetingById(meetingId) ?: return
        val currentUserId = DataRepository.getCurrentUser().userId
        val messageUis = DataRepository.getMessagesByMeetingId(meetingId).map {
            it.toUi(currentUserId)
        }
        view.showContent(
            ScheduleMeetingChatUiState(
                meetingTitle = meeting.topic,
                messages = messageUis
            )
        )
    }

    override fun sendMessage(meetingId: String, content: String): ScheduleMeetingChatMessageUi? {
        val trimmed = content.trim()
        if (trimmed.isBlank()) return null
        val saved = DataRepository.addRuntimeChatMessage(
            meetingId = meetingId,
            content = trimmed
        )
        val currentUserId = DataRepository.getCurrentUser().userId
        return saved.toUi(currentUserId)
    }

    private fun Message.toUi(currentUserId: String): ScheduleMeetingChatMessageUi {
        return ScheduleMeetingChatMessageUi(
            messageId = messageId,
            senderName = senderName,
            content = content,
            timestampLabel = timeFormatter.format(Date(timestamp)),
            isSelf = senderId == currentUserId
        )
    }
}
