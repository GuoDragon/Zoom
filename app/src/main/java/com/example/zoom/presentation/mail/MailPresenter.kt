package com.example.zoom.presentation.mail

import com.example.zoom.data.DataRepository

class MailPresenter(private val view: MailContract.View) : MailContract.Presenter {
    override fun loadData() {
        val currentUserInitial = DataRepository.getCurrentUser()
            .username
            .firstOrNull()
            ?.uppercase()
            ?: "?"
        view.showUiState(
            MailUiState(
                currentUserInitial = currentUserInitial,
                showWelcome = true
            )
        )
    }
}
