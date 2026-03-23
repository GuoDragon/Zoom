package com.example.zoom.presentation.meetingdetailed

import com.example.zoom.data.DataRepository

class MeetingDetailedPresenter(
    private val view: MeetingDetailedContract.View
) : MeetingDetailedContract.Presenter {
    override fun loadData() {
        val currentUser = DataRepository.getCurrentUser()
        val initials = currentUser.username
            .split(" ")
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .take(2)
            .joinToString("")
            .ifBlank { "ME" }

        view.showContent(
            MeetingDetailedUiState(
                title = "${currentUser.username}'s Zoom Meeting",
                participantInitials = initials,
                participantLabel = currentUser.username,
                controls = listOf(
                    MeetingControlUiState("Unmute", MeetingControlAction.Audio),
                    MeetingControlUiState("Start video", MeetingControlAction.Video),
                    MeetingControlUiState("Chat", MeetingControlAction.Chat),
                    MeetingControlUiState("More", MeetingControlAction.More),
                    MeetingControlUiState("End", MeetingControlAction.End)
                )
            )
        )
    }
}
