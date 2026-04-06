package com.example.zoom.model

data class RuntimeSignalMeta(
    val schemaVersion: Int,
    val seedScheduledMeetingCount: Int,
    val contactCount: Int,
    val generatedAt: Long
)
