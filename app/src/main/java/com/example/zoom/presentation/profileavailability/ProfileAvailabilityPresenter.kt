package com.example.zoom.presentation.profileavailability

import com.example.zoom.data.DataRepository

class ProfileAvailabilityPresenter(
    private val view: ProfileAvailabilityContract.View
) : ProfileAvailabilityContract.Presenter {
    override fun loadData() {
        view.showContent(
            ProfileAvailabilityUiState(
                currentAvailability = DataRepository.getUserProfileSignal().availability,
                options = listOf("Available", "Busy")
            )
        )
    }

    override fun updateAvailability(availability: String) {
        val statusText = if (availability == "Busy") availability else "What is your status?"
        DataRepository.updateCurrentUserAvailability(
            availability = availability,
            statusText = statusText
        )
    }
}
