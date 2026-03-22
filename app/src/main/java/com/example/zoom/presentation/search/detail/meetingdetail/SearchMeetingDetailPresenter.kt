package com.example.zoom.presentation.search.detail.meetingdetail

import com.example.zoom.data.DataRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchMeetingDetailPresenter(
    private val view: SearchMeetingDetailContract.View,
    private val meetingId: String
) : SearchMeetingDetailContract.Presenter {

    override fun loadData() {
        val meeting = DataRepository.getMeetingById(meetingId) ?: return
        val dateFormat = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.US)

        val durationMs = (meeting.endTime ?: meeting.startTime) - meeting.startTime
        val durationMin = (durationMs / 60000).toInt()
        val durationLabel = if (durationMin >= 60) {
            "${durationMin / 60}h ${durationMin % 60}m"
        } else {
            "${durationMin}m"
        }

        val participants = meeting.participantIds.mapNotNull { id ->
            DataRepository.getUserById(id)
        }.map { user ->
            ParticipantItem(
                username = user.username,
                email = user.email ?: "",
                initial = user.username.firstOrNull()?.uppercase() ?: "?"
            )
        }

        view.showContent(
            SearchMeetingDetailUiState(
                topic = meeting.topic,
                dateTimeLabel = dateFormat.format(Date(meeting.startTime)),
                durationLabel = durationLabel,
                participants = participants
            )
        )
    }
}
