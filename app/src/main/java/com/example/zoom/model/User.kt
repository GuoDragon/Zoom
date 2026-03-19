package com.example.zoom.model

data class User(
    val userId: String,
    val username: String,
    val avatar: String? = null,
    val phone: String? = null,
    val email: String? = null
)
