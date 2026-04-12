package com.example.zoom.presentation.teamchat

interface TeamChatContract {
    interface View {
        fun showUiState(state: TeamChatUiState)
    }
    interface Presenter {
        fun loadData()
    }
}

data class TeamChatUiState(
    val currentUserInitial: String,
    val chats: List<TeamChatThreadUi>,
    val unreadSessionCount: Int
)

data class TeamChatThreadUi(
    val threadId: String,
    val title: String,
    val preview: String,
    val dateLabel: String,
    val avatarText: String,
    val sortTimestamp: Long,
    val unreadCount: Int = 0,
    val directUserId: String? = null
)
