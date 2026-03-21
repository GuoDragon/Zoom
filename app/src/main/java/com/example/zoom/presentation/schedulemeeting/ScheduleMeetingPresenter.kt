package com.example.zoom.presentation.schedulemeeting

import com.example.zoom.data.DataRepository

class ScheduleMeetingPresenter(
    private val view: ScheduleMeetingContract.View
) : ScheduleMeetingContract.Presenter {
    override fun loadData() {
        val user = DataRepository.getCurrentUser()
        view.showContent(
            ScheduleMeetingUiState(
                meetingTitle = "${user.username}'s Zoom Meeting",
                starts = "Today at 17:00",
                duration = "30 mins",
                timeZone = "Beijing, Shanghai",
                repeat = "Never",
                calendar = "iCalendar",
                personalMeetingId = "994 888 1080",
                usePersonalMeetingId = false,
                requirePasscode = true,
                passcode = "d3L4Sh",
                waitingRoom = false,
                encryption = "Enhanced",
                invitees = "None",
                continuousMeetingChat = true,
                hostVideoOn = true,
                participantVideoOn = true
            )
        )
    }
}
