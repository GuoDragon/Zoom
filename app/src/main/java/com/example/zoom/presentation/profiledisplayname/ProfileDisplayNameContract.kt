package com.example.zoom.presentation.profiledisplayname

interface ProfileDisplayNameContract {
    interface View {
        fun showContent(content: ProfileDisplayNameUiState)
    }

    interface Presenter {
        fun loadData()
        fun saveDisplayName(displayName: String)
    }
}

data class ProfileDisplayNameUiState(
    val displayName: String
)
