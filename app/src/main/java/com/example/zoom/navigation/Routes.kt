package com.example.zoom.navigation

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
    object MeetingPreview : Screen("meeting_preview")
    object LeaveMeeting : Screen("leave_meeting")
    object MeetingDetailed : Screen("meeting_detailed")
    object LeaveMeetingDetailed : Screen("leave_meeting_detailed")
    object JoinMeeting : Screen("join_meeting")
    object ScheduleMeeting : Screen("schedule_meeting")
}
