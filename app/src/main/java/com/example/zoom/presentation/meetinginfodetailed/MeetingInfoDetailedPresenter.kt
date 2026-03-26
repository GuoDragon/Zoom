package com.example.zoom.presentation.meetinginfodetailed

import com.example.zoom.data.DataRepository

class MeetingInfoDetailedPresenter(
    private val view: MeetingInfoDetailedContract.View
) : MeetingInfoDetailedContract.Presenter {
    override fun loadData() {
        val meeting = DataRepository.getCurrentMeeting()
        val host = DataRepository.getCurrentUser()

        view.showContent(
            MeetingInfoDetailedUiState(
                topic = meeting.topic,
                meetingId = meeting.meetingId,
                passcode = "qwjU5X",
                host = host.username,
                inviteLink = "https://zoom.us/j/${meeting.meetingId}",
                participantId = "408628",
                encryptionLabel = "Enhanced",
                connectionSummary = "You are connected to Zoom Global\nNetwork via data centers in the United States",
                securityOverviewLabel = "Security settings overview"
            )
        )
    }
}
