package com.example.zoom.model

data class MeetingActionSignal(
    val actionId: String,
    val meetingId: String,
    val meetingNumber: String = "",
    val actionType: String,
    val targetUserIds: List<String> = emptyList(),
    val note: String = "",
    val emoji: String = "",
    val microphoneOn: Boolean? = null,
    val cameraOn: Boolean? = null,
    val audioOption: String = "",
    val exitAction: String = "",
    val mediaChangeSource: String = "",
    val screenSharingEnabled: Boolean? = null,
    val shareCode: String = "",
    val occurredAt: Long
)
