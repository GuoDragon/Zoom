package com.example.zoom.presentation.meetingpreview

interface MeetingPreviewContract {
    interface View {
        fun showContent(content: MeetingPreviewUiState)
    }

    interface Presenter {
        fun loadData(meetingId: String?)
    }
}

data class MeetingPreviewUiState(
    val meetingTitle: String,
    val participantInitials: String,
    val microphoneOn: Boolean,
    val cameraOn: Boolean,
    val alwaysShowPreview: Boolean
)
