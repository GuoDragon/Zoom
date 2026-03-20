package com.example.zoom.presentation.mail

class MailPresenter(private val view: MailContract.View) : MailContract.Presenter {
    override fun loadData() {
        view.showWelcome()
    }
}
