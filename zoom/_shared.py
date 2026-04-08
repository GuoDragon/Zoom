from __future__ import annotations

import json
import logging
import os
import pathlib
import re
from datetime import date, datetime, timedelta, timezone
from functools import lru_cache

from appsim.utils import read_json_from_device

PACKAGE_NAME = "com.example.zoom"
CURRENT_USER_ID = "user001"
PERSONAL_MEETING_NUMBER = "9948881080"

RUNTIME_SCHEDULED_MEETINGS_FILE = "runtime_scheduled_meetings.json"
RUNTIME_INSTANT_MEETINGS_FILE = "runtime_instant_meetings.json"
RUNTIME_CHAT_MESSAGES_FILE = "runtime_chat_messages.json"
RUNTIME_DIRECT_MESSAGES_FILE = "runtime_direct_messages.json"
RUNTIME_MEETING_ACTIONS_FILE = "runtime_meeting_actions.json"
RUNTIME_PROFILE_STATE_FILE = "runtime_profile_state.json"

ACTION_INVITE_CONTACTS = "INVITE_CONTACTS"
ACTION_SCREEN_SHARE_STATUS_CHANGED = "SCREEN_SHARE_STATUS_CHANGED"
ACTION_SAFE_DRIVING_VOICE_NOTE = "SAFE_DRIVING_VOICE_NOTE"
ACTION_COPY_INVITE_LINK = "COPY_INVITE_LINK"
ACTION_UNMUTE_ALL = "UNMUTE_ALL"
ACTION_RAISE_HAND = "RAISE_HAND"
ACTION_LOWER_HAND = "LOWER_HAND"
ACTION_EMOJI_REACTION = "EMOJI_REACTION"
ACTION_MEETING_STARTED = "MEETING_STARTED"
ACTION_MEETING_EXITED = "MEETING_EXITED"
ACTION_MEETING_MEDIA_STATE_CHANGED = "MEETING_MEDIA_STATE_CHANGED"

FIXTURES_DIR = pathlib.Path(__file__).resolve().parent / "fixtures"
SHANGHAI_TZ = timezone(timedelta(hours=8))
RESULT_SKIP_KEYS = {"point", "path", "screenshot_path", "image"}
_RUNTIME_CACHE: dict[tuple[int, str, str | None, str | None], object] = {}

LATE_MESSAGE_KEYWORD_GROUPS = [['迟到', '十分钟'], ['late', '10'], ['late', 'ten']]
LEAVE_MESSAGE_KEYWORD_GROUPS = [['下周一', '请假'], ['下周一', '请个假'], ['next monday', 'leave'], ['next monday', 'miss']]


def _build_backup_dir(task_id: int, backup_dir: str | None) -> str:
    if backup_dir:
        return backup_dir
    return os.path.join(os.getcwd(), "scripts", "zoom", f"task_{task_id:02d}")


def _load_fixture_json(filename: str):
    with (FIXTURES_DIR / filename).open("r", encoding="utf-8") as handle:
        return json.load(handle)


@lru_cache(maxsize=1)
def _fixture_context() -> dict:
    users = _load_fixture_json("users.json")
    users_by_id = {
        str(user.get("userId")): user
        for user in users
        if isinstance(user, dict) and user.get("userId")
    }
    users_by_name = {
        str(user.get("username")): user
        for user in users
        if isinstance(user, dict) and user.get("username")
    }
    contact_ids = {user_id for user_id in users_by_id if user_id != CURRENT_USER_ID}
    return {
        "users": users,
        "users_by_id": users_by_id,
        "users_by_name": users_by_name,
        "contact_ids": contact_ids,
    }


def _find_user_id(name: str) -> str:
    user = _fixture_context()["users_by_name"].get(name, {})
    return str(user.get("userId", ""))


def _read_runtime_json(task_id: int, filename: str, device_id: str | None, backup_dir: str | None):
    cache_key = (task_id, filename, device_id, backup_dir)
    if cache_key not in _RUNTIME_CACHE:
        resolved_backup_dir = _build_backup_dir(task_id, backup_dir)
        _RUNTIME_CACHE[cache_key] = read_json_from_device(
            device_id=device_id,
            package_name=PACKAGE_NAME,
            device_json_path=f"files/{filename}",
            backup_dir=resolved_backup_dir,
        )
    return _RUNTIME_CACHE[cache_key]


def _as_list(value) -> list:
    return value if isinstance(value, list) else []


def _as_dict(value) -> dict:
    return value if isinstance(value, dict) else {}


def _scheduled_meetings(task_id: int, device_id: str | None, backup_dir: str | None) -> list[dict]:
    return [item for item in _as_list(_read_runtime_json(task_id, RUNTIME_SCHEDULED_MEETINGS_FILE, device_id, backup_dir)) if isinstance(item, dict)]


def _instant_meetings(task_id: int, device_id: str | None, backup_dir: str | None) -> list[dict]:
    return [item for item in _as_list(_read_runtime_json(task_id, RUNTIME_INSTANT_MEETINGS_FILE, device_id, backup_dir)) if isinstance(item, dict)]


def _chat_messages(task_id: int, device_id: str | None, backup_dir: str | None) -> list[dict]:
    return [item for item in _as_list(_read_runtime_json(task_id, RUNTIME_CHAT_MESSAGES_FILE, device_id, backup_dir)) if isinstance(item, dict)]


def _direct_messages(task_id: int, device_id: str | None, backup_dir: str | None) -> list[dict]:
    return [item for item in _as_list(_read_runtime_json(task_id, RUNTIME_DIRECT_MESSAGES_FILE, device_id, backup_dir)) if isinstance(item, dict)]


def _meeting_actions(task_id: int, device_id: str | None, backup_dir: str | None) -> list[dict]:
    return [item for item in _as_list(_read_runtime_json(task_id, RUNTIME_MEETING_ACTIONS_FILE, device_id, backup_dir)) if isinstance(item, dict)]


def _profile_state(task_id: int, device_id: str | None, backup_dir: str | None) -> dict:
    return _as_dict(_read_runtime_json(task_id, RUNTIME_PROFILE_STATE_FILE, device_id, backup_dir))


def _append_named_strings(source: dict, keys: tuple[str, ...], chunks: list[str]) -> None:
    for key in keys:
        value = source.get(key)
        if isinstance(value, str):
            text = value.strip()
            if text:
                chunks.append(text)


def _collect_all_strings(value, chunks: list[str]) -> None:
    if isinstance(value, str):
        text = value.strip()
        if text:
            chunks.append(text)
        return
    if isinstance(value, dict):
        for key, item in value.items():
            if key in RESULT_SKIP_KEYS:
                continue
            _collect_all_strings(item, chunks)
        return
    if isinstance(value, list):
        for item in value:
            _collect_all_strings(item, chunks)


def _extract_result_text(result) -> str:
    if not isinstance(result, dict):
        return ""
    chunks: list[str] = []
    _append_named_strings(result, ("final_answer", "answer", "content", "message", "final_message", "summary"), chunks)
    for action in _as_list(result.get("executed_actions")):
        if isinstance(action, dict):
            _append_named_strings(action, ("content", "message", "text", "thought", "reason", "observation", "status", "description"), chunks)
    return "\n".join(chunks)


def _extract_result_broad_text(result) -> str:
    if not isinstance(result, dict):
        return ""
    chunks: list[str] = []
    _collect_all_strings(result, chunks)
    return "\n".join(chunks)


def _normalize_text(text: str) -> str:
    return re.sub(r"\s+", " ", text).strip().lower()


def _text_contains_all(text: str, keywords: list[str]) -> bool:
    normalized = _normalize_text(text)
    return all(_normalize_text(keyword) in normalized for keyword in keywords)


def _text_contains_any(text: str, keywords: tuple[str, ...] | list[str]) -> bool:
    normalized = _normalize_text(text)
    return any(_normalize_text(keyword) in normalized for keyword in keywords)


def _text_matches_groups(text: str, groups: list[list[str]]) -> bool:
    normalized = _normalize_text(text)
    return any(all(_normalize_text(keyword) in normalized for keyword in group) for group in groups)


def _result_success(result) -> bool:
    return isinstance(result, dict) and bool(result.get("success"))


def _result_contains_number(result, expected_number: int) -> bool:
    text = _extract_result_text(result)
    if not text:
        return False
    candidates = {str(expected_number), f"{expected_number:,}"}
    for candidate in candidates:
        if re.search(rf"(?<!\d){re.escape(candidate)}(?!\d)", text):
            return True
    return False


def _result_contains_any(result, keywords: tuple[str, ...] | list[str], broad: bool = False) -> bool:
    text = _extract_result_broad_text(result) if broad else _extract_result_text(result)
    if not text:
        return False
    return _text_contains_any(text, keywords)


def _result_mentions_media_setup(result) -> bool:
    text = _extract_result_broad_text(result)
    if not text:
        return False
    has_mic = _text_contains_any(text, ("mic", "microphone", "???"))
    has_camera = _text_contains_any(text, ("camera", "video", "???"))
    has_audio_off = _text_contains_any(text, ("audio off", "disconnect audio", "????", "mute audio"))
    return has_mic and has_camera and has_audio_off


def _to_local_datetime(timestamp_ms) -> datetime | None:
    try:
        return datetime.fromtimestamp(int(timestamp_ms) / 1000, SHANGHAI_TZ)
    except Exception:
        return None


def _matches_local_slot(timestamp_ms, target_date: date, hour: int, minute: int, tolerance_minutes: int = 1) -> bool:
    actual = _to_local_datetime(timestamp_ms)
    if actual is None:
        return False
    target = datetime(target_date.year, target_date.month, target_date.day, hour, minute, tzinfo=SHANGHAI_TZ)
    return abs((actual - target).total_seconds()) <= tolerance_minutes * 60


def _matches_local_time(timestamp_ms, hour: int, minute: int) -> bool:
    actual = _to_local_datetime(timestamp_ms)
    if actual is None:
        return False
    return actual.hour == hour and actual.minute == minute


def _tomorrow_local_date() -> date:
    return datetime.now(SHANGHAI_TZ).date() + timedelta(days=1)


def _find_latest(records: list[dict], predicate) -> dict | None:
    for item in reversed(records):
        if predicate(item):
            return item
    return None


def _is_seed_meeting(signal: dict, index: int) -> bool:
    return str(signal.get("signalId", "")).endswith(f"seed_{index}")


def _is_any_seed_meeting(signal: dict) -> bool:
    return any(_is_seed_meeting(signal, index) for index in (1, 2, 3))


def _find_seed_meeting(task_id: int, device_id: str | None, backup_dir: str | None, index: int) -> dict | None:
    return _find_latest(_scheduled_meetings(task_id, device_id, backup_dir), lambda signal: _is_seed_meeting(signal, index))


def _invitee_ids(meeting: dict) -> set[str]:
    return {str(user_id) for user_id in _as_list(meeting.get("inviteeUserIds")) if user_id}


def _find_instant_session(task_id: int, device_id: str | None, backup_dir: str | None, *, source: str | None = None, meeting_number: str | None = None, use_personal_meeting_id: bool | None = None) -> dict | None:
    sessions = _instant_meetings(task_id, device_id, backup_dir)

    def predicate(session: dict) -> bool:
        if source and str(session.get("source", "")) != source:
            return False
        if meeting_number and str(session.get("meetingNumber", "")) != str(meeting_number):
            return False
        if use_personal_meeting_id is not None and bool(session.get("usePersonalMeetingId")) != use_personal_meeting_id:
            return False
        return True

    return _find_latest(sessions, predicate)


def _find_meeting_action(task_id: int, device_id: str | None, backup_dir: str | None, *, action_type: str, meeting_id: str | None = None, meeting_number: str | None = None, required_target_ids: list[str] | None = None, emoji: str | None = None, note_keywords: list[str] | None = None, screen_sharing_enabled: bool | None = None, microphone_on: bool | None = None, camera_on: bool | None = None, audio_option: str | None = None, exit_action: str | None = None, media_change_source: str | None = None) -> dict | None:
    required_targets = {str(target_id) for target_id in (required_target_ids or []) if target_id}
    actions = _meeting_actions(task_id, device_id, backup_dir)

    def predicate(action: dict) -> bool:
        if str(action.get("actionType", "")) != action_type:
            return False
        if meeting_id and str(action.get("meetingId", "")) != str(meeting_id):
            return False
        if meeting_number and str(action.get("meetingNumber", "")) != str(meeting_number):
            return False
        target_ids = {str(target_id) for target_id in _as_list(action.get("targetUserIds")) if target_id}
        if required_targets and not required_targets.issubset(target_ids):
            return False
        if emoji is not None and str(action.get("emoji", "")) != emoji:
            return False
        if screen_sharing_enabled is not None and action.get("screenSharingEnabled") != screen_sharing_enabled:
            return False
        if microphone_on is not None and action.get("microphoneOn") != microphone_on:
            return False
        if camera_on is not None and action.get("cameraOn") != camera_on:
            return False
        if audio_option is not None and str(action.get("audioOption", "")) != audio_option:
            return False
        if exit_action is not None and str(action.get("exitAction", "")) != exit_action:
            return False
        if media_change_source is not None and str(action.get("mediaChangeSource", "")) != media_change_source:
            return False
        if note_keywords and not _text_contains_all(str(action.get("note", "")), note_keywords):
            return False
        return True

    return _find_latest(actions, predicate)


def _resolve_effective_media_state(
    task_id: int,
    device_id: str | None,
    backup_dir: str | None,
    meeting_id: str,
) -> dict | None:
    if not meeting_id:
        return None
    latest_media_change = _find_meeting_action(
        task_id,
        device_id,
        backup_dir,
        action_type=ACTION_MEETING_MEDIA_STATE_CHANGED,
        meeting_id=meeting_id,
    )
    if latest_media_change is not None:
        return latest_media_change
    return _find_meeting_action(
        task_id,
        device_id,
        backup_dir,
        action_type=ACTION_MEETING_STARTED,
        meeting_id=meeting_id,
    )


def _media_state_matches(
    media_state: dict | None,
    *,
    microphone_on: bool | None = None,
    camera_on: bool | None = None,
    audio_option: str | None = None,
    media_change_source: str | None = None,
) -> bool:
    if not isinstance(media_state, dict):
        return False
    if microphone_on is not None and media_state.get("microphoneOn") != microphone_on:
        return False
    if camera_on is not None and media_state.get("cameraOn") != camera_on:
        return False
    if audio_option is not None and str(media_state.get("audioOption", "")) != audio_option:
        return False
    if media_change_source is not None and str(media_state.get("mediaChangeSource", "")) != media_change_source:
        return False
    return True


def _chat_message_exists(task_id: int, device_id: str | None, backup_dir: str | None, *, meeting_ids: set[str] | None = None, content_exact: str | None = None, keyword_groups: list[list[str]] | None = None) -> bool:
    normalized_exact = _normalize_text(content_exact) if content_exact is not None else None
    for message in reversed(_chat_messages(task_id, device_id, backup_dir)):
        if str(message.get("senderId", "")) != CURRENT_USER_ID:
            continue
        if meeting_ids and str(message.get("meetingId", "")) not in meeting_ids:
            continue
        content = str(message.get("content", ""))
        if normalized_exact is not None and _normalize_text(content) != normalized_exact:
            continue
        if keyword_groups and not _text_matches_groups(content, keyword_groups):
            continue
        return True
    return False


def _direct_message_exists(task_id: int, device_id: str | None, backup_dir: str | None, *, partner_user_id: str, keyword_groups: list[list[str]]) -> bool:
    for message in reversed(_direct_messages(task_id, device_id, backup_dir)):
        if str(message.get("senderId", "")) != CURRENT_USER_ID:
            continue
        if str(message.get("partnerUserId", "")) != str(partner_user_id):
            continue
        if _text_matches_groups(str(message.get("content", "")), keyword_groups):
            return True
    return False


def evaluate_task(task_id: int, result=None, device_id=None, backup_dir=None, **kwargs) -> bool:
    fixtures = _fixture_context()
    amber_id = _find_user_id("Amber Campbell")
    derek_id = _find_user_id("Derek Stewart")
    brittany_id = _find_user_id("Brittany Evans")
    natalie_id = _find_user_id("Natalie Cox")
    all_contact_ids = fixtures["contact_ids"]

    if task_id == 1:
        return _find_instant_session(task_id, device_id, backup_dir, source="HOST", meeting_number=PERSONAL_MEETING_NUMBER, use_personal_meeting_id=True) is not None

    if task_id == 2:
        host_session = _find_instant_session(task_id, device_id, backup_dir, source="HOST")
        return bool(host_session and amber_id and _find_meeting_action(task_id, device_id, backup_dir, action_type=ACTION_INVITE_CONTACTS, meeting_id=str(host_session.get("signalId", "")), required_target_ids=[amber_id]))

    if task_id == 3:
        host_session = _find_instant_session(task_id, device_id, backup_dir, source="HOST")
        host_meeting_id = str(host_session.get("signalId", "")) if host_session else ""
        return bool(
            host_session
            and _find_meeting_action(task_id, device_id, backup_dir, action_type=ACTION_MEETING_STARTED, meeting_id=host_meeting_id)
            and _find_meeting_action(task_id, device_id, backup_dir, action_type=ACTION_COPY_INVITE_LINK, meeting_id=host_meeting_id)
            and _find_meeting_action(task_id, device_id, backup_dir, action_type=ACTION_MEETING_EXITED, meeting_id=host_meeting_id, exit_action="LEAVE_SELF")
        )

    if task_id == 4:
        host_session = _find_instant_session(task_id, device_id, backup_dir, source="HOST")
        return bool(host_session and _find_meeting_action(task_id, device_id, backup_dir, action_type=ACTION_SCREEN_SHARE_STATUS_CHANGED, meeting_id=str(host_session.get("signalId", "")), screen_sharing_enabled=True))

    if task_id == 5:
        host_session = _find_instant_session(task_id, device_id, backup_dir, source="HOST")
        host_meeting_id = str(host_session.get("signalId", "")) if host_session else ""
        return bool(
            host_session
            and _find_meeting_action(task_id, device_id, backup_dir, action_type=ACTION_MEETING_STARTED, meeting_id=host_meeting_id)
            and _find_meeting_action(task_id, device_id, backup_dir, action_type=ACTION_SAFE_DRIVING_VOICE_NOTE, meeting_id=host_meeting_id, note_keywords=["hello"])
            and _find_meeting_action(task_id, device_id, backup_dir, action_type=ACTION_MEETING_EXITED, meeting_id=host_meeting_id)
        )

    if task_id == 6:
        host_session = _find_instant_session(task_id, device_id, backup_dir, source="HOST", use_personal_meeting_id=False)
        host_meeting_id = str(host_session.get("signalId", "")) if host_session else ""
        media_state = _resolve_effective_media_state(task_id, device_id, backup_dir, host_meeting_id)
        return bool(
            host_session
            and _media_state_matches(
                media_state,
                microphone_on=True,
                camera_on=True,
                audio_option="none",
            )
        )

    if task_id == 7:
        host_sessions = {str(session.get("signalId")) for session in _instant_meetings(task_id, device_id, backup_dir) if str(session.get("source", "")) == "HOST" and session.get("signalId")}
        return bool(host_sessions and _chat_message_exists(task_id, device_id, backup_dir, meeting_ids=host_sessions, content_exact="I'm lcl."))

    if task_id == 8:
        return str(_profile_state(task_id, device_id, backup_dir).get("availability", "")) == "Busy"

    if task_id == 9:
        return str(_profile_state(task_id, device_id, backup_dir).get("displayName", "")) == "Liu Chenlong"

    if task_id == 10:
        join_session = _find_instant_session(task_id, device_id, backup_dir, source="JOIN", meeting_number="994488281")
        join_meeting_id = str(join_session.get("signalId", "")) if join_session else ""
        media_state = _resolve_effective_media_state(task_id, device_id, backup_dir, join_meeting_id)
        return bool(
            join_session
            and _media_state_matches(
                media_state,
                camera_on=True,
            )
        )

    if task_id == 11:
        return _find_meeting_action(task_id, device_id, backup_dir, action_type=ACTION_UNMUTE_ALL, meeting_number="389257198") is not None

    if task_id == 12:
        return all((_find_meeting_action(task_id, device_id, backup_dir, action_type=action_type, meeting_number="927650367", emoji=emoji) is not None) for action_type, emoji in ((ACTION_RAISE_HAND, None), (ACTION_LOWER_HAND, None), (ACTION_EMOJI_REACTION, "\U0001f44d")))

    if task_id == 13:
        return _result_contains_number(result, len(all_contact_ids))

    if task_id == 14:
        return _result_contains_number(result, 3)

    if task_id == 15:
        required_invitees = {derek_id, brittany_id} - {""}
        tomorrow = _tomorrow_local_date()
        return _find_latest(_scheduled_meetings(task_id, device_id, backup_dir), lambda meeting: (not _is_any_seed_meeting(meeting) and _matches_local_slot(meeting.get("startTime"), tomorrow, 19, 0) and required_invitees.issubset(_invitee_ids(meeting)))) is not None

    if task_id == 16:
        return _find_latest(_scheduled_meetings(task_id, device_id, backup_dir), lambda meeting: (not _is_any_seed_meeting(meeting) and str(meeting.get("repeat", "")) == "Every day" and _matches_local_time(meeting.get("startTime"), 10, 0) and _invitee_ids(meeting) == all_contact_ids)) is not None

    if task_id == 17:
        return _find_seed_meeting(task_id, device_id, backup_dir, 3) is None

    if task_id == 18:
        return bool(natalie_id and _direct_message_exists(task_id, device_id, backup_dir, partner_user_id=natalie_id, keyword_groups=LEAVE_MESSAGE_KEYWORD_GROUPS))

    if task_id == 19:
        seed_meeting = _find_seed_meeting(task_id, device_id, backup_dir, 2)
        return bool(seed_meeting and _matches_local_slot(seed_meeting.get("startTime"), _tomorrow_local_date(), 13, 0))

    if task_id == 20:
        seed_meeting = _find_seed_meeting(task_id, device_id, backup_dir, 2)
        return bool(seed_meeting and bool(seed_meeting.get("waitingRoomEnabled")) and int(seed_meeting.get("durationMinutes", 0)) == 240)

    if task_id == 21:
        seed_meeting = _find_seed_meeting(task_id, device_id, backup_dir, 1)
        seed_meeting_id = str(seed_meeting.get("signalId", "")) if seed_meeting else ""
        return bool(
            seed_meeting
            and _find_meeting_action(
                task_id,
                device_id,
                backup_dir,
                action_type=ACTION_MEETING_STARTED,
                meeting_id=seed_meeting_id,
            )
        )

    if task_id == 22:
        seed_meeting = _find_seed_meeting(task_id, device_id, backup_dir, 1)
        return bool(seed_meeting and _chat_message_exists(task_id, device_id, backup_dir, meeting_ids={str(seed_meeting.get("signalId", ""))}, keyword_groups=LATE_MESSAGE_KEYWORD_GROUPS))

    if task_id == 23:
        seed_meeting = _find_seed_meeting(task_id, device_id, backup_dir, 1)
        return bool(seed_meeting and _invitee_ids(seed_meeting) == all_contact_ids)

    logging.error("Unsupported Zoom task ID: %s", task_id)
    return False

