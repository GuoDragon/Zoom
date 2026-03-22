package com.example.zoom.presentation.search

data class SearchUiState(
    val tabs: List<String>,
    val dateOptions: List<String>,
    val typeOptions: List<String>,
    val messageResults: List<MessageResult> = emptyList(),
    val meetingResults: List<MeetingResult> = emptyList(),
    val contactResults: List<ContactResult> = emptyList(),
    val chatResults: List<ChatResult> = emptyList()
)

data class MessageResult(
    val messageId: String,
    val meetingId: String,
    val senderName: String,
    val senderInitial: String,
    val meetingTopic: String,
    val contentPreview: String,
    val timeLabel: String
)

data class MeetingResult(
    val meetingId: String,
    val topic: String,
    val dateTimeLabel: String,
    val participantCount: Int
)

data class ContactResult(
    val userId: String,
    val username: String,
    val email: String,
    val initial: String
)

data class ChatResult(
    val meetingId: String,
    val chatName: String,
    val lastMessage: String,
    val timeLabel: String
)
