package com.example.zoom.presentation.search.detail.chatdetail

data class SearchChatDetailUiState(
    val chatName: String,
    val messages: List<ChatMessageItem>
)

data class ChatMessageItem(
    val senderName: String,
    val senderInitial: String,
    val content: String,
    val timeLabel: String,
    val isCurrentUser: Boolean
)

interface SearchChatDetailContract {
    interface View {
        fun showContent(content: SearchChatDetailUiState)
    }

    interface Presenter {
        fun loadData()
    }
}
