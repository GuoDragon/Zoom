package com.example.zoom.navigation

import com.example.zoom.ui.components.MeetingSessionConfig

enum class MeetingExitAction {
    END_FOR_ALL,
    LEAVE_SELF
}

sealed class Screen(val route: String) {
    object Search : Screen("search")
    object Home : Screen("home")
    object TeamChat : Screen("team_chat")
    object Documents : Screen("documents")
    object Calendar : Screen("calendar")
    object Mail : Screen("mail")
    object Profile : Screen("profile")
    object DetailedInfo : Screen("detailed_info")
    object Settings : Screen("settings")
    object HostMeeting : Screen("host_meeting")
    object MeetingPreview : Screen("meeting_preview") {
        const val meetingIdArg = "meetingId"
        const val microphoneArg = "microphoneOn"
        const val cameraArg = "cameraOn"
        const val audioArg = "audioOption"
        val routePattern =
            "$route?$meetingIdArg={$meetingIdArg}&$microphoneArg={$microphoneArg}&$cameraArg={$cameraArg}&$audioArg={$audioArg}"

        fun createRoute(
            meetingId: String? = null,
            config: MeetingSessionConfig = MeetingSessionConfig(
                microphoneOn = false,
                cameraOn = false,
                audioOption = com.example.zoom.ui.components.MeetingAudioOption.WifiOrCellular
            )
        ): String {
            val resolvedMeetingId = meetingId ?: ""
            return "$route?$meetingIdArg=$resolvedMeetingId&$microphoneArg=${config.microphoneOn}&$cameraArg=${config.cameraOn}&$audioArg=${config.audioOption.routeValue}"
        }
    }
    object LeaveMeeting : Screen("leave_meeting")
    object MeetingDetailed : Screen("meeting_detailed") {
        const val microphoneArg = "microphoneOn"
        const val cameraArg = "cameraOn"
        const val audioArg = "audioOption"
        const val screenSharingArg = "screenSharingEnabled"
        val routePattern =
            "$route?$microphoneArg={$microphoneArg}&$cameraArg={$cameraArg}&$audioArg={$audioArg}&$screenSharingArg={$screenSharingArg}"

        fun createRoute(config: MeetingSessionConfig): String {
            return "$route?$microphoneArg=${config.microphoneOn}&$cameraArg=${config.cameraOn}&$audioArg=${config.audioOption.routeValue}&$screenSharingArg=${config.screenSharingEnabled}"
        }
    }
    object LeaveMeetingDetailed : Screen("leave_meeting_detailed")
    object MeetingInfoDetailed : Screen("meeting_info_detailed")
    object JoinMeeting : Screen("join_meeting")
    object ScheduleMeeting : Screen("schedule_meeting") {
        const val meetingIdArg = "meetingId"
        val routePattern = "$route?$meetingIdArg={$meetingIdArg}"

        fun createRoute(meetingId: String? = null): String {
            return if (meetingId.isNullOrBlank()) route else "$route?$meetingIdArg=$meetingId"
        }
    }
    object ScheduleMeetingDetailed : Screen("schedule_meeting_detailed") {
        const val meetingIdArg = "meetingId"
        val routePattern = "$route/{$meetingIdArg}"

        fun createRoute(meetingId: String): String {
            return "$route/$meetingId"
        }
    }
    object ScheduleMeetingChat : Screen("schedule_meeting_chat") {
        const val meetingIdArg = "meetingId"
        val routePattern = "$route/{$meetingIdArg}"

        fun createRoute(meetingId: String): String {
            return "$route/$meetingId"
        }
    }
    object ScheduleMeetingDetailedInCalendar : Screen("schedule_meeting_detailed_in_calendar") {
        const val meetingIdArg = "meetingId"
        val routePattern = "$route/{$meetingIdArg}"

        fun createRoute(meetingId: String): String {
            return "$route/$meetingId"
        }
    }
    object Contacts : Screen("contacts")
    object DirectChat : Screen("direct_chat") {
        const val userIdArg = "userId"
        val routePattern = "$route/{$userIdArg}"

        fun createRoute(userId: String): String {
            return "$route/$userId"
        }
    }
    object ProfileAvailability : Screen("profile_availability")
    object ProfileDisplayName : Screen("profile_display_name")
}
