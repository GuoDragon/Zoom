package com.example.zoom.presentation.meetingchatdetailed

interface MeetingChatDetailedContract {
    interface View {
        fun showContent(content: MeetingChatDetailedUiState)
    }

    interface Presenter {
        fun loadData()
        fun sendMessage(content: String): ChatMessageUi?
    }
}

data class ChatMessageUi(
    val messageId: String,
    val senderName: String,
    val senderInitials: String,
    val content: String,
    val timestamp: String,
    val isSelf: Boolean
)

data class MeetingChatDetailedUiState(
    val messages: List<ChatMessageUi>
)
