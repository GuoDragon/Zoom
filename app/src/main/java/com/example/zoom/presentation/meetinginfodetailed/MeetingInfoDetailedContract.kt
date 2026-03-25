package com.example.zoom.presentation.meetinginfodetailed

interface MeetingInfoDetailedContract {
    interface View {
        fun showContent(content: MeetingInfoDetailedUiState)
    }

    interface Presenter {
        fun loadData()
    }
}

data class MeetingInfoDetailedUiState(
    val topic: String,
    val meetingId: String,
    val passcode: String,
    val host: String,
    val inviteLink: String,
    val dialInNumbers: String,
    val isEncrypted: Boolean = true
)
