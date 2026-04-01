package com.example.zoom.model

data class DirectMessageSignal(
    val messageId: String,
    val threadId: String,
    val partnerUserId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long
)
