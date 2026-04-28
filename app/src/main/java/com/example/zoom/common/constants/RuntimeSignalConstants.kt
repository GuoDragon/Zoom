package com.example.zoom.common.constants

object RuntimeSignalFileNames {
    const val RUNTIME_SCHEDULED_MEETINGS = "runtime_scheduled_meetings.json"
    const val RUNTIME_CHAT_MESSAGES = "runtime_chat_messages.json"
    const val RUNTIME_JOIN_HISTORY = "runtime_join_history.json"
    const val RUNTIME_MEETING_ACTIONS = "runtime_meeting_actions.json"
    const val RUNTIME_INSTANT_MEETINGS = "runtime_instant_meetings.json"
    const val RUNTIME_PROFILE_STATE = "runtime_profile_state.json"
    const val RUNTIME_DIRECT_MESSAGES = "runtime_direct_messages.json"
    const val RUNTIME_MEETING_PREFERENCES = "runtime_meeting_preferences.json"
    const val RUNTIME_CHAT_THREAD_STATES = "runtime_chat_thread_states.json"
    const val RUNTIME_CLIPBOARD_ACTIONS = "runtime_clipboard_actions.json"
    const val RUNTIME_META = "runtime_meta.json"
}

object RuntimeSignalPrefixes {
    const val RUNTIME_MEETING_ID_PREFIX = "scheduled_"
    const val INSTANT_MEETING_ID_PREFIX = "instant_"
    const val RUNTIME_MESSAGE_ID_PREFIX = "runtime_msg_"
    const val DIRECT_MESSAGE_ID_PREFIX = "direct_msg_"
    const val JOIN_HISTORY_ACTION_ID_PREFIX = "join_history_"
    const val MEETING_ACTION_ID_PREFIX = "meeting_action_"
}

object JoinHistoryActionTypes {
    const val USED = "USED"
    const val CLEARED = "CLEARED"
}

object MeetingActionTypes {
    const val MEETING_STARTED = "MEETING_STARTED"
    const val MEETING_EXITED = "MEETING_EXITED"
    const val MEETING_MEDIA_STATE_CHANGED = "MEETING_MEDIA_STATE_CHANGED"
    const val MUTE_ALL = "MUTE_ALL"
    const val UNMUTE_ALL = "UNMUTE_ALL"
    const val PARTICIPANT_UNMUTE = "PARTICIPANT_UNMUTE"
    const val LOCK_MEETING = "LOCK_MEETING"
    const val INVITE_CONTACTS = "INVITE_CONTACTS"
    const val SCREEN_SHARE_STATUS_CHANGED = "SCREEN_SHARE_STATUS_CHANGED"
    const val SCREEN_SHARE_PAUSED = "SCREEN_SHARE_PAUSED"
    const val SCREEN_SHARE_RESUMED = "SCREEN_SHARE_RESUMED"
    const val RAISE_HAND = "RAISE_HAND"
    const val LOWER_HAND = "LOWER_HAND"
    const val EMOJI_REACTION = "EMOJI_REACTION"
    const val COPY_INVITE_LINK = "COPY_INVITE_LINK"
    const val COPY_MEETING_NUMBER = "COPY_MEETING_NUMBER"
    const val SAFE_DRIVING_VOICE_NOTE = "SAFE_DRIVING_VOICE_NOTE"
}

object MeetingMediaChangeSources {
    const val MAIN_MICROPHONE_BUTTON = "MAIN_MICROPHONE_BUTTON"
    const val MAIN_CAMERA_BUTTON = "MAIN_CAMERA_BUTTON"
    const val MAIN_AUDIO_MENU = "MAIN_AUDIO_MENU"
    const val SAFE_DRIVING_AUDIO_MENU = "SAFE_DRIVING_AUDIO_MENU"
}
