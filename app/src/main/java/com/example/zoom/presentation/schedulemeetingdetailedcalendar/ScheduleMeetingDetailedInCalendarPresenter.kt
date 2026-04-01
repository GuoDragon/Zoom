package com.example.zoom.presentation.schedulemeetingdetailedcalendar

import com.example.zoom.data.DataRepository
import com.example.zoom.model.ScheduledMeetingSignal
import com.example.zoom.presentation.schedulemeeting.formatDurationLabel
import com.example.zoom.presentation.schedulemeeting.formatScheduleStartLabel

class ScheduleMeetingDetailedInCalendarPresenter(
    private val view: ScheduleMeetingDetailedInCalendarContract.View
) : ScheduleMeetingDetailedInCalendarContract.Presenter {

    override fun loadData(meetingId: String) {
        val currentUser = DataRepository.getCurrentUser()
        val signal = DataRepository.getScheduledMeetingSignalById(meetingId)
        val meeting = DataRepository.getMeetingById(meetingId) ?: return

        val inviteeUserIds = signal?.inviteeUserIds
            ?: meeting.participantIds.filter { it != currentUser.userId }
        val inviteeNames = inviteeUserIds.mapNotNull { userId ->
            DataRepository.getUserById(userId)?.username
        }
        val inviteeSummary = when {
            inviteeNames.isEmpty() -> "None"
            inviteeNames.size == 1 -> inviteeNames.first()
            else -> "${inviteeNames.first()} +${inviteeNames.size - 1}"
        }

        val durationMinutes = signal?.durationMinutes
            ?: meeting.endTime?.let { end -> ((end - meeting.startTime) / 60_000L).toInt() }
            ?: 30
        val description = buildDescription(
            signal = signal,
            meetingTitle = meeting.topic,
            inviteeNames = inviteeNames
        )

        view.showContent(
            ScheduleMeetingDetailedInCalendarUiState(
                meetingId = meetingId,
                meetingTitle = meeting.topic,
                startsLabel = formatScheduleStartLabel(
                    startTimeMillis = meeting.startTime,
                    timeZoneId = signal?.timeZoneId ?: "Asia/Shanghai"
                ),
                durationLabel = formatDurationLabel(durationMinutes),
                inviteeSummary = inviteeSummary,
                description = description,
                canEdit = signal != null
            )
        )
    }

    private fun buildDescription(
        signal: ScheduledMeetingSignal?,
        meetingTitle: String,
        inviteeNames: List<String>
    ): String {
        val inviteeText = if (inviteeNames.isEmpty()) {
            "No invitees yet"
        } else {
            "Invitees: ${inviteeNames.joinToString(", ")}"
        }
        return if (signal != null) {
            "Repeat: ${signal.repeat}. Calendar: ${signal.calendar}. Encryption: ${signal.encryption}. $inviteeText."
        } else {
            "Topic: $meetingTitle. $inviteeText."
        }
    }
}
