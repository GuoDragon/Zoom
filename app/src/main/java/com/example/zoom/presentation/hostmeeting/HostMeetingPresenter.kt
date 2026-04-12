package com.example.zoom.presentation.hostmeeting

import com.example.zoom.data.DataRepository

class HostMeetingPresenter(
    private val view: HostMeetingContract.View
) : HostMeetingContract.Presenter {
    override fun loadData() {
        val currentUser = DataRepository.getCurrentUser()
        val preferences = DataRepository.getMeetingPreferencesSignal()
        view.showContent(
            HostMeetingUiState(
                personalMeetingId = "994 888 1080",
                meetingTitle = "${currentUser.username}'s Zoom Meeting",
                videoOn = preferences.autoTurnOnCameraOn,
                audioConnectedByDefault = preferences.autoConnectAudioOn,
                usePersonalMeetingId = false,
                waitingRoomEnabled = false,
                allowJoinBeforeHost = true
            )
        )
    }

    override fun prepareMeetingSession(
        usePersonalMeetingId: Boolean,
        videoOn: Boolean,
        topic: String,
        waitingRoomEnabled: Boolean,
        allowJoinBeforeHost: Boolean
    ): String {
        return DataRepository.prepareHostMeetingSession(
            usePersonalMeetingId = usePersonalMeetingId,
            videoOn = videoOn,
            topic = topic,
            waitingRoomEnabled = waitingRoomEnabled,
            allowJoinBeforeHost = allowJoinBeforeHost
        )
    }
}
