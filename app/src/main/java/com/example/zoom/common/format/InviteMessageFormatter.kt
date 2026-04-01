package com.example.zoom.common.format

fun buildMeetingInviteMessageText(
    hostName: String,
    meetingTopic: String,
    meetingId: String
): String {
    return buildString {
        appendLine("$hostName is inviting you to a scheduled Zoom meeting.")
        appendLine()
        appendLine("Topic: $meetingTopic")
        appendLine("Meeting ID: $meetingId")
        appendLine()
        appendLine("Join Zoom Meeting")
        append("https://zoom.us/j/$meetingId")
    }
}
