package com.example.zoom.presentation.meetingdetailed

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
    val controls: List<MeetingControlUiState>
)

data class MeetingControlUiState(
    val label: String,
    val action: MeetingControlAction
)

enum class MeetingControlAction {
    Audio,
    Video,
    Chat,
    More,
    End
}
