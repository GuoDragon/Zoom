package com.example.zoom.presentation.joinmeeting

interface JoinMeetingContract {
    interface View {
        fun showContent(content: JoinMeetingUiState)
    }

    interface Presenter {
        fun loadData()
        fun refreshHistory(): List<JoinMeetingHistoryItem>
        fun recordJoinByMeetingNumber(meetingNumber: String)
        fun recordJoinByHistoryItem(item: JoinMeetingHistoryItem)
        fun clearHistory()
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
