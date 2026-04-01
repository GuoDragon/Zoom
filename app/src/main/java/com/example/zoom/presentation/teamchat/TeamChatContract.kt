package com.example.zoom.presentation.teamchat

import com.example.zoom.model.Message

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
    val chats: List<Message>
)
