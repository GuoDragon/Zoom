package com.example.zoom.presentation.calendar

import com.example.zoom.data.DataRepository
import kotlinx.coroutines.flow.StateFlow

class CalendarPresenter(private val view: CalendarContract.View) : CalendarContract.Presenter {
    override fun loadData(dateMillis: Long) {
        val currentUserInitial = DataRepository.getCurrentUser()
            .username
            .firstOrNull()
            ?.uppercase()
            ?: "?"
        val meetings = DataRepository.getMeetingsByDate(dateMillis)
        view.showUiState(
            CalendarUiState(
                currentUserInitial = currentUserInitial,
                meetings = meetings,
                isEmpty = meetings.isEmpty()
            )
        )
    }

    override fun observeRuntimeVersion(): StateFlow<Int> {
        return DataRepository.observeMeetingDataVersion()
    }
}
