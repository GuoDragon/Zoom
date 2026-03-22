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
    object JoinMeeting : Screen("join_meeting")
    object ScheduleMeeting : Screen("schedule_meeting")

    object SearchMessageDetail : Screen("search_message_detail/{meetingId}") {
        fun createRoute(meetingId: String) = "search_message_detail/$meetingId"
    }

    object SearchChatDetail : Screen("search_chat_detail/{meetingId}") {
        fun createRoute(meetingId: String) = "search_chat_detail/$meetingId"
    }

    object SearchMeetingDetail : Screen("search_meeting_detail/{meetingId}") {
        fun createRoute(meetingId: String) = "search_meeting_detail/$meetingId"
    }
}
