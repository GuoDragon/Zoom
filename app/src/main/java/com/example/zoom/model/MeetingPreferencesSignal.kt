package com.example.zoom.model

data class MeetingPreferencesSignal(
    val autoConnectAudioOn: Boolean,
    val autoTurnOnCameraOn: Boolean,
    val updatedAt: Long
)
