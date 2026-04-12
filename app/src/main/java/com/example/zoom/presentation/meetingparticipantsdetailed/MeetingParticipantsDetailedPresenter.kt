package com.example.zoom.presentation.meetingparticipantsdetailed

import com.example.zoom.common.constants.MeetingActionTypes
import com.example.zoom.common.format.buildMeetingInviteMessageText
import com.example.zoom.data.DataRepository

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
            .filterNot { it.userId == currentUser.userId }
        val allUsers = DataRepository.getContacts()

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

        val inviteMessageText = buildMeetingInviteMessageText(
            hostName = currentUser.username,
            meetingTopic = meeting.topic,
            meetingId = DataRepository.getMeetingNumber(meeting.meetingId)
        )

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

    override fun muteAllParticipants(
        participants: List<MeetingParticipantUi>,
        meetingId: String
    ): List<MeetingParticipantUi> {
        val targetIds = participants.filterNot { it.isSelf }.map { it.userId }
        DataRepository.recordMeetingAction(
            actionType = MeetingActionTypes.MUTE_ALL,
            meetingId = meetingId,
            targetUserIds = targetIds
        )
        return participants.map { participant ->
            if (participant.isSelf) participant else participant.copy(isMuted = true)
        }
    }

    override fun askAllToUnmute(
        participants: List<MeetingParticipantUi>,
        meetingId: String
    ): List<MeetingParticipantUi> {
        val targetIds = participants.filterNot { it.isSelf }.map { it.userId }
        DataRepository.recordMeetingAction(
            actionType = MeetingActionTypes.UNMUTE_ALL,
            meetingId = meetingId,
            targetUserIds = targetIds
        )
        return participants.map { participant ->
            if (participant.isSelf) participant else participant.copy(isMuted = false)
        }
    }

    override fun askParticipantToUnmute(
        participants: List<MeetingParticipantUi>,
        meetingId: String,
        targetUserId: String
    ): List<MeetingParticipantUi> {
        if (targetUserId.isBlank()) return participants
        DataRepository.recordMeetingAction(
            actionType = MeetingActionTypes.PARTICIPANT_UNMUTE,
            meetingId = meetingId,
            targetUserIds = listOf(targetUserId)
        )
        return participants.map { participant ->
            if (participant.userId == targetUserId) {
                participant.copy(isMuted = false)
            } else {
                participant
            }
        }
    }

    override fun inviteContacts(
        meetingId: String,
        selectedContactIds: Set<String>
    ) {
        DataRepository.inviteContactsToMeeting(meetingId, selectedContactIds)
    }

    override fun copyInviteLink(meetingId: String): String {
        val inviteLink = DataRepository.getMeetingInviteLink(meetingId)
        DataRepository.recordMeetingAction(
            actionType = MeetingActionTypes.COPY_INVITE_LINK,
            meetingId = meetingId,
            note = inviteLink
        )
        return inviteLink
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
