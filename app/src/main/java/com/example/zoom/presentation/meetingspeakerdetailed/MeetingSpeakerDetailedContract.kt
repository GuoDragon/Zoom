package com.example.zoom.presentation.meetingspeakerdetailed

interface MeetingSpeakerDetailedContract {
    interface View {
        fun showContent(content: MeetingSpeakerDetailedUiState)
    }

    interface Presenter {
        fun loadData()
    }
}

data class ParticipantUi(
    val userId: String,
    val name: String,
    val initials: String,
    val isActiveSpeaker: Boolean = false
)

data class MeetingSpeakerDetailedUiState(
    val participants: List<ParticipantUi>
)
