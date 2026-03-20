package com.example.zoom.presentation.teamchat

import com.example.zoom.model.Message

interface TeamChatContract {
    interface View {
        fun showChatList(chats: List<Message>)
    }
    interface Presenter {
        fun loadData()
    }
}
