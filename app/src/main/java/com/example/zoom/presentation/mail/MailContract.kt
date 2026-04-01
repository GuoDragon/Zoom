package com.example.zoom.presentation.mail

interface MailContract {
    interface View {
        fun showUiState(state: MailUiState)
    }
    interface Presenter {
        fun loadData()
    }
}

data class MailUiState(
    val currentUserInitial: String,
    val showWelcome: Boolean
)
