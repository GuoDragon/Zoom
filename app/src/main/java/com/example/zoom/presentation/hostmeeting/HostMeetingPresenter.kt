package com.example.zoom.presentation.hostmeeting

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
}
