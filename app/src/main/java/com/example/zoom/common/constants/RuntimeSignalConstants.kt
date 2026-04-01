package com.example.zoom.common.constants

object RuntimeSignalFileNames {
    const val RUNTIME_SCHEDULED_MEETINGS = "runtime_scheduled_meetings.json"
    const val RUNTIME_CHAT_MESSAGES = "runtime_chat_messages.json"
    const val RUNTIME_JOIN_HISTORY = "runtime_join_history.json"
    const val RUNTIME_MEETING_ACTIONS = "runtime_meeting_actions.json"
}

object RuntimeSignalPrefixes {
    const val RUNTIME_MEETING_ID_PREFIX = "scheduled_"
    const val RUNTIME_MESSAGE_ID_PREFIX = "runtime_msg_"
    const val JOIN_HISTORY_ACTION_ID_PREFIX = "join_history_"
    const val MEETING_ACTION_ID_PREFIX = "meeting_action_"
}

object JoinHistoryActionTypes {
    const val USED = "USED"
    const val CLEARED = "CLEARED"
}

object MeetingActionTypes {
    const val MUTE_ALL = "MUTE_ALL"
    const val ASK_ALL_TO_UNMUTE = "ASK_ALL_TO_UNMUTE"
    const val INVITE_CONTACTS = "INVITE_CONTACTS"
    const val SCREEN_SHARE_STATUS_CHANGED = "SCREEN_SHARE_STATUS_CHANGED"
}
