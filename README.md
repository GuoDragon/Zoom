# Zoom Android Template

This project is a static Zoom-like Android app built with Jetpack Compose and MVP-style presentation layers.

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

