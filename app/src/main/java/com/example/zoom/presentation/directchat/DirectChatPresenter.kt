package com.example.zoom.presentation.directchat

import com.example.zoom.data.DataRepository
import com.example.zoom.model.DirectMessageSignal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DirectChatPresenter(
    private val view: DirectChatContract.View
) : DirectChatContract.Presenter {
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.US)

    override fun loadData(userId: String) {
        val partner = DataRepository.getUserById(userId) ?: return
        val currentUserId = DataRepository.getCurrentUser().userId
        val messages = DataRepository.getDirectMessagesForPartner(userId).map { message ->
            message.toUi(currentUserId)
        }
        view.showContent(
            DirectChatUiState(
                partnerName = partner.username,
                messages = messages
            )
        )
    }

    override fun sendMessage(userId: String, content: String): DirectChatMessageUi? {
        val trimmed = content.trim()
        if (trimmed.isBlank()) return null
        val signal = DataRepository.sendDirectMessage(userId, trimmed)
        return signal.toUi(DataRepository.getCurrentUser().userId)
    }

    private fun DirectMessageSignal.toUi(currentUserId: String): DirectChatMessageUi {
        return DirectChatMessageUi(
            messageId = messageId,
            senderName = senderName,
            content = content,
            timestampLabel = timeFormatter.format(Date(timestamp)),
            isSelf = senderId == currentUserId
        )
    }
}
