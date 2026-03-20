package com.example.zoom.presentation.mail

interface MailContract {
    interface View {
        fun showWelcome()
    }
    interface Presenter {
        fun loadData()
    }
}
