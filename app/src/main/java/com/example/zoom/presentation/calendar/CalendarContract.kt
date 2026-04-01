package com.example.zoom.presentation.calendar

import com.example.zoom.model.Meeting
import kotlinx.coroutines.flow.StateFlow

interface CalendarContract {
    interface View {
        fun showUiState(state: CalendarUiState)
    }
    interface Presenter {
        fun loadData(dateMillis: Long)
        fun observeRuntimeVersion(): StateFlow<Int>
    }
}

data class CalendarUiState(
    val currentUserInitial: String,
    val meetings: List<Meeting>,
    val isEmpty: Boolean
)
