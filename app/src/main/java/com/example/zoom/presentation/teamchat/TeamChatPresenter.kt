package com.example.zoom.presentation.teamchat

import com.example.zoom.data.DataRepository

class TeamChatPresenter(private val view: TeamChatContract.View) : TeamChatContract.Presenter {
    override fun loadData() {
        view.showChatList(DataRepository.getChatList())
    }
}
