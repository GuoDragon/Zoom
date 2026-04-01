package com.example.zoom.presentation.sharepage

import com.example.zoom.ui.components.MeetingSessionConfig

interface SharePageContract {
    interface View {
        fun showContent(content: SharePageUiState)
        fun dismiss()
        fun navigateToMeeting(config: MeetingSessionConfig)
    }

    interface Presenter {
        fun loadData()
        fun onCancel()
        fun onConfirmShare(shareCode: String)
    }
}

data class SharePageUiState(
    val title: String,
    val hint: String,
    val placeholder: String,
    val cancelLabel: String,
    val confirmLabel: String
)
