package com.example.zoom.presentation.schedulemeeting

interface ScheduleMeetingContract {
    interface View {
        fun showContent(content: ScheduleMeetingUiState)
    }

    interface Presenter {
        fun loadData()
    }
}

data class ScheduleMeetingUiState(
    val meetingTitle: String,
    val starts: String,
    val duration: String,
    val timeZone: String,
    val repeat: String,
    val calendar: String,
    val personalMeetingId: String,
    val usePersonalMeetingId: Boolean,
    val requirePasscode: Boolean,
    val passcode: String,
    val waitingRoom: Boolean,
    val encryption: String,
    val invitees: String,
    val continuousMeetingChat: Boolean,
    val hostVideoOn: Boolean,
    val participantVideoOn: Boolean
)
