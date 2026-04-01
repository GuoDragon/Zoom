package com.example.zoom.presentation.meetingpreview

import com.example.zoom.ui.components.MeetingSessionConfig

interface MeetingPreviewContract {
    interface View {
        fun showContent(content: MeetingPreviewUiState)
    }

    interface Presenter {
        fun loadData(meetingId: String?, initialConfig: MeetingSessionConfig)
    }
}

data class MeetingPreviewUiState(
    val meetingTitle: String,
    val participantInitials: String,
    val microphoneOn: Boolean,
    val cameraOn: Boolean,
    val alwaysShowPreview: Boolean
)
