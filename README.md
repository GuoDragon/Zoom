# Zoom Android Template

This project is a static Zoom-like Android app built with Jetpack Compose and MVP-style presentation layers.

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

1. Added `More` as the last item in the bottom navigation bar (no navigation target page).
2. Renamed the third bottom navigation item from `Documents` to `Docs`.
