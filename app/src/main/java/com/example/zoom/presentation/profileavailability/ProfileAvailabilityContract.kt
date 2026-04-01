package com.example.zoom.presentation.profileavailability

interface ProfileAvailabilityContract {
    interface View {
        fun showContent(content: ProfileAvailabilityUiState)
    }

    interface Presenter {
        fun loadData()
        fun updateAvailability(availability: String)
    }
}

data class ProfileAvailabilityUiState(
    val currentAvailability: String,
    val options: List<String>
)
