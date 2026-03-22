package com.example.zoom.presentation.search.detail.messagedetail

import com.example.zoom.data.DataRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchMessageDetailPresenter(
    private val view: SearchMessageDetailContract.View,
    private val meetingId: String
) : SearchMessageDetailContract.Presenter {

    override fun loadData() {
        val meeting = DataRepository.getMeetingById(meetingId)
        val messages = DataRepository.getMessagesByMeetingId(meetingId)
        val currentUser = DataRepository.getCurrentUser()
        val timeFormat = SimpleDateFormat("h:mm a", Locale.US)

        view.showContent(
            SearchMessageDetailUiState(
                topic = meeting?.topic ?: meetingId,
                messages = messages.map { msg ->
                    MessageItem(
                        senderName = msg.senderName,
                        senderInitial = msg.senderName.firstOrNull()?.uppercase() ?: "?",
                        content = msg.content,
                        timeLabel = timeFormat.format(Date(msg.timestamp)),
                        isCurrentUser = msg.senderId == currentUser.userId
                    )
                }
            )
        )
    }
}
