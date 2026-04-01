package com.example.zoom.model

data class InstantMeetingSessionSignal(
    val signalId: String,
    val meetingNumber: String,
    val topic: String,
    val source: String,
    val participantIds: List<String>,
    val passcode: String,
    val usePersonalMeetingId: Boolean,
    val createdAt: Long
)
