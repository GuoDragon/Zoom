package com.example.zoom.model

data class Meeting(
    val meetingId: String,
    val topic: String,
    val startTime: Long,
    val endTime: Long? = null,
    val participantIds: List<String> = emptyList()
)
