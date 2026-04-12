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
        val unreadByThreadId = DataRepository.getChatThreadStates()
            .associate { it.threadId to it.unreadCount }
        val meetingThreads = DataRepository.getChatList().map { message ->
            val threadId = "meeting_${message.meetingId}"
            TeamChatThreadUi(
                threadId = threadId,
                title = DataRepository.getMeetingById(message.meetingId)?.topic ?: message.senderName,
                preview = message.content,
                dateLabel = dateFormatter.format(Date(message.timestamp)),
                avatarText = message.senderName.firstOrNull()?.uppercase() ?: "#",
                sortTimestamp = message.timestamp,
                unreadCount = unreadByThreadId[threadId] ?: 0
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
                unreadCount = unreadByThreadId[signal.threadId] ?: 0,
                directUserId = partner.userId
            )
        }
        val allThreads = (directThreads + meetingThreads).sortedByDescending { it.sortTimestamp }
        view.showUiState(
            TeamChatUiState(
                currentUserInitial = currentUserInitial,
                chats = allThreads,
                unreadSessionCount = allThreads.count { it.unreadCount > 0 }
            )
        )
    }
}
