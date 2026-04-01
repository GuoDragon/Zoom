package com.example.zoom.presentation.meetingparticipantsdetailed

enum class ParticipantsSubPage {
    PARTICIPANTS, ADD_INVITE, SEND_MESSAGE, INVITE_CONTACTS, PARTICIPANT_MORE
}

data class MeetingParticipantUi(
    val userId: String,
    val name: String,
    val initials: String,
    val avatarColor: Long,
    val roleTag: String,
    val isMuted: Boolean,
    val isVideoOff: Boolean,
    val isHost: Boolean,
    val isSelf: Boolean
)

data class ContactUi(
    val userId: String,
    val name: String,
    val initials: String,
    val avatarColor: Long,
    val phone: String
)

data class InviteOptionUi(val label: String, val iconName: String)

data class MeetingParticipantsDetailedUiState(
    val participantCount: Int,
    val participants: List<MeetingParticipantUi>,
    val inviteOptions: List<InviteOptionUi>,
    val inviteMessageText: String,
    val allContacts: List<ContactUi>,
    val meetingId: String
)

interface MeetingParticipantsDetailedContract {
    interface View {
        fun showContent(content: MeetingParticipantsDetailedUiState)
    }

    interface Presenter {
        fun loadData()
        fun muteAllParticipants(
            participants: List<MeetingParticipantUi>,
            meetingId: String
        ): List<MeetingParticipantUi>
        fun askAllToUnmute(
            participants: List<MeetingParticipantUi>,
            meetingId: String
        ): List<MeetingParticipantUi>
        fun inviteContacts(
            meetingId: String,
            selectedContactIds: Set<String>
        )
        fun copyInviteLink(meetingId: String): String
    }
}
