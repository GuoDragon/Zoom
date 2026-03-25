package com.example.zoom.presentation.meetingdetailed

import com.example.zoom.data.DataRepository
import com.example.zoom.presentation.meetingspeakerdetailed.ParticipantUi

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

        val meeting = DataRepository.getCurrentMeeting()
        val users = DataRepository.getParticipantsForMeeting(meeting.meetingId)
        val participantUis = users.mapIndexed { index, user ->
            val pInitials = user.username
                .split(" ")
                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                .take(2)
                .joinToString("")
                .ifBlank { "?" }
            ParticipantUi(
                userId = user.userId,
                name = user.username,
                initials = pInitials,
                isActiveSpeaker = index == 0
            )
        }

        view.showContent(
            MeetingDetailedUiState(
                title = "${currentUser.username}'s Zoom Meeting",
                participantInitials = initials,
                participantLabel = currentUser.username,
                participants = participantUis
            )
        )
    }
}
