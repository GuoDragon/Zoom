package com.example.zoom.presentation.meetingpreview

import com.example.zoom.data.DataRepository

class MeetingPreviewPresenter(
    private val view: MeetingPreviewContract.View
) : MeetingPreviewContract.Presenter {
    override fun loadData(meetingId: String?) {
        DataRepository.setCurrentMeeting(meetingId)
        val currentUser = DataRepository.getCurrentUser()
        val currentMeeting = DataRepository.getCurrentMeeting()
        val meetingTitle = if (meetingId.isNullOrBlank()) {
            "${currentUser.username}'s Zoom Meeting"
        } else {
            currentMeeting.topic
        }
        val initials = currentUser.username
            .split(" ")
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .take(2)
            .joinToString("")
            .ifBlank { "ME" }

        view.showContent(
            MeetingPreviewUiState(
                meetingTitle = meetingTitle,
                participantInitials = initials,
                microphoneOn = false,
                cameraOn = false,
                alwaysShowPreview = true
            )
        )
    }
}
