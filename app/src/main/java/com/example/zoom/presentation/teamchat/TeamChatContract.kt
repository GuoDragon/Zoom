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
    val chats: List<TeamChatThreadUi>
)

data class TeamChatThreadUi(
    val threadId: String,
    val title: String,
    val preview: String,
    val dateLabel: String,
    val avatarText: String,
    val sortTimestamp: Long,
    val directUserId: String? = null
)
