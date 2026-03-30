package com.example.zoom.model

data class MeetingActionSignal(
    val actionId: String,
    val meetingId: String,
    val actionType: String,
    val targetUserIds: List<String> = emptyList(),
    val note: String = "",
    val occurredAt: Long
)
