package com.example.zoom.presentation.hostmeeting

interface HostMeetingContract {
    interface View {
        fun showContent(content: HostMeetingUiState)
    }

    interface Presenter {
        fun loadData()
        fun prepareMeetingSession(usePersonalMeetingId: Boolean, videoOn: Boolean): String
    }
}

data class HostMeetingUiState(
    val personalMeetingId: String,
    val videoOn: Boolean,
    val usePersonalMeetingId: Boolean
)
