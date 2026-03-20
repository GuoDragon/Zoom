package com.example.zoom.presentation.home

import com.example.zoom.data.DataRepository

class HomePresenter(private val view: HomeContract.View) : HomeContract.Presenter {
    override fun loadData() {
        view.showCurrentUser(DataRepository.getCurrentUser())
        view.showUpcomingMeetings(DataRepository.getUpcomingMeetings())
    }
}
