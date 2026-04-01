package com.example.zoom.presentation.schedulemeetingdetailed

import com.example.zoom.common.format.buildMeetingInviteMessageText
import com.example.zoom.data.DataRepository
import com.example.zoom.presentation.schedulemeeting.ScheduleMeetingInviteeOption
import com.example.zoom.presentation.schedulemeeting.formatDurationLabel
import com.example.zoom.presentation.schedulemeeting.formatScheduleStartLabel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScheduleMeetingDetailedPresenter(
    private val view: ScheduleMeetingDetailedContract.View
) : ScheduleMeetingDetailedContract.Presenter {
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.US)

    override fun loadData(meetingId: String) {
        val currentUser = DataRepository.getCurrentUser()
        val inviteeOptions = DataRepository.getUsers()
            .filter { it.userId != currentUser.userId }
            .sortedBy { it.username.lowercase() }
            .map { user ->
                ScheduleMeetingInviteeOption(
                    userId = user.userId,
                    name = user.username,
                    email = user.email.orEmpty()
                )
            }

        val signal = DataRepository.getScheduledMeetingSignalById(meetingId)
        val meeting = DataRepository.getMeetingById(meetingId) ?: return

        val selectedInvitees = signal?.inviteeUserIds?.toSet()
            ?: meeting.participantIds.filter { it != currentUser.userId }.toSet()
        val selectedInviteeNames = inviteeOptions
            .filter { selectedInvitees.contains(it.userId) }
            .map { it.name }
        val inviteeSummary = when {
            selectedInviteeNames.isEmpty() -> "None"
            selectedInviteeNames.size == 1 -> selectedInviteeNames.first()
            else -> "${selectedInviteeNames.first()} +${selectedInviteeNames.size - 1}"
        }

        val durationMinutes = signal?.durationMinutes
            ?: meeting.endTime?.let { end -> ((end - meeting.startTime) / 60_000L).toInt() }
            ?: 30
        val startLabel = formatScheduleStartLabel(
            startTimeMillis = meeting.startTime,
            timeZoneId = signal?.timeZoneId ?: "Asia/Shanghai"
        )

        val recentMessages = DataRepository.getMessagesByMeetingId(meetingId)
            .takeLast(3)
            .map { message ->
                ScheduleMeetingDetailedChatPreviewUi(
                    messageId = message.messageId,
                    senderName = message.senderName,
                    content = message.content,
                    timestampLabel = timeFormatter.format(Date(message.timestamp))
                )
            }
        val inviteMessageText = buildMeetingInviteMessageText(
            hostName = currentUser.username,
            meetingTopic = meeting.topic,
            meetingId = meeting.meetingId
        )

        view.showContent(
            ScheduleMeetingDetailedUiState(
                meetingId = meetingId,
                meetingTitle = meeting.topic,
                startsLabel = startLabel,
                durationLabel = formatDurationLabel(durationMinutes),
                canEdit = signal != null,
                inviteeSummary = inviteeSummary,
                selectedInviteeUserIds = selectedInvitees,
                inviteeOptions = inviteeOptions,
                inviteMessageText = inviteMessageText,
                recentMessages = recentMessages
            )
        )
    }

    override fun updateInvitees(meetingId: String, inviteeUserIds: Set<String>) {
        DataRepository.updateScheduledMeetingInvitees(
            signalId = meetingId,
            inviteeUserIds = inviteeUserIds.toList()
        )
        loadData(meetingId)
    }
}
