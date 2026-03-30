package com.example.zoom.presentation.schedulemeeting

interface ScheduleMeetingContract {
    interface View {
        fun showInitialState(state: ScheduleMeetingInitialState)
        fun onMeetingSaved()
    }

    interface Presenter {
        fun loadData()
        fun saveMeeting(draft: ScheduleMeetingDraft)
    }
}

data class ScheduleMeetingInitialState(
    val draft: ScheduleMeetingDraft,
    val personalMeetingId: String,
    val repeatOptions: List<String>,
    val calendarOptions: List<String>,
    val encryptionOptions: List<ScheduleMeetingEncryptionOption>,
    val timeZoneOptions: List<ScheduleMeetingTimeZoneOption>,
    val inviteeOptions: List<ScheduleMeetingInviteeOption>
)

data class ScheduleMeetingDraft(
    val meetingTitle: String,
    val startTimeMillis: Long,
    val durationMinutes: Int,
    val timeZoneId: String,
    val repeat: String,
    val calendar: String,
    val usePersonalMeetingId: Boolean,
    val requirePasscode: Boolean,
    val passcode: String,
    val waitingRoom: Boolean,
    val encryption: String,
    val inviteeUserIds: Set<String>,
    val continuousMeetingChat: Boolean,
    val hostVideoOn: Boolean,
    val participantVideoOn: Boolean
)

data class ScheduleMeetingTimeZoneOption(
    val id: String,
    val displayName: String,
    val gmtOffsetLabel: String,
    val alphabetBucket: Char
)

data class ScheduleMeetingEncryptionOption(
    val value: String,
    val summary: String? = null,
    val detail: String? = null
)

data class ScheduleMeetingInviteeOption(
    val userId: String,
    val name: String,
    val email: String
)

enum class ScheduleMeetingSubPage {
    MAIN,
    TIME_ZONE,
    REPEAT,
    CALENDAR,
    ENCRYPTION,
    ADD_INVITEES
}
