package com.example.zoom.presentation.calendar

import com.example.zoom.model.Meeting

interface CalendarContract {
    interface View {
        fun showMeetings(meetings: List<Meeting>)
        fun showEmpty()
    }
    interface Presenter {
        fun loadData(dateMillis: Long)
    }
}
