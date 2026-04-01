package com.example.zoom.presentation.directchat

interface DirectChatContract {
    interface View {
        fun showContent(content: DirectChatUiState)
    }

    interface Presenter {
        fun loadData(userId: String)
        fun sendMessage(userId: String, content: String): DirectChatMessageUi?
    }
}

data class DirectChatMessageUi(
    val messageId: String,
    val senderName: String,
    val content: String,
    val timestampLabel: String,
    val isSelf: Boolean
)

data class DirectChatUiState(
    val partnerName: String,
    val messages: List<DirectChatMessageUi>
)
