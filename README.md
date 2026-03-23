# Zoom Android Template

This project is a static Zoom-like Android app built with Jetpack Compose and MVP-style presentation layers.

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
