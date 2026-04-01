package com.example.zoom.presentation.profile

import com.example.zoom.model.User

interface ProfileContract {
    interface View {
        fun showUser(state: ProfileUiState)
    }
    interface Presenter {
        fun loadData()
    }
}

data class ProfileUiState(
    val user: User,
    val availability: String,
    val statusText: String
)
