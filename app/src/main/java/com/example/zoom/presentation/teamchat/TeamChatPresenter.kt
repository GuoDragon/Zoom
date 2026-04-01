package com.example.zoom.presentation.teamchat

import com.example.zoom.data.DataRepository

class TeamChatPresenter(private val view: TeamChatContract.View) : TeamChatContract.Presenter {
    override fun loadData() {
        val currentUserInitial = DataRepository.getCurrentUser()
            .username
            .firstOrNull()
            ?.uppercase()
            ?: "?"
        view.showUiState(
            TeamChatUiState(
                currentUserInitial = currentUserInitial,
                chats = DataRepository.getChatList()
            )
        )
    }
}
