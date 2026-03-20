package com.example.zoom.presentation.profile

import com.example.zoom.data.DataRepository

class ProfilePresenter(private val view: ProfileContract.View) : ProfileContract.Presenter {
    override fun loadData() {
        view.showUser(DataRepository.getCurrentUser())
    }
}
