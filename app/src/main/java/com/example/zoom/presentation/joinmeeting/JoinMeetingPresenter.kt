package com.example.zoom.presentation.joinmeeting

class JoinMeetingPresenter(
    private val view: JoinMeetingContract.View
) : JoinMeetingContract.Presenter {
    override fun loadData() {
        view.showContent(
            JoinMeetingUiState(
                audioOff = false,
                videoOff = true,
                historyItems = listOf(
                    JoinMeetingHistoryItem(
                        title = "CL L's Zoom Meeting",
                        meetingNumber = "994888108"
                    ),
                    JoinMeetingHistoryItem(
                        title = "CL L's Zoom Meeting",
                        meetingNumber = "820112935"
                    ),
                    JoinMeetingHistoryItem(
                        title = "CL L's Zoom Meeting",
                        meetingNumber = "867558037"
                    ),
                    JoinMeetingHistoryItem(
                        title = "CL L's Zoom Meeting",
                        meetingNumber = "834821295"
                    )
                )
            )
        )
    }
}
