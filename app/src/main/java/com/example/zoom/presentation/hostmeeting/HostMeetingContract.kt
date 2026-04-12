package com.example.zoom.presentation.hostmeeting

interface HostMeetingContract {
    interface View {
        fun showContent(content: HostMeetingUiState)
    }

    interface Presenter {
        fun loadData()
        fun prepareMeetingSession(
            usePersonalMeetingId: Boolean,
            videoOn: Boolean,
            topic: String,
            waitingRoomEnabled: Boolean,
            allowJoinBeforeHost: Boolean
        ): String
    }
}

data class HostMeetingUiState(
    val personalMeetingId: String,
    val meetingTitle: String,
    val videoOn: Boolean,
    val audioConnectedByDefault: Boolean,
    val usePersonalMeetingId: Boolean,
    val waitingRoomEnabled: Boolean,
    val allowJoinBeforeHost: Boolean
)
