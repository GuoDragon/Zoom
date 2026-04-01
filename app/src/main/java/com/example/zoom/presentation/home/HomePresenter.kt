package com.example.zoom.presentation.home

import com.example.zoom.data.DataRepository
import com.example.zoom.presentation.schedulemeeting.formatDurationLabel
import com.example.zoom.presentation.schedulemeeting.formatScheduleStartLabel
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomePresenter(private val view: HomeContract.View) : HomeContract.Presenter {
    private val staticTimeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun loadData() {
        val currentUser = DataRepository.getCurrentUser()
        val meetings = DataRepository.getUpcomingMeetings().map { meeting ->
            val runtimeSignal = DataRepository.getScheduledMeetingSignalById(meeting.meetingId)
            val timeLabel = if (runtimeSignal != null) {
                "${formatScheduleStartLabel(runtimeSignal.startTime, runtimeSignal.timeZoneId)} · ${formatDurationLabel(runtimeSignal.durationMinutes)}"
            } else {
                staticTimeFormatter.format(Date(meeting.startTime)) +
                    if (meeting.endTime != null) {
                        " - ${staticTimeFormatter.format(Date(meeting.endTime))}"
                    } else {
                        ""
                    }
            }
            HomeMeetingCardUi(
                meeting = meeting,
                timeLabel = timeLabel
            )
        }
        view.showHomeState(
            HomeUiState(
                currentUser = currentUser,
                upcomingMeetings = meetings
            )
        )
    }

    override fun observeRuntimeVersion(): StateFlow<Int> {
        return DataRepository.observeMeetingDataVersion()
    }
}
