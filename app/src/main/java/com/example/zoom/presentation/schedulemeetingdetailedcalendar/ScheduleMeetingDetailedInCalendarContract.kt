package com.example.zoom.presentation.schedulemeetingdetailedcalendar

interface ScheduleMeetingDetailedInCalendarContract {
    interface View {
        fun showContent(content: ScheduleMeetingDetailedInCalendarUiState)
    }

    interface Presenter {
        fun loadData(meetingId: String)
    }
}

data class ScheduleMeetingDetailedInCalendarUiState(
    val meetingId: String,
    val meetingTitle: String,
    val startsLabel: String,
    val durationLabel: String,
    val inviteeSummary: String,
    val description: String,
    val canEdit: Boolean
)
