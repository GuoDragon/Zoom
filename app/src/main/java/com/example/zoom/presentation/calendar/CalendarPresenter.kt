package com.example.zoom.presentation.calendar

import com.example.zoom.data.DataRepository

class CalendarPresenter(private val view: CalendarContract.View) : CalendarContract.Presenter {
    override fun loadData(dateMillis: Long) {
        val meetings = DataRepository.getMeetingsByDate(dateMillis)
        if (meetings.isEmpty()) {
            view.showEmpty()
        } else {
            view.showMeetings(meetings)
        }
    }
}
