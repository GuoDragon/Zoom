package com.example.zoom.presentation.joinmeeting

class JoinMeetingPresenter(
    private val view: JoinMeetingContract.View
) : JoinMeetingContract.Presenter {
    override fun loadData() {
        view.showContent(
            JoinMeetingUiState(
                audioOff = false,
                videoOff = true
            )
        )
    }
}
