package com.example.zoom.presentation.schedulemeetingdetailed

import com.example.zoom.presentation.schedulemeeting.ScheduleMeetingInviteeOption

interface ScheduleMeetingDetailedContract {
    interface View {
        fun showContent(content: ScheduleMeetingDetailedUiState)
    }

    interface Presenter {
        fun loadData(meetingId: String)
        fun updateInvitees(meetingId: String, inviteeUserIds: Set<String>)
        fun cancelMeeting(meetingId: String): Boolean
    }
}

data class ScheduleMeetingDetailedUiState(
    val meetingId: String,
    val meetingTitle: String,
    val meetingNumberLabel: String,
    val startsLabel: String,
    val durationLabel: String,
    val canEdit: Boolean,
    val inviteeSummary: String,
    val waitingRoomLabel: String,
    val selectedInviteeUserIds: Set<String>,
    val inviteeOptions: List<ScheduleMeetingInviteeOption>,
    val inviteMessageText: String,
    val recentMessages: List<ScheduleMeetingDetailedChatPreviewUi>
)

data class ScheduleMeetingDetailedChatPreviewUi(
    val messageId: String,
    val senderName: String,
    val content: String,
    val timestampLabel: String
)
