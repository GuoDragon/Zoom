package com.example.zoom.presentation.schedulemeetingchat

interface ScheduleMeetingChatContract {
    interface View {
        fun showContent(content: ScheduleMeetingChatUiState)
    }

    interface Presenter {
        fun loadData(meetingId: String)
        fun sendMessage(meetingId: String, content: String): ScheduleMeetingChatMessageUi?
    }
}

data class ScheduleMeetingChatUiState(
    val meetingTitle: String,
    val messages: List<ScheduleMeetingChatMessageUi>
)

data class ScheduleMeetingChatMessageUi(
    val messageId: String,
    val senderName: String,
    val content: String,
    val timestampLabel: String,
    val isSelf: Boolean
)
