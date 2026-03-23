package com.example.zoom.presentation.meetingpreview

import com.example.zoom.data.DataRepository

class MeetingPreviewPresenter(
    private val view: MeetingPreviewContract.View
) : MeetingPreviewContract.Presenter {
    override fun loadData() {
        val currentUser = DataRepository.getCurrentUser()
        val initials = currentUser.username
            .split(" ")
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .take(2)
            .joinToString("")
            .ifBlank { "ME" }

        view.showContent(
            MeetingPreviewUiState(
                meetingTitle = "${currentUser.username}'s Zoom Meeting",
                participantInitials = initials,
                microphoneOn = false,
                cameraOn = false,
                alwaysShowPreview = true
            )
        )
    }
}
