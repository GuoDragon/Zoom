package com.example.zoom.presentation.search.detail.meetingdetail

data class SearchMeetingDetailUiState(
    val meetingId: String,
    val topic: String,
    val dateTimeLabel: String,
    val durationLabel: String,
    val participants: List<ParticipantItem>,
    val canCancel: Boolean
)

data class ParticipantItem(
    val username: String,
    val email: String,
    val initial: String
)

interface SearchMeetingDetailContract {
    interface View {
        fun showContent(content: SearchMeetingDetailUiState)
    }

    interface Presenter {
        fun loadData()
        fun cancelMeeting(): Boolean
    }
}
