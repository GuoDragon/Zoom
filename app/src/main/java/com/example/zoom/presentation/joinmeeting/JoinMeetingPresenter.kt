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
                historyItems = DataRepository.getJoinHistoryEntries().map { item ->
                    JoinMeetingHistoryItem(
                        title = item.title,
                        meetingNumber = item.meetingNumber
                    )
                }
            )
        )
    }
}
