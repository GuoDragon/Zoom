package com.example.zoom.presentation.joinmeeting

import com.example.zoom.data.DataRepository

class JoinMeetingPresenter(
    private val view: JoinMeetingContract.View
) : JoinMeetingContract.Presenter {
    override fun loadData() {
        view.showContent(
            JoinMeetingUiState(
                audioOff = false,
                videoOff = true,
                historyItems = refreshHistory()
            )
        )
    }

    override fun refreshHistory(): List<JoinMeetingHistoryItem> {
        return DataRepository.getJoinHistoryEntries().map { item ->
            JoinMeetingHistoryItem(
                title = item.title,
                meetingNumber = item.meetingNumber
            )
        }
    }

    override fun prepareJoinByMeetingNumber(meetingNumber: String): String {
        DataRepository.recordJoinHistoryUsed(
            meetingNumber = meetingNumber,
            title = "Zoom Meeting $meetingNumber"
        )
        return DataRepository.prepareJoinMeetingSession(meetingNumber = meetingNumber)
    }

    override fun prepareJoinByHistoryItem(item: JoinMeetingHistoryItem): String {
        DataRepository.recordJoinHistoryUsed(
            meetingNumber = item.meetingNumber,
            title = item.title
        )
        return DataRepository.prepareJoinMeetingSession(
            meetingNumber = item.meetingNumber,
            title = item.title
        )
    }

    override fun clearHistory() {
        DataRepository.clearJoinHistoryEntries()
    }
}
