package com.example.zoom.presentation.meetingparticipantsdetailed

import com.example.zoom.data.DataRepository
import com.example.zoom.model.User

class MeetingParticipantsDetailedPresenter(
    private val view: MeetingParticipantsDetailedContract.View
) : MeetingParticipantsDetailedContract.Presenter {

    companion object {
        private val AVATAR_COLORS = longArrayOf(
            0xFF78A93A, 0xFF2D8CFF, 0xFFE57373, 0xFFFFB74D,
            0xFF4DB6AC, 0xFF9575CD, 0xFFFF8A65, 0xFF4FC3F7
        )
    }

    override fun loadData() {
        val currentUser = DataRepository.getCurrentUser()
        val meeting = DataRepository.getCurrentMeeting()
        val meetingParticipants = DataRepository.getParticipantsForMeeting(meeting.meetingId)
        val allUsers = DataRepository.getUsers()

        // Build participant list: current user as Host first, then others
        val participants = mutableListOf<MeetingParticipantUi>()

        participants.add(
            MeetingParticipantUi(
                userId = currentUser.userId,
                name = currentUser.username,
                initials = getInitials(currentUser.username),
                avatarColor = AVATAR_COLORS[0],
                roleTag = "(Host, me)",
                isMuted = false,
                isVideoOff = false,
                isHost = true,
                isSelf = true
            )
        )

        meetingParticipants.forEachIndexed { index, user ->
            participants.add(
                MeetingParticipantUi(
                    userId = user.userId,
                    name = user.username,
                    initials = getInitials(user.username),
                    avatarColor = AVATAR_COLORS[(index + 1) % AVATAR_COLORS.size],
                    roleTag = "",
                    isMuted = index % 2 == 0,
                    isVideoOff = index % 3 == 0,
                    isHost = false,
                    isSelf = false
                )
            )
        }

        val inviteOptions = listOf(
            InviteOptionUi("Send a message", "Chat"),
            InviteOptionUi("Invite contacts", "Contacts"),
            InviteOptionUi("Copy invite link", "Link")
        )

        val inviteMessageText = buildString {
            appendLine("${currentUser.username} is inviting you to a scheduled Zoom meeting.")
            appendLine()
            appendLine("Topic: ${meeting.topic}")
            appendLine("Meeting ID: ${meeting.meetingId}")
            appendLine()
            appendLine("Join Zoom Meeting")
            appendLine("https://zoom.us/j/${meeting.meetingId}")
        }

        val contacts = allUsers
            .sortedBy { it.username }
            .mapIndexed { index, user ->
                ContactUi(
                    userId = user.userId,
                    name = user.username,
                    initials = getInitials(user.username),
                    avatarColor = AVATAR_COLORS[index % AVATAR_COLORS.size],
                    phone = user.phone ?: ""
                )
            }

        view.showContent(
            MeetingParticipantsDetailedUiState(
                participantCount = participants.size,
                participants = participants,
                inviteOptions = inviteOptions,
                inviteMessageText = inviteMessageText,
                allContacts = contacts,
                meetingId = meeting.meetingId
            )
        )
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split("\\s+".toRegex())
        return when {
            parts.size >= 2 -> "${parts.first().first()}${parts.last().first()}".uppercase()
            parts.isNotEmpty() -> parts.first().take(2).uppercase()
            else -> "?"
        }
    }
}
