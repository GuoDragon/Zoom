# Zoom Android Template

This project is a static Zoom-like Android app built with Jetpack Compose and MVP-style presentation layers.

## Latest Update (2026-04-01 Automation Seed Scheduled Meetings)

Added cold-start automation seed meetings so AppSim detection tasks can rely on stable upcoming schedule records after each app data reset:

1. `DataRepository.resetRuntimeSignalData()` now preloads 3 runtime scheduled meetings on every fresh app start:
   - tomorrow 08:00
   - tomorrow 12:00
   - the next upcoming May 1 meeting
2. These seeds are stored in `runtime_scheduled_meetings.json` immediately after reset, alongside the existing runtime reset policy.
3. Seed meetings keep normal Zoom schedule fields (`calendar`, `encryption`, `waitingRoomEnabled`, `inviteeUserIds`) so later schedule-edit automation can modify them directly.
4. Build verification:
   - Re-ran `./gradlew.bat :app:compileDebugKotlin` and confirmed success.

## Latest Update (2026-04-01 More Page Contacts Entry Alignment)

Aligned the newly added page-tree `Contacts Page` under `More Page` with the existing contacts/direct-message flow:

1. Added stable shortcut action identifiers to the shared `More Page` MVP state instead of relying on label text.
2. Wired the bottom-navigation `More Page` shortcut grid so tapping `Contacts` now closes the overlay and opens the existing `Contacts` page.
3. Kept the earlier `Team Chat` floating-action `+ -> Contacts` path as a secondary shortcut entry.
4. Kept `Contacts -> Direct Chat` unchanged, so direct messages still write to `runtime_direct_messages.json`.
5. Build verification:
   - Re-ran `./gradlew.bat :app:compileDebugKotlin` and confirmed success.

## Latest Update (2026-04-01 Detection Task Gap Closure: Instant Meeting Identity + Profile + Contacts)

Implemented the audited detection-task gaps so the remaining host/join/profile/contact/schedule flows are no longer UI-only:

1. Added runtime-backed instant meeting identity flow:
   - `Host Meeting` now creates a real runtime instant-meeting session before preview.
   - `Join Meeting` now binds the typed meeting number to the current meeting context before preview.
   - `Meeting Preview`, `Meeting info`, in-meeting actions, and runtime meeting action logs now use the real meeting number instead of falling back to the default static meeting.
2. Expanded runtime JSON persistence:
   - added `runtime_instant_meetings.json`
   - added `runtime_profile_state.json`
   - added `runtime_direct_messages.json`
   - kept startup reset behavior, so these records are cleared on a fresh run while static contacts remain unchanged
3. Added profile-edit flows in MVP:
   - `Availability` is now editable and supports switching to `Busy`
   - `Display name` is now editable and persists to runtime profile state
   - profile/detail pages refresh from runtime state after edits
4. Added contacts/direct-message flow in MVP:
   - `Team Chat` floating action now opens a dedicated `Contacts` page with total contact count
   - tapping a contact opens a direct-chat page
   - sending a direct message writes to `runtime_direct_messages.json`
   - latest direct-chat threads now appear in `Team Chat`
5. Completed missing meeting/schedule behavior wiring:
   - in-meeting `Raise hand`, `Lower hand`, emoji reactions, safe-driving `Hello`, screen-share toggles, and invite-link copy now write to `runtime_meeting_actions.json`
   - in-meeting `Invite contacts` now updates the live meeting participant source instead of only logging
   - scheduled meetings now persist real `meetingNumber`, `passcode`, `waitingRoomEnabled`, and `usePersonalMeetingId`
   - `Schedule Meeting` invite picker and schedule-detail invite popup now support selecting all contacts
   - `Schedule Meeting Detailed` now shows `Meeting ID` and `Waiting room`, and supports canceling a runtime scheduled meeting
6. Supporting UX improvements:
   - `Home` upcoming meetings header now shows the total count
   - meeting invite text now uses the actual meeting number instead of the internal runtime record id
   - calendar detailed description now includes meeting ID and waiting-room state for runtime schedules
7. Build verification:
   - Ran `./gradlew.bat :app:compileDebugKotlin` successfully.

### New runtime files
- `runtime_scheduled_meetings.json`
- `runtime_instant_meetings.json`
- `runtime_chat_messages.json`
- `runtime_direct_messages.json`
- `runtime_join_history.json`
- `runtime_meeting_actions.json`
- `runtime_profile_state.json`

## Latest Update (2026-04-01 Share Page Input Flow + Screen Share Runtime Signal)

Implemented the latest `开发需求.md` current task for `Share Page`:

1. `Home` `Share` action now opens a dedicated MVP `Share Page` overlay instead of the old local fake-keyboard dialog.
2. `Share Page` behavior now matches the requested flow:
   - tapping the input uses the system keyboard
   - only numeric input is accepted
   - `OK` is enabled only after entering 8 digits
   - tapping `OK` enters the meeting page directly
3. Added runtime screen-share backend signal tracking:
   - entering from `Share Page` writes a `SCREEN_SHARE_STATUS_CHANGED` action with `screenSharingEnabled=true`
   - leaving that meeting writes a matching `screenSharingEnabled=false` action
   - UI does not add extra visible share-state changes
4. Build verification:
   - Re-ran `./gradlew.bat :app:compileDebugKotlin` and confirmed success.

### Modified files
- `app/src/main/java/com/example/zoom/common/constants/RuntimeSignalConstants.kt`
- `app/src/main/java/com/example/zoom/data/DataRepository.kt`
- `app/src/main/java/com/example/zoom/model/MeetingActionSignal.kt`
- `app/src/main/java/com/example/zoom/navigation/Routes.kt`
- `app/src/main/java/com/example/zoom/navigation/NavGraph.kt`
- `app/src/main/java/com/example/zoom/presentation/home/HomeActionComponents.kt`
- `app/src/main/java/com/example/zoom/presentation/home/HomeScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/meetingpreview/MeetingPreviewScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/sharepage/SharePageContract.kt`
- `app/src/main/java/com/example/zoom/presentation/sharepage/SharePagePresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/sharepage/SharePageScreen.kt`
- `app/src/main/java/com/example/zoom/ui/components/MeetingSessionComponents.kt`
- `README.md`

## Latest Update (2026-04-01 Schedule Detailed Invite Message Alignment)

Aligned the `Send a message` text in `Schedule Meeting Deatiled Page` invite popup with in-meeting `Meetint Participents Add Page`:

1. Added shared invite message builder so both pages use the same template and line structure.
2. Updated `MeetingParticipantsDetailedPresenter` to use the shared builder.
3. Updated `ScheduleMeetingDetailedPresenter`/`ScheduleMeetingDetailedUiState`/`ScheduleMeetingDetailedScreen` so popup message content comes from the same source.
4. Build verification:
   - Re-ran `./gradlew.bat :app:compileDebugKotlin` and confirmed success.

### Modified files
- `app/src/main/java/com/example/zoom/common/format/InviteMessageFormatter.kt`
- `app/src/main/java/com/example/zoom/presentation/meetingparticipantsdetailed/MeetingParticipantsDetailedPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingdetailed/ScheduleMeetingDetailedContract.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingdetailed/ScheduleMeetingDetailedPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingdetailed/ScheduleMeetingDetailedScreen.kt`
- `README.md`

## Latest Update (2026-03-31 Schedule Detail Invite Popup + Calendar Description)

Implemented the latest `开发需求.md` current task adjustments:

1. `Schedule Meeting Deatiled Page` invite popup now matches the meeting participants add flow structure, with only two first-level options:
   - `Send a message`
   - `Invite contacts`
   - `Copy invite link` is removed from this page flow.
2. `Schedule Meeting Deatiled in Calendar Page` now displays `Description` content below the core meeting fields.
   - Description is built from existing meeting/schedule signal data in presenter layer (no model schema expansion).
3. Build verification:
   - Re-ran `./gradlew.bat :app:compileDebugKotlin` and confirmed success.

### Modified files
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingdetailed/ScheduleMeetingDetailedScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingdetailedcalendar/ScheduleMeetingDetailedInCalendarContract.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingdetailedcalendar/ScheduleMeetingDetailedInCalendarPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingdetailedcalendar/ScheduleMeetingDetailedInCalendarScreen.kt`
- `README.md`

## Latest Update (2026-03-31 Home Scheduled Time Alignment)

Aligned Home scheduled-meeting time text with actual schedule settings:

1. Home meeting card time text now comes from presenter-provided UI state (`timeLabel`) instead of local UI formatting.
2. For runtime scheduled meetings, Home now builds time text from schedule signal fields:
   - `startTime` + `timeZoneId` via `formatScheduleStartLabel(...)`
   - `durationMinutes` via `formatDurationLabel(...)`
   - Final display: `starts label · duration`
3. Static asset meetings keep the previous `HH:mm` / `HH:mm - HH:mm` display style.
4. Build verification:
   - Re-ran `./gradlew.bat :app:compileDebugKotlin` and confirmed success.

### Modified files
- `app/src/main/java/com/example/zoom/presentation/home/HomeContract.kt`
- `app/src/main/java/com/example/zoom/presentation/home/HomePresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/home/HomeScreen.kt`
- `README.md`

## Latest Update (2026-03-31 Schedule Meeting Flow Tuning + Calendar Detailed Page)

Implemented the new `开发需求.md` latest task adjustments:

1. `Schedule meeting` save behavior changed:
   - Saving a new scheduled meeting now returns directly to `Home`.
2. `Home` scheduled records interaction changed:
   - Removed the right-side `Start` button from scheduled meeting rows.
   - Entire meeting row is now clickable and opens `Schedule Meeting Deatiled Page`.
3. `Schedule Meeting Deatiled Page` edit flow added:
   - Added top-right `Edit` action.
   - `Edit` opens `Schedule Meeting Page` for the same meeting record.
   - Editing save writes back to the same runtime scheduled signal and returns to the detailed page.
4. Meeting preview leave issue fixed:
   - Leaving from preview now uses route-agnostic back-stack exit logic, so exit works from schedule/join/host entry paths.
5. Added `Schedule Meeting Deatiled in Calendar Page`:
   - Calendar meeting cards are now clickable.
   - Clicking a meeting in Calendar opens a dedicated calendar detailed page route.
6. Build verification:
   - Re-ran `./gradlew.bat :app:compileDebugKotlin` and confirmed success.

### Modified files
- `app/src/main/java/com/example/zoom/navigation/Routes.kt`
- `app/src/main/java/com/example/zoom/navigation/NavGraph.kt`
- `app/src/main/java/com/example/zoom/data/DataRepository.kt`
- `app/src/main/java/com/example/zoom/presentation/home/HomeContract.kt`
- `app/src/main/java/com/example/zoom/presentation/home/HomePresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/home/HomeScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/calendar/CalendarContract.kt`
- `app/src/main/java/com/example/zoom/presentation/calendar/CalendarPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/calendar/CalendarScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeeting/ScheduleMeetingContract.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeeting/ScheduleMeetingPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeeting/ScheduleMeetingScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingdetailed/ScheduleMeetingDetailedContract.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingdetailed/ScheduleMeetingDetailedPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingdetailed/ScheduleMeetingDetailedScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingdetailedcalendar/ScheduleMeetingDetailedInCalendarContract.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingdetailedcalendar/ScheduleMeetingDetailedInCalendarPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingdetailedcalendar/ScheduleMeetingDetailedInCalendarScreen.kt`
- `README.md`

## Latest Update (2026-03-31 Schedule Meeting Detailed + Pre-Meeting Chat)

Implemented the latest `开发需求.md` current task for `Schedule Meeting Deatiled Page` and initial seed data:

1. Added full `Schedule Meeting Deatiled Page` flow in MVP:
   - New route and screen: `ScheduleMeetingDetailed`.
   - `Schedule Meeting` save now returns new runtime `meetingId` and navigates to this detail page.
   - Detail page actions:
     - `Start` -> `Meeting Preview` -> existing in-meeting flow.
     - `Chat` -> new full-screen pre-meeting chat page.
     - `Add invitees` -> meeting-style popup (bottom overlay) with search/select and save.
2. Added new pre-meeting chat page in MVP:
   - New route and screen: `ScheduleMeetingChat`.
   - Sending messages writes to runtime JSON via `DataRepository.addRuntimeChatMessage(...)`.
3. Aligned meeting-context continuity:
   - Added `DataRepository` current-meeting context setter/getter.
   - `MeetingPreview` now accepts optional `meetingId` and sets active meeting context.
   - After entering meeting, in-meeting chat reads the same meeting context, so messages sent in pre-meeting chat are visible in meeting message flow.
4. Added repository capabilities for schedule-detail editing:
   - Query runtime scheduled meeting by ID.
   - Update runtime scheduled meeting invitees by ID and persist to runtime scheduled JSON.
5. Added initial static seed data in assets:
   - Contacts: `user051`, `user052`
   - Meetings: `mtg021`, `mtg022`
   - Messages: `msg081`~`msg084`
6. Build verification:
   - Ran `./gradlew.bat :app:compileDebugKotlin` and confirmed success.

### Modified files
- `app/src/main/java/com/example/zoom/navigation/Routes.kt`
- `app/src/main/java/com/example/zoom/navigation/NavGraph.kt`
- `app/src/main/java/com/example/zoom/data/DataRepository.kt`
- `app/src/main/java/com/example/zoom/presentation/home/HomeScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeeting/ScheduleMeetingContract.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeeting/ScheduleMeetingPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeeting/ScheduleMeetingScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/meetingpreview/MeetingPreviewContract.kt`
- `app/src/main/java/com/example/zoom/presentation/meetingpreview/MeetingPreviewPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/meetingpreview/MeetingPreviewScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/meetingdetailed/MeetingDetailedPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingdetailed/ScheduleMeetingDetailedContract.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingdetailed/ScheduleMeetingDetailedPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingdetailed/ScheduleMeetingDetailedScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingchat/ScheduleMeetingChatContract.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingchat/ScheduleMeetingChatPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeetingchat/ScheduleMeetingChatScreen.kt`
- `app/src/main/assets/data/users.json`
- `app/src/main/assets/data/meetings.json`
- `app/src/main/assets/data/messages.json`
- `README.md`

## Latest Update (2026-03-31 Full MVP UI-Layer Decoupling)

Continued the MVP normalization plan and completed UI-layer repository decoupling on main pages:

1. Removed the last UI component direct repository access:
   - `ZoomTopBar` no longer reads `DataRepository` directly.
   - Avatar letter is now passed in from page state (`avatarInitial`) so `ui/components` remains pure render logic.
2. Unified primary tab pages to presenter-driven UI state:
   - Refactored `Team Chat`, `Calendar`, `Docs`, and `Mail` contracts/presenters/screens to use explicit `UiState` payloads that include `currentUserInitial`.
   - Standardized screen initialization by remembering presenter instances and loading data through presenter only.
   - Updated `SearchFilterCatalog` to accept presenter-injected user initials instead of reading repository directly.
3. Kept existing behavior unchanged while normalizing structure:
   - Existing lists/empty states/tab behavior remain the same.
   - Changes are structural and maintainability-focused for future automation compatibility.
4. Build verification:
   - Re-ran `./gradlew.bat :app:compileDebugKotlin` and confirmed success.

### Modified files
- `app/src/main/java/com/example/zoom/ui/components/ZoomTopBar.kt`
- `app/src/main/java/com/example/zoom/presentation/teamchat/TeamChatContract.kt`
- `app/src/main/java/com/example/zoom/presentation/teamchat/TeamChatPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/teamchat/TeamChatScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/calendar/CalendarContract.kt`
- `app/src/main/java/com/example/zoom/presentation/calendar/CalendarPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/calendar/CalendarScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/documents/DocumentsContract.kt`
- `app/src/main/java/com/example/zoom/presentation/documents/DocumentsPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/documents/DocumentsScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/mail/MailContract.kt`
- `app/src/main/java/com/example/zoom/presentation/mail/MailPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/mail/MailScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/home/HomeScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/search/SearchFilterCatalog.kt`
- `app/src/main/java/com/example/zoom/presentation/search/SearchPresenter.kt`
- `README.md`

## Latest Update (2026-03-31 MVP Normalization Closure And i18n Baseline)

Implemented the current normalization plan closure for automation-readiness and maintainability:

1. Completed `Meeting Participants` action flow MVP alignment:
   - Removed direct `DataRepository` writes from `MeetingParticipantsDetailedOverlay`.
   - Added presenter-side action methods for `Mute all`, `Ask all to unmute`, and `Invite contacts` runtime logging.
   - Unified action types with centralized constants (`MeetingActionTypes`) to avoid string drift.
2. Standardized runtime signal infrastructure:
   - Added centralized runtime signal constants in `RuntimeSignalConstants.kt` (file names, ID prefixes, action types).
   - Kept `DataRepository` runtime JSON IO explicitly UTF-8 and pretty-printed (`GsonBuilder#setPrettyPrinting`).
3. Added string resource baseline for key pages (`Home`, `Join meeting`, `Meeting chat`):
   - Migrated major display text/content descriptions to `stringResource(...)`.
   - Added `res/values-zh/strings.xml` for Chinese localization entries.
4. Build verification:
   - Ran `./gradlew.bat :app:compileDebugKotlin` and confirmed success.

### Modified files
- `app/src/main/java/com/example/zoom/common/constants/RuntimeSignalConstants.kt`
- `app/src/main/java/com/example/zoom/data/DataRepository.kt`
- `app/src/main/java/com/example/zoom/presentation/home/HomeContract.kt`
- `app/src/main/java/com/example/zoom/presentation/home/HomePresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/home/HomeScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/joinmeeting/JoinMeetingContract.kt`
- `app/src/main/java/com/example/zoom/presentation/joinmeeting/JoinMeetingPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/joinmeeting/JoinMeetingScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/meetingchatdetailed/MeetingChatDetailedContract.kt`
- `app/src/main/java/com/example/zoom/presentation/meetingchatdetailed/MeetingChatDetailedPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/meetingchatdetailed/MeetingChatDetailedScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/meetingparticipantsdetailed/MeetingParticipantsDetailedContract.kt`
- `app/src/main/java/com/example/zoom/presentation/meetingparticipantsdetailed/MeetingParticipantsDetailedPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/meetingparticipantsdetailed/MeetingParticipantsDetailedOverlay.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-zh/strings.xml`
- `README.md`

## Latest Update (2026-03-30 Runtime Business Signals JSON Integration)

Aligned core mutable business behavior with the same runtime-JSON policy used by scheduled meetings:

1. Added unified runtime signal persistence in `DataRepository` and split files by domain:
   - `runtime_scheduled_meetings.json`
   - `runtime_chat_messages.json`
   - `runtime_join_history.json`
   - `runtime_meeting_actions.json`
2. Added startup reset for all runtime signal files inside `DataRepository.init(...)`, so every Android Studio `Run` (new app process) starts from clean runtime records.
3. Kept static contact data unchanged (`assets/data/users.json` remains read-only and is not cleared on restart).
4. Wired in-meeting chat send actions to persist into `runtime_chat_messages.json` instead of local-only overlay state.
5. Wired Join Meeting history to repository-backed runtime data:
   - selecting a history entry updates usage state
   - tapping `Join` records usage
   - `Clear history` persists clear action
6. Wired meeting participant core actions to runtime action logs (`runtime_meeting_actions.json`):
   - `Mute all`
   - `Ask all to unmute`
   - `Invite contacts` confirm

### Modified files
- `app/src/main/java/com/example/zoom/data/DataRepository.kt`
- `app/src/main/java/com/example/zoom/model/JoinMeetingHistorySignal.kt`
- `app/src/main/java/com/example/zoom/model/MeetingActionSignal.kt`
- `app/src/main/java/com/example/zoom/presentation/meetingchatdetailed/MeetingChatDetailedPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/meetingchatdetailed/MeetingChatDetailedScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/joinmeeting/JoinMeetingPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/joinmeeting/JoinMeetingScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/meetingparticipantsdetailed/MeetingParticipantsDetailedOverlay.kt`
- `README.md`

## Latest Update (2026-03-30 Time Zone Alphabet Index Interaction Fix)

Refined the `Schedule Meeting Time Zone Page` right-side alphabet index so it now behaves as a real jump navigator:

1. Replaced the right-side alphabet from passive display text with clickable `A-Z` index entries.
2. Added list scroll targeting via `LazyListState.animateScrollToItem(...)` so tapping a letter jumps to the corresponding timezone section.
3. Ensured all 26 letters provide feedback: when a tapped letter has no direct section, it now jumps to the nearest available letter section.
4. Added active-letter highlight feedback on the side index during navigation/scrolling.

### Modified files
- `app/src/main/java/com/example/zoom/presentation/schedulemeeting/ScheduleMeetingSubPages.kt`
- `README.md`

## Latest Update (2026-03-30 Schedule Meeting Child Pages And Runtime Signal JSON)

Implemented the latest schedule-meeting expansion task from the requirements document:

1. Completed all newly added child pages under `Schedule Meeting`:
   - `Starts Time` and `Duration` are now popup pickers.
   - `Time Zone`, `Repeat`, `Calendar`, `Encryption`, and `Add invitees` are now full-page sub-screens.
2. `Schedule Meeting Time Zone Page` now uses the full IANA zone list and shows readable city names with GMT offsets.
3. Wired the `Save` action on `Schedule Meeting` to persist a runtime meeting signal and return to `Home`.
4. Added `Home Page After Schedule` behavior: after scheduling, Home now shows the new scheduled record with a `Start` action.
5. Wired the `Start` action in Home scheduled records into the existing `Meeting Preview Page` flow, then continuing to in-meeting as before.
6. Added runtime signal persistence file `scheduled_meetings_runtime.json` in app-internal `files/`, and reset it on each app launch to restore initial state.

### Modified files
- `app/src/main/java/com/example/zoom/model/ScheduledMeetingSignal.kt`
- `app/src/main/java/com/example/zoom/data/DataRepository.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeeting/ScheduleMeetingContract.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeeting/ScheduleMeetingPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeeting/ScheduleMeetingFormatters.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeeting/ScheduleMeetingDialogs.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeeting/ScheduleMeetingSubPages.kt`
- `app/src/main/java/com/example/zoom/presentation/schedulemeeting/ScheduleMeetingScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/home/HomeScreen.kt`
- `app/src/main/java/com/example/zoom/navigation/NavGraph.kt`
- `README.md`

## Latest Update (2026-03-26 Leave Meeting Detailed Exit Fix)

Implemented the latest meeting-exit behavior fix from the requirements document:

1. Fixed `Leave the Meeting Detailed Page` so both `End meeting for all` and `Leave meeting` now respond correctly no matter whether the meeting was opened from `Host Meeting` or `Join Meeting`.
2. Removed the broken route-specific exit behavior that depended on `HostMeeting` being present in the back stack.
3. Added an explicit in-meeting exit action model so `End meeting for all` and `Leave meeting` now travel through separate code paths before returning to `Home`.
4. Centralized detailed meeting-exit cleanup in the app shell so PiP/minimized meeting state is cleared consistently when leaving or ending a meeting.
5. Re-ran `:app:compileDebugKotlin` and confirmed the build succeeds.

### Modified files
- `navigation/Routes.kt`
- `navigation/NavGraph.kt`
- `MainActivity.kt`
- `README.md`

## Latest Update (2026-03-26 Join Meeting Page And History Overlay)

Implemented the latest `Join Meeting Page` refinement task from the requirements document:

1. Updated the meeting ID field to accept digits only and enable `Join` only when exactly 9 digits have been entered.
2. Wired `Join` to reuse the same downstream flow as `Host Meeting`, entering the existing `Meeting Preview` page before continuing into the meeting.
3. Added a `Join Meeting History Page` bottom-sheet overlay opened from the meeting ID dropdown arrow.
4. Added local meeting-history items that can be tapped to quickly fill the meeting number, plus `Clear history` and `Done` actions matching the screenshot pattern.
5. Re-ran `:app:compileDebugKotlin` after the changes and confirmed the build succeeds.

### Modified files
- `presentation/joinmeeting/JoinMeetingContract.kt`
- `presentation/joinmeeting/JoinMeetingPresenter.kt`
- `presentation/joinmeeting/JoinMeetingScreen.kt`
- `navigation/NavGraph.kt`
- `README.md`

## Latest Update (2026-03-26 Meeting Chat Overlay Refinement)

Refined `Meeting Chat Detailed Page` to match the latest screenshot direction:

1. Reworked the meeting chat entry from a standalone full-screen route into an in-meeting bottom overlay that sits above the live meeting screen.
2. Kept the meeting content visible behind the chat panel and left the in-meeting bottom control bar visible below the panel.
3. Restyled the chat panel to match the reference more closely with a drag handle, close button, centered meeting title, `Everyone` and `New chat` shortcuts, centered time marker, compact dark message bubbles, and the screenshot-style message composer.
4. Kept chat interactions local-only: sending a message appends it to the overlay list and scrolls it into view, while the header actions and message action icons remain visual-only.
5. Removed the now-unused dedicated meeting-chat navigation route and re-ran `:app:compileDebugKotlin` after implementation.

### Modified files
- `presentation/meetingchatdetailed/MeetingChatDetailedScreen.kt`
- `presentation/meetingdetailed/MeetingDetailedScreen.kt`
- `navigation/NavGraph.kt`
- `navigation/Routes.kt`
- `README.md`

## Latest Update (2026-03-26 Meeting Emoji Flow And Safe Driving Direction Fix)

Implemented the latest in-meeting refinement task from the requirements document:

1. Updated `Meeting More Detailed Page` emoji behavior so tapping a quick emoji, reaction emoji, effect emoji, or non-verbal feedback emoji now dismisses the popup first and then plays a transient emoji animation on the meeting screen near the lower-middle area.
2. Moved the emoji animation responsibility to `MeetingDetailedScreen`, so the send effect now appears on the underlying meeting UI instead of inside the popup itself.
3. Corrected the safe driving swipe direction so `Meeting Safe Driving Mode Page` behaves as the page on the left side of the main meeting screen.
4. Centered the `Tap to speak` button horizontally and adjusted the safe driving page indicator to reflect the corrected page order.
5. Re-ran `:app:compileDebugKotlin` and confirmed the build succeeds.

### Modified files
- `presentation/meetingdetailed/MeetingDetailedScreen.kt`
- `presentation/meetingdetailed/MeetingSafeDrivingModeScreen.kt`
- `presentation/meetingmoredetailed/MeetingMoreDetailedOverlay.kt`
- `presentation/meetingmoredetailed/MeetingEmojiAnimation.kt`
- `README.md`

## Latest Update (2026-03-26 Safe Driving Mode And Emoji Send Animation)

Implemented the latest in-meeting refinement task from the requirements document:

1. Added emoji send micro-animations to `Meeting More Detailed Page`, so tapping quick emojis and reaction emojis now creates a small upward fade-out send effect near the tapped emoji.
2. Added `Meeting Safe Driving Mode Page` as a second in-meeting mode that appears after swiping left on the main meeting page.
3. Added right-swipe navigation from `Meeting Safe Driving Mode Page` back to the normal meeting page.
4. Reused the existing speaker/audio menu behavior in the safe driving page header and kept the red `End` action wired to the same leave-meeting flow as before.
5. Added a local pulse animation to the central `Tap to speak` button in safe driving mode.

### Modified files
- `presentation/meetingdetailed/MeetingDetailedScreen.kt`
- `presentation/meetingdetailed/MeetingSafeDrivingModeScreen.kt`
- `presentation/meetingmoredetailed/MeetingMoreDetailedOverlay.kt`
- `presentation/meetingmoredetailed/MeetingEmojiAnimation.kt`
- `README.md`

## Latest Update (2026-03-26 Meeting More Pages Compile Fix)

Fixed the Kotlin compilation failure introduced in the new `meetingmorepages` module:

1. Removed the incorrect `androidx.compose.foundation.layout.weight` imports from the new meeting more page files.
2. Kept the existing `Modifier.weight(...)` layout calls unchanged, so the UI structure stayed the same.
3. Re-ran `:app:compileDebugKotlin` and confirmed the build now succeeds.

### Modified files
- `presentation/meetingmorepages/MeetingMorePagesScreens.kt`
- `presentation/meetingmorepages/MeetingMorePagesShared.kt`
- `README.md`

## Latest Update (2026-03-26 Meeting More Child Pages Expansion)

Completed the remaining newly added child pages under `Meeting More Detailed Page` from the updated page tree:

1. Added `Meetint Share Page`, `Meetint Notes Page`, `Meetint Apps Page`, `Meetint Host tools Page`, and `Meetint Settings Page` as real in-meeting child interfaces.
2. Added `Meetint Show CC Page` as an in-meeting bottom toast matching the reference instead of a separate full screen page.
3. Wired all More-grid entries so `Share`, `Show CC`, `Notes`, `Apps`, `Host tools`, and `Settings` now open their own local-state pages or overlays.
4. Kept the new pages local-only with screenshot-level or richer local interactions such as sort toggles, filter cycling, local selection, and local draft state, without introducing deeper navigation.
5. Built the new pages in a separate `meetingmorepages` presentation module to avoid expanding the existing meeting screen files too aggressively.

### Modified files
- `presentation/meetingmorepages/MeetingMorePagesContract.kt`
- `presentation/meetingmorepages/MeetingMorePagesPresenter.kt`
- `presentation/meetingmorepages/MeetingMorePagesShared.kt`
- `presentation/meetingmorepages/MeetingMorePagesOverlays.kt`
- `presentation/meetingmorepages/MeetingMorePagesScreens.kt`
- `presentation/meetingmoredetailed/MeetingMoreDetailedOverlay.kt`
- `presentation/meetingdetailed/MeetingDetailedScreen.kt`
- `README.md`

## Latest Update (2026-03-25 Meeting Info Page Unification)

Unified the in-meeting `Meeting info` experience so the More-grid entry and the standalone `Meeting Info Detailed Page` now share the same screenshot-driven UI:

1. Added a new in-meeting `Meeting info` overlay that opens from `Meeting More Detailed Page`.
2. Reworked `Meeting Info Detailed Page` to use the same dark bottom-sheet layout instead of the old full-screen info page.
3. Updated the meeting info data model with the extra fields required by the latest reference: `Participant ID`, `Encryption`, connection summary text, and `Security settings overview`.
4. Kept `Copy meeting link` as a working local copy action and wired the top-right share icon to the Android system share sheet.

### Modified files
- `presentation/meetinginfodetailed/MeetingInfoDetailedContract.kt`
- `presentation/meetinginfodetailed/MeetingInfoDetailedPresenter.kt`
- `presentation/meetinginfodetailed/MeetingInfoDetailedScreen.kt`
- `presentation/meetinginfodetailed/MeetingInfoUi.kt`
- `presentation/meetingmoredetailed/MeetingMoreDetailedOverlay.kt`
- `presentation/meetingdetailed/MeetingDetailedScreen.kt`
- `README.md`

## Latest Update (2026-03-25 Meeting Participants More Menu Fix)

Adjusted the `Meeting Participants` overlay so the top-right `...` action now matches the latest reference image:

1. Replaced the old `...` behavior that jumped into a participant detail sheet with a small top-right popup menu inside the participants page.
2. Matched the popup structure to the reference with only 2 actions: `Mute all` and `Ask all to unmute`.
3. Added local participant-list state inside the overlay so the two menu actions immediately update mic status icons for all non-self participants.
4. Kept participant-row taps unchanged, so tapping a specific participant still opens that participant's own More page.

### Modified files
- `presentation/meetingparticipantsdetailed/MeetingParticipantsDetailedOverlay.kt` - added popup menu UI and local mute/unmute-all state handling
- `README.md`

## Latest Update (2026-03-25 Meeting Participants UI Optimization)

Optimized the Meeting Participants pages to match the UIReference screenshots:

1. **Participants List Page header** — moved X button to the left, added PersonAdd (invite) and MoreHoriz ("...") icons on the right side of the header bar.
2. **Removed bottom buttons** — removed the "Invite" and "Mute All" blue buttons from the bottom of the participants list.
3. **Invite options reduced** — trimmed from 5 to 3 options: Send a message, Invite contacts, Copy invite link.
4. **More page navigation** — clicking "..." in the header opens the Participant More page for the first non-self participant; clicking a participant row still opens their More page directly.

### Modified files
- `presentation/meetingparticipantsdetailed/MeetingParticipantsDetailedPresenter.kt` — invite options reduced to 3
- `presentation/meetingparticipantsdetailed/MeetingParticipantsDetailedOverlay.kt` — header refactored, bottom buttons removed, onMoreClick added

## Latest Update (2026-03-25 Meeting Participants Pages)

Added 5 sub-pages under the Meeting Participants branch of the page tree:

1. **Participants List Page** — dark bottom sheet showing all meeting participants with colored initials avatars, name/role tags, mic/camera status icons, and Invite/Mute All buttons.
2. **Add/Invite Page** — dark bottom sheet with 5 invite options (Copy invite link, Send a message, Send an email, Invite contacts, Invite room system).
3. **Send Message Page** — displays pre-formatted meeting invitation text with share icon row (Messages, Gmail, Copy).
4. **Invite Contacts Page** — full-screen contact picker with search bar, alphabetically grouped contact list with sticky headers, and checkbox selection.
5. **Participant More Page** — action menu for a selected participant (Chat, Mute, Stop Video, Make Host, Make Co-Host, Put in Waiting Room, Remove, Report).

Entry: Meeting Detailed → More → tap "Participants" grid item → closes More overlay, opens Participants overlay.

### New files (3)
- `presentation/meetingparticipantsdetailed/MeetingParticipantsDetailedContract.kt` — enums, data classes, UI state, MVP contract
- `presentation/meetingparticipantsdetailed/MeetingParticipantsDetailedPresenter.kt` — loads participants, contacts, invite options from DataRepository
- `presentation/meetingparticipantsdetailed/MeetingParticipantsDetailedOverlay.kt` — single overlay composable with 5 internal sub-page composables

### Modified files
- `presentation/meetingmoredetailed/MeetingMoreDetailedOverlay.kt` — added `onParticipantsClick` callback, made grid items clickable
- `presentation/meetingdetailed/MeetingDetailedScreen.kt` — added `showParticipantsOverlay` state, wired participants overlay

## Latest Update (2026-03-24 Meeting More Detailed Page — Raise Hand Style Tweak)

Adjusted the white bottom-sheet emoji row to match the UI reference screenshot:

1. Changed the Raise Hand button from a small circle + bottom label to a wide capsule shape (icon + text side-by-side) with `RoundedCornerShape(24.dp)`.
2. Reduced quick emojis from 5 to 3 (`👍`, `👏`, `❤️`).

### Modified files
- `presentation/meetingmoredetailed/MeetingMoreDetailedPresenter.kt` — quickEmojis trimmed to 3
- `presentation/meetingmoredetailed/MeetingMoreDetailedOverlay.kt` — Raise Hand restyled as horizontal capsule

## Latest Update (2026-03-24 Meeting More Detailed Page Polish)

Refined the Meeting More Detailed Page (white bottom-sheet overlay) to match the latest UI reference:

1. Added a "Raise Hand" (🖐) circular button with label as the first item in the quick emoji row, before the standard emoji buttons.
2. Renamed "Show CC Notes" to "Show CC" in the grid.
3. Added a new "Notes" grid item between "Show CC" and "Apps", bringing the grid total to 8 items.

### Modified files
- `presentation/meetingmoredetailed/MeetingMoreDetailedPresenter.kt` — renamed grid item, inserted "Notes"
- `presentation/meetingmoredetailed/MeetingMoreDetailedOverlay.kt` — added Raise Hand button in QuickEmojiRow, updated icon mapping

## Latest Update (2026-03-24 Meeting Detailed Sub-Pages)

Added 5 sub-pages under the Meeting Detailed Page to complete the in-meeting experience:

1. **Meeting Chat Detailed Page** — full-screen chat with message bubbles, avatar/name for others, blue accent for self, emoji + text input bar with send. Navigated via Chat button in bottom bar.
2. **Meeting More Detailed Page** — dark bottom-sheet overlay with 2-page `HorizontalPager` grid (8 items on page 1, 6 on page 2), page indicator dots, and close button. Tapping Security opens Meeting Info.
3. **Meeting More and More Detailed Page** — second page of the More overlay with Live Streaming, Reactions, Raise Hand, Non-verbal Feedback, Poll, and Q&A (some grayed out).
4. **Meeting Speaker Detailed Page** — speaker view mode toggle in top bar. Shows large active speaker tile with green border + horizontal thumbnail strip of other participants. Toggling returns to gallery view.
5. **Meeting Info Detailed Page** — full-screen meeting information with topic, meeting ID, passcode, host, invite link (all copyable), end-to-end encryption badge, and Copy Invitation button.

### New files (11)
- `presentation/meetingchatdetailed/` — Contract, Presenter, Screen
- `presentation/meetingmoredetailed/` — Contract, Presenter, Overlay
- `presentation/meetingspeakerdetailed/` — Contract, Presenter
- `presentation/meetinginfodetailed/` — Contract, Presenter, Screen

### Modified files
- `data/DataRepository.kt` — added `getCurrentMeeting()` and `getParticipantsForMeeting()`
- `navigation/Routes.kt` — added `MeetingChatDetailed` and `MeetingInfoDetailed` routes
- `navigation/NavGraph.kt` — added 2 composable entries, wired callbacks
- `presentation/meetingdetailed/MeetingDetailedContract.kt` — expanded UiState with participants
- `presentation/meetingdetailed/MeetingDetailedPresenter.kt` — loads participant data
- `presentation/meetingdetailed/MeetingDetailedScreen.kt` — added Chat/Info callbacks, More overlay, Speaker view toggle

Refined the newly added in-meeting page to better match the latest requirement correction:

1. The `Meeting Detailed Page` top title bar now sits directly under the system status bar instead of floating lower in the screen.
2. The in-meeting microphone, camera, and speaker settings now stay aligned with the `Meeting Preview Page` when entering the meeting.
3. The `Meeting Detailed Page` speaker button now uses the same two-option audio menu as `Meeting Preview Page`.
4. The in-meeting microphone and camera controls now reuse the same active/inactive visual logic as the preview screen.

## Latest Update (2026-03-23 Meeting Detailed Pages)

Extended the Host Meeting flow with the newly added in-meeting pages from the updated page tree:

1. Tapping `Start` on `Meeting Preview Page` now opens a dedicated `Meeting Detailed Page`.
2. Added a separate `Leave the Meeting Detailed Page` that opens from the in-meeting `End` action.
3. The earlier `LeaveMeetingScreen` is now the preview-stage leave confirmation (`Leave the Meeting Preview Page`), while the new detailed leave sheet is reserved for the in-meeting flow.
4. Both new meeting pages stay static and local-state only, and continue to hide the app bottom navigation bar.

## Latest Update (2026-03-23 More Page Correction)

Corrected the page-tree mistake from the latest requirements update and completed the real `More Page`:

1. The top-right three-dot popup is now treated as `Expansion Page`, matching the corrected page tree.
2. The bottom-navigation `More Page` is now implemented as a popup sheet on top of the current first-level page instead of routing to a standalone screen.
3. Tapping the rightmost `More` item on `Home`, `Team Chat`, `Docs`, `Calendar`, or `Mail` keeps the current page as the background and shows the shared shortcut-sheet content from the latest reference.
4. The bottom bar now treats `More` as an overlay state instead of a route, while the top-right `Expansion Page` remains a separate menu pattern.

## Latest Update (2026-03-23 Meeting UI Polish)

Refined the newest meeting-related pages and menu positioning:

1. The `More` menu card now sits much closer to the top-right trigger button on supported pages.
2. The `Meeting Preview` speaker icon now opens a local audio-choice popup with `Wi‑Fi or cellular data` and `No audio`.
3. The extra dark rounded block behind `Leave the Meeting` was removed so the page reads more like the provided reference.

## Latest Update (2026-03-23 Meeting Preview & Expansion Page)

Added the latest page-tree expansion from the requirements document:

1. Starting a meeting from `Host Meeting` now opens a dedicated `Meeting Preview` page instead of stopping on the setup page.
2. The new preview page shows local microphone, camera, speaker, and preview-toggle UI based on the provided reference.
3. Leaving from the preview page now opens a separate `Leave the Meeting` confirmation page.
4. The top-right `More` button on `Home` and `Team Chat` now shows the floating `Expansion Page` menu with static actions matching the current reference direction.
5. These new child pages continue to behave as standalone pages without the bottom navigation bar.

## Latest Update (2026-03-23 Fullscreen Search & Meeting Subpages)

Adjusted page presentation so search and meeting-related child pages open as standalone screens without the bottom navigation bar:

1. The bottom navigation bar now appears only on the main sections: `Home`, `Team Chat`, `Docs`, `Calendar`, and `Mail`.
2. `Search`, `Start a meeting`, `Join meeting`, and `Schedule meeting` now hide the bottom navigation bar and behave like full child pages.
3. Returning from those child pages restores the bottom navigation bar with the correct selected main tab.
4. The main bottom navigation selection is now derived from the current navigation route instead of a separate local index state.

## Latest Update (2026-03-23 Search Page Rework)

Reworked the shared Search flow so it now matches the latest requirement more closely:

1. Search now stays inside one shared page reached from the top-right search action on the main pages.
2. The 9 in-search categories are `Top results`, `Messages`, `Chats and channels`, `Meetings`, `Contacts`, `Files`, `Docs`, `Whiteboards`, and `Mail`.
3. Each category now owns its own filter chip set and bottom-sheet options based on the provided `UIReference` screenshots instead of sharing one global `Date/Type` filter model.
4. Search no longer routes into the old message/chat/meeting detail pages from the main Search experience; the category content itself is now the target experience.
5. `Top results`, `Messages`, `Chats and channels`, `Meetings`, and `Contacts` still use the existing local asset data for search results.
6. `Files`, `Docs`, and `Whiteboards` currently show static empty/search guidance states because no matching asset data exists yet, while `Mail` shows the expected local `Connect your Mail` placeholder state.
7. Added searchable filter sheets, toggle chips for Mail (`Starred`, `Has attachment`), and kept the implementation in MVP-style classes with smaller split files.

## Latest Update (2026-03-22 Search Results & Detail Pages)

Implemented search functionality and detail pages for the Search screen:

1. Added search methods to `DataRepository` for messages, meetings, users, and chats filtering.
2. Extended `SearchUiState` with result data classes (`MessageResult`, `MeetingResult`, `ContactResult`, `ChatResult`).
3. Updated Search MVP with `search(query)` method that filters data and formats timestamps.
4. Search screen now conditionally renders: empty state when no query, tab-specific results when searching.
5. Created `SearchResultContent.kt` with composables for each tab's result list (Top Results, Messages, Chats, Meetings, Contacts, and empty results for Files/Docs/Whiteboards/Mail).
6. Added 3 detail pages with full MVP pattern: Message Detail (chat bubble view), Chat Detail (chat bubble view), Meeting Detail (info card + participant list).
7. Registered 3 new parameterized routes (`search_message_detail/{meetingId}`, `search_chat_detail/{meetingId}`, `search_meeting_detail/{meetingId}`) in NavGraph.

## Previous Update (2026-03-21 Search Page)

Implemented the latest search entry task from the requirements document:

1. Added a new `Search` page to the page tree and navigation graph.
2. Wired the top-right search action on `Home`, `Team Chat`, `Docs`, `Calendar`, and `Mail` to open the shared search page.
3. Built the search screen with a search box, `Cancel` action, 9 searchable category tabs, and empty-state guidance text.
4. Added `Date` and `Type` filter chips with bottom-sheet selection panels based on the provided references.
5. Replaced the touched search-entry top-bar actions with standard Material icons to avoid the earlier emoji text encoding issues.

## Latest Update (2026-03-21 Home Subpages)

Implemented the latest Home page tree expansion task from the requirements document:

1. Added `Host Meeting`, `Join Meeting`, and `Schedule Meeting` as separate pages under the Home flow.
2. Added `Share` as an in-place overlay on top of Home instead of a separate full-screen route.
3. Wired the four Home action buttons to the new child interfaces.
4. Kept all newly added meeting pages as static or local-state UI only, without real meeting creation, joining, or sharing logic.
5. Updated the meeting subpages so their top titles are centered, and removed the custom keyboard from `Join Meeting`.

## Latest Update (2026-03-21)

Implemented the latest profile expansion task from the requirements document:

1. Added `My profile` detail page and `Settings` page under the personal/profile flow.
2. Updated the main profile screen to match the latest reference direction with header, offer card, status rows, and page entry rows.
3. Added navigation from `Profile` to `Detailed Info` and `Settings`.
4. Kept `Detailed Info` and `Settings` as static display pages only; list items do not navigate to deeper pages in this round.
5. Tightened top app bar insets so all page headers sit closer to the system status bar.

## Entry Path

- Tap the avatar in the top bar to open `Profile`.
- Tap `My profile` to open the detailed info page.
- Tap `Settings` to open the settings page.

## Files Updated

- `app/src/main/java/com/example/zoom/navigation/NavGraph.kt`
- `app/src/main/java/com/example/zoom/navigation/Routes.kt`
- `app/src/main/java/com/example/zoom/presentation/profile/ProfileScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/detailedinfo/DetailedInfoContract.kt`
- `app/src/main/java/com/example/zoom/presentation/detailedinfo/DetailedInfoPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/detailedinfo/DetailedInfoScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/settings/SettingsContract.kt`
- `app/src/main/java/com/example/zoom/presentation/settings/SettingsPresenter.kt`
- `app/src/main/java/com/example/zoom/presentation/settings/SettingsScreen.kt`
- `app/src/main/java/com/example/zoom/ui/components/ProfileUiComponents.kt`
- `README.md`

## Latest Update (2026-03-20)

Implemented the latest UI refinement tasks from the development requirements file:

1. Updated page icon style to emoji for the current refinement scope.
2. Added `More` to the right side of the Team Chat top function area.
3. Updated Documents top function area items to `Recent`, `Started`, `My docs`, `Shared folders`, `Shared with me`, and `Trash`.
4. Added a proper calendar viewing function on Calendar page with mode switching (`Weekly` and `Agenda`).

## Files Updated

- `app/src/main/java/com/example/zoom/ui/components/ZoomTopBar.kt`
- `app/src/main/java/com/example/zoom/presentation/teamchat/TeamChatScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/documents/DocumentsScreen.kt`
- `app/src/main/java/com/example/zoom/presentation/calendar/CalendarScreen.kt`
- `README.md`

## Notes

- Build/compile was not executed in this round per project requirement (run and verify in Android Studio environment).

## Latest Update (Bottom Navigation)

Implemented current bottom navigation refinements from the development requirements:

1. Added `More` as the last item in the bottom navigation bar and now use it to open the shared `More Page` popup sheet.
2. Renamed the third bottom navigation item from `Documents` to `Docs`.
