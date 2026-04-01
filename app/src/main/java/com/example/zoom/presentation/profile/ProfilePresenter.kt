package com.example.zoom.presentation.profile

import com.example.zoom.data.DataRepository

class ProfilePresenter(private val view: ProfileContract.View) : ProfileContract.Presenter {
    override fun loadData() {
        val profileSignal = DataRepository.getUserProfileSignal()
        view.showUser(
            ProfileUiState(
                user = DataRepository.getCurrentUser(),
                availability = profileSignal.availability,
                statusText = profileSignal.statusText
            )
        )
    }
}
