package com.example.zoom.presentation.meetingdetailed

import com.example.zoom.presentation.meetingspeakerdetailed.ParticipantUi

interface MeetingDetailedContract {
    interface View {
        fun showContent(content: MeetingDetailedUiState)
    }

    interface Presenter {
        fun loadData()
    }
}

data class MeetingDetailedUiState(
    val title: String,
    val participantInitials: String,
    val participantLabel: String,
    val participants: List<ParticipantUi> = emptyList()
)
