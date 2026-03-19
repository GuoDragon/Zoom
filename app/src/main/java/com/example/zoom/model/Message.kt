package com.example.zoom.model

data class Message(
    val messageId: String,
    val meetingId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long
)
