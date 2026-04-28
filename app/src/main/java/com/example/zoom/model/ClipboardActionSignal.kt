package com.example.zoom.model

data class ClipboardActionSignal(
    val type: String,
    val meetingId: String,
    val meetingNumber: String,
    val text: String,
    val createdAt: Long
)
