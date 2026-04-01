package com.example.zoom.presentation.profiledisplayname

import com.example.zoom.data.DataRepository

class ProfileDisplayNamePresenter(
    private val view: ProfileDisplayNameContract.View
) : ProfileDisplayNameContract.Presenter {
    override fun loadData() {
        view.showContent(ProfileDisplayNameUiState(DataRepository.getCurrentUser().username))
    }

    override fun saveDisplayName(displayName: String) {
        DataRepository.updateCurrentUserDisplayName(displayName)
    }
}
