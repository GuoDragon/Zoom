package com.example.zoom.presentation.schedulemeeting

import com.example.zoom.data.DataRepository
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ScheduleMeetingPresenter(
    private val view: ScheduleMeetingContract.View
) : ScheduleMeetingContract.Presenter {

    override fun loadData() {
        val user = DataRepository.getCurrentUser()
        val defaultZoneId = if (ZoneId.getAvailableZoneIds().contains(DEFAULT_TIME_ZONE_ID)) {
            DEFAULT_TIME_ZONE_ID
        } else {
            ZoneId.systemDefault().id
        }
        val initialState = ScheduleMeetingInitialState(
            draft = ScheduleMeetingDraft(
                meetingTitle = "${user.username}'s Zoom Meeting",
                startTimeMillis = buildDefaultStartTime(defaultZoneId),
                durationMinutes = 30,
                timeZoneId = defaultZoneId,
                repeat = "None",
                calendar = "iCalendar",
                usePersonalMeetingId = false,
                requirePasscode = true,
                passcode = "d3L4Sh",
                waitingRoom = false,
                encryption = "Enhanced",
                inviteeUserIds = emptySet(),
                continuousMeetingChat = true,
                hostVideoOn = true,
                participantVideoOn = true
            ),
            personalMeetingId = "994 888 1080",
            repeatOptions = listOf(
                "None",
                "Every day",
                "Every week",
                "Every 2 weeks",
                "Every month",
                "Every year"
            ),
            calendarOptions = listOf("None", "iCalendar"),
            encryptionOptions = listOf(
                ScheduleMeetingEncryptionOption(
                    value = "Enhanced",
                    summary = "Encryption key stored in the cloud."
                ),
                ScheduleMeetingEncryptionOption(
                    value = "End-to-end",
                    detail = "Several features will be automatically disabled when using end-to-end encryption, including cloud recording and phone/SIP/H.323 dial-in.",
                    summary = "Encryption key stored on the device."
                )
            ),
            timeZoneOptions = buildTimeZoneOptions(),
            inviteeOptions = DataRepository.getUsers()
                .filter { it.userId != user.userId }
                .sortedBy { it.username.lowercase() }
                .map { userOption ->
                    ScheduleMeetingInviteeOption(
                        userId = userOption.userId,
                        name = userOption.username,
                        email = userOption.email ?: ""
                    )
                }
        )
        view.showInitialState(initialState)
    }

    override fun saveMeeting(draft: ScheduleMeetingDraft) {
        DataRepository.addScheduledMeetingSignal(
            topic = draft.meetingTitle,
            startTime = draft.startTimeMillis,
            durationMinutes = draft.durationMinutes,
            timeZoneId = draft.timeZoneId,
            repeat = draft.repeat,
            calendar = draft.calendar,
            encryption = draft.encryption,
            inviteeUserIds = draft.inviteeUserIds.toList()
        )
        view.onMeetingSaved()
    }

    private fun buildTimeZoneOptions(): List<ScheduleMeetingTimeZoneOption> {
        val now = Instant.now()
        return ZoneId.getAvailableZoneIds()
            .sorted()
            .map { zoneId ->
                val zone = ZoneId.of(zoneId)
                val offset = zone.rules.getOffset(now)
                val totalSeconds = offset.totalSeconds
                val sign = if (totalSeconds >= 0) "+" else "-"
                val absSeconds = kotlin.math.abs(totalSeconds)
                val hours = absSeconds / 3600
                val minutes = (absSeconds % 3600) / 60
                val offsetLabel = "GMT$sign${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
                val displayName = knownTimeZoneAliases[zoneId] ?: zoneId.substringAfterLast('/').replace('_', ' ')
                val bucket = displayName.firstOrNull()?.uppercaseChar() ?: '#'

                ScheduleMeetingTimeZoneOption(
                    id = zoneId,
                    displayName = displayName,
                    gmtOffsetLabel = offsetLabel,
                    alphabetBucket = if (bucket in 'A'..'Z') bucket else '#'
                )
            }
    }

    private fun buildDefaultStartTime(zoneId: String): Long {
        val zone = ZoneId.of(zoneId)
        val now = ZonedDateTime.now(zone)
        val minutesToNextQuarter = (15 - (now.minute % 15)) % 15
        val minuteStep = if (minutesToNextQuarter == 0) 15L else minutesToNextQuarter.toLong()
        val nextQuarter = now
            .plusMinutes(minuteStep)
            .withSecond(0)
            .withNano(0)
        return nextQuarter.toInstant().toEpochMilli()
    }

    companion object {
        private const val DEFAULT_TIME_ZONE_ID = "Asia/Shanghai"

        private val knownTimeZoneAliases = mapOf(
            "Asia/Shanghai" to "Beijing, Shanghai",
            "America/Halifax" to "Atlantic Time (Canada)",
            "Pacific/Auckland" to "Auckland, Wellington",
            "Asia/Almaty" to "Astana, Almaty"
        )
    }
}
