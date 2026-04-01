package com.example.zoom.presentation.home

import com.example.zoom.model.Meeting
import com.example.zoom.model.User
import kotlinx.coroutines.flow.StateFlow

interface HomeContract {
    interface View {
        fun showHomeState(state: HomeUiState)
    }
    interface Presenter {
        fun loadData()
        fun observeRuntimeVersion(): StateFlow<Int>
    }
}

data class HomeUiState(
    val currentUser: User,
    val upcomingMeetings: List<HomeMeetingCardUi>
)

data class HomeMeetingCardUi(
    val meeting: Meeting,
    val timeLabel: String
)
