package com.example.zoom.model

data class ChatThreadStateSignal(
    val threadId: String,
    val threadType: String,
    val partnerUserId: String = "",
    val meetingId: String = "",
    val unreadCount: Int,
    val lastMessageAt: Long,
    val lastReadAt: Long? = null
)
