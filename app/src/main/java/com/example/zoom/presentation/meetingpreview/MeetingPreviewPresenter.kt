package com.example.zoom.presentation.meetingpreview

import com.example.zoom.data.DataRepository
import com.example.zoom.ui.components.MeetingSessionConfig

class MeetingPreviewPresenter(
    private val view: MeetingPreviewContract.View
) : MeetingPreviewContract.Presenter {
    override fun loadData(meetingId: String?, initialConfig: MeetingSessionConfig) {
        DataRepository.setCurrentMeeting(meetingId)
        val currentUser = DataRepository.getCurrentUser()
        val currentMeeting = DataRepository.getCurrentMeeting()
        val initials = currentUser.username
            .split(" ")
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .take(2)
            .joinToString("")
            .ifBlank { "ME" }

        view.showContent(
            MeetingPreviewUiState(
                meetingTitle = currentMeeting.topic,
                participantInitials = initials,
                microphoneOn = initialConfig.microphoneOn,
                cameraOn = initialConfig.cameraOn,
                alwaysShowPreview = true
            )
        )
    }
}
