package com.example.zoom.presentation.search.detail.messagedetail

data class SearchMessageDetailUiState(
    val topic: String,
    val messages: List<MessageItem>
)

data class MessageItem(
    val senderName: String,
    val senderInitial: String,
    val content: String,
    val timeLabel: String,
    val isCurrentUser: Boolean
)

interface SearchMessageDetailContract {
    interface View {
        fun showContent(content: SearchMessageDetailUiState)
    }

    interface Presenter {
        fun loadData()
    }
}
