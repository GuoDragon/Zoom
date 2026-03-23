package com.example.zoom.presentation.leavemeetingdetailed

interface LeaveMeetingDetailedContract {
    interface View {
        fun showContent(content: LeaveMeetingDetailedUiState)
    }

    interface Presenter {
        fun loadData()
    }
}

data class LeaveMeetingDetailedUiState(
    val endForAllLabel: String,
    val leaveLabel: String,
    val cancelLabel: String
)
