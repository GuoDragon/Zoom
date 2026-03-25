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
                passcode = "283746",
                host = host.username,
                inviteLink = "https://zoom.us/j/${meeting.meetingId}",
                dialInNumbers = "+1 669 900 9128, +1 346 248 7799"
            )
        )
    }
}
