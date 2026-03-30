package com.example.zoom.model

data class JoinMeetingHistoryEntry(
    val title: String,
    val meetingNumber: String,
    val lastUsedAt: Long? = null
)

data class JoinMeetingHistoryAction(
    val actionId: String,
    val actionType: String,
    val meetingNumber: String? = null,
    val title: String? = null,
    val occurredAt: Long
)

data class JoinMeetingHistorySignal(
    val entries: List<JoinMeetingHistoryEntry>,
    val actions: List<JoinMeetingHistoryAction>
)
