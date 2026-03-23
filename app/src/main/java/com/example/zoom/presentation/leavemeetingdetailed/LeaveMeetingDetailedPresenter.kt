package com.example.zoom.presentation.leavemeetingdetailed

class LeaveMeetingDetailedPresenter(
    private val view: LeaveMeetingDetailedContract.View
) : LeaveMeetingDetailedContract.Presenter {
    override fun loadData() {
        view.showContent(
            LeaveMeetingDetailedUiState(
                endForAllLabel = "End meeting for all",
                leaveLabel = "Leave meeting",
                cancelLabel = "Cancel"
            )
        )
    }
}
