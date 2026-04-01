package com.example.zoom.presentation.meetinginfodetailed

import com.example.zoom.data.DataRepository

class MeetingInfoDetailedPresenter(
    private val view: MeetingInfoDetailedContract.View
) : MeetingInfoDetailedContract.Presenter {
    override fun loadData() {
        val meeting = DataRepository.getCurrentMeeting()
        val host = DataRepository.getCurrentUser()
        val meetingNumber = DataRepository.getMeetingNumber(meeting.meetingId)

        view.showContent(
            MeetingInfoDetailedUiState(
                topic = meeting.topic,
                meetingId = meetingNumber,
                passcode = DataRepository.getMeetingPasscode(meeting.meetingId),
                host = host.username,
                inviteLink = DataRepository.getMeetingInviteLink(meeting.meetingId),
                participantId = "408628",
                encryptionLabel = "Enhanced",
                connectionSummary = "You are connected to Zoom Global\nNetwork via data centers in the United States",
                securityOverviewLabel = "Security settings overview"
            )
        )
    }
}
