package com.example.zoom.model

data class UserProfileSignal(
    val displayName: String,
    val availability: String,
    val statusText: String,
    val updatedAt: Long
)
