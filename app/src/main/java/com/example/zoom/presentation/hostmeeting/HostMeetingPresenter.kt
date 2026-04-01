package com.example.zoom.presentation.hostmeeting

import com.example.zoom.data.DataRepository

class HostMeetingPresenter(
    private val view: HostMeetingContract.View
) : HostMeetingContract.Presenter {
    override fun loadData() {
        view.showContent(
            HostMeetingUiState(
                personalMeetingId = "994 888 1080",
                videoOn = true,
                usePersonalMeetingId = false
            )
        )
    }

    override fun prepareMeetingSession(usePersonalMeetingId: Boolean, videoOn: Boolean): String {
        return DataRepository.prepareHostMeetingSession(
            usePersonalMeetingId = usePersonalMeetingId,
            videoOn = videoOn
        )
    }
}
