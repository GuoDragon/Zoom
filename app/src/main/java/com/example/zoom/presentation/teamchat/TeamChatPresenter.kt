package com.example.zoom.presentation.teamchat

import com.example.zoom.data.DataRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TeamChatPresenter(private val view: TeamChatContract.View) : TeamChatContract.Presenter {
    private val dateFormatter = SimpleDateFormat("MM/dd", Locale.getDefault())

    override fun loadData() {
        val currentUserInitial = DataRepository.getCurrentUser()
            .username
            .firstOrNull()
            ?.uppercase()
            ?: "?"
        val meetingThreads = DataRepository.getChatList().map { message ->
            TeamChatThreadUi(
                threadId = message.meetingId,
                title = DataRepository.getMeetingById(message.meetingId)?.topic ?: message.senderName,
                preview = message.content,
                dateLabel = dateFormatter.format(Date(message.timestamp)),
                avatarText = message.senderName.firstOrNull()?.uppercase() ?: "#",
                sortTimestamp = message.timestamp
            )
        }
        val directThreads = DataRepository.getDirectChatThreads().mapNotNull { signal ->
            val partner = DataRepository.getUserById(signal.partnerUserId) ?: return@mapNotNull null
            TeamChatThreadUi(
                threadId = signal.threadId,
                title = partner.username,
                preview = signal.content,
                dateLabel = dateFormatter.format(Date(signal.timestamp)),
                avatarText = partner.username.firstOrNull()?.uppercase() ?: "#",
                sortTimestamp = signal.timestamp,
                directUserId = partner.userId
            )
        }
        view.showUiState(
            TeamChatUiState(
                currentUserInitial = currentUserInitial,
                chats = (directThreads + meetingThreads).sortedByDescending { it.sortTimestamp }
            )
        )
    }
}
