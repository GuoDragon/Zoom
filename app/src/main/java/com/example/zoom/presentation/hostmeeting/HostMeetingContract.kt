package com.example.zoom.presentation.hostmeeting

interface HostMeetingContract {
    interface View {
        fun showContent(content: HostMeetingUiState)
    }

    interface Presenter {
        fun loadData()
    }
}

data class HostMeetingUiState(
    val personalMeetingId: String,
    val videoOn: Boolean,
    val usePersonalMeetingId: Boolean
)
