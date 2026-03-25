package com.example.zoom.presentation.meetingspeakerdetailed

import com.example.zoom.data.DataRepository

class MeetingSpeakerDetailedPresenter(
    private val view: MeetingSpeakerDetailedContract.View
) : MeetingSpeakerDetailedContract.Presenter {
    override fun loadData() {
        val meeting = DataRepository.getCurrentMeeting()
        val participants = DataRepository.getParticipantsForMeeting(meeting.meetingId)

        val participantUis = participants.mapIndexed { index, user ->
            val initials = user.username
                .split(" ")
                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                .take(2)
                .joinToString("")
                .ifBlank { "?" }

            ParticipantUi(
                userId = user.userId,
                name = user.username,
                initials = initials,
                isActiveSpeaker = index == 0
            )
        }

        view.showContent(MeetingSpeakerDetailedUiState(participants = participantUis))
    }
}
