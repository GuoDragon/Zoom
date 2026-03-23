package com.example.zoom.presentation.leavemeeting

interface LeaveMeetingContract {
    interface View {
        fun showContent(content: LeaveMeetingUiState)
    }

    interface Presenter {
        fun loadData()
    }
}

data class LeaveMeetingUiState(
    val leaveLabel: String,
    val cancelLabel: String
)
