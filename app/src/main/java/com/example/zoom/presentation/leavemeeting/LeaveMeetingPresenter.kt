package com.example.zoom.presentation.leavemeeting

class LeaveMeetingPresenter(
    private val view: LeaveMeetingContract.View
) : LeaveMeetingContract.Presenter {
    override fun loadData() {
        view.showContent(
            LeaveMeetingUiState(
                leaveLabel = "Leave meeting",
                cancelLabel = "Cancel"
            )
        )
    }
}
