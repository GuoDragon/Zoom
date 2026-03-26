package com.example.zoom.presentation.joinmeeting

interface JoinMeetingContract {
    interface View {
        fun showContent(content: JoinMeetingUiState)
    }

    interface Presenter {
        fun loadData()
    }
}

data class JoinMeetingUiState(
    val audioOff: Boolean,
    val videoOff: Boolean,
    val historyItems: List<JoinMeetingHistoryItem>
)

data class JoinMeetingHistoryItem(
    val title: String,
    val meetingNumber: String
)
