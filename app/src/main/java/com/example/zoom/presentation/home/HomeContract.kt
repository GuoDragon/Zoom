package com.example.zoom.presentation.home

import com.example.zoom.model.Meeting
import com.example.zoom.model.User

interface HomeContract {
    interface View {
        fun showUpcomingMeetings(meetings: List<Meeting>)
        fun showCurrentUser(user: User)
    }
    interface Presenter {
        fun loadData()
    }
}
