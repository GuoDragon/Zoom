package com.example.zoom.model

data class ScheduledMeetingSignal(
    val signalId: String,
    val meetingNumber: String,
    val topic: String,
    val startTime: Long,
    val durationMinutes: Int,
    val timeZoneId: String,
    val repeat: String,
    val calendar: String,
    val encryption: String,
    val inviteeUserIds: List<String>,
    val passcode: String,
    val waitingRoomEnabled: Boolean,
    val allowJoinBeforeHost: Boolean = true,
    val hostVideoOn: Boolean = true,
    val participantVideoOn: Boolean = true,
    val usePersonalMeetingId: Boolean,
    val createdAt: Long
)
