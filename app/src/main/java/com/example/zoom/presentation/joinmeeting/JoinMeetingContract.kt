package com.example.zoom.presentation.joinmeeting

interface JoinMeetingContract {
    interface View {
        fun showContent(content: JoinMeetingUiState)
    }

    interface Presenter {
        fun loadData()
        fun refreshHistory(): List<JoinMeetingHistoryItem>
        fun prepareJoinByMeetingNumber(meetingNumber: String): String
        fun prepareJoinByHistoryItem(item: JoinMeetingHistoryItem): String
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
