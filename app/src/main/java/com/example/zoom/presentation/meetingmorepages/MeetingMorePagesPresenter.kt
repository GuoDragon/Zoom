package com.example.zoom.presentation.meetingmorepages

class MeetingMorePagesPresenter(
    private val view: MeetingMorePagesContract.View
) : MeetingMorePagesContract.Presenter {

    override fun loadData() {
        view.showContent(
            MeetingMorePagesUiState(
                shareOptions = listOf(
                    ShareOptionUi("Zoom Docs", "Docs"),
                    ShareOptionUi("Zoom Whiteboards", "Whiteboard"),
                    ShareOptionUi("Photos", "Photos"),
                    ShareOptionUi("Share Camera", "Camera"),
                    ShareOptionUi("Website URL", "Link"),
                    ShareOptionUi("Bookmark", "Bookmark")
                ),
                hostToolOptions = listOf(
                    HostToolOptionUi("Security", "Security"),
                    HostToolOptionUi("General", "General"),
                    HostToolOptionUi("Captions", "Captions"),
                    HostToolOptionUi("Participants", "Participants")
                ),
                meetingSettingOptions = listOf(
                    MeetingSettingOptionUi("Meeting settings", "Meeting"),
                    MeetingSettingOptionUi("Virtual backgrounds", "Background"),
                    MeetingSettingOptionUi("Video filters", "Filters"),
                    MeetingSettingOptionUi("Avatars", "Avatars"),
                    MeetingSettingOptionUi("Audio", "Audio"),
                    MeetingSettingOptionUi("Captions and translations", "Captions"),
                    MeetingSettingOptionUi("Zoom Rooms", "Rooms")
                ),
                notesSortOptions = listOf(
                    "Modified (new-old)",
                    "Modified (old-new)",
                    "Name (A-Z)"
                ),
                notesOwnerOptions = listOf(
                    "Owned by anyone",
                    "Owned by me",
                    "Shared with me"
                ),
                zoomApps = listOf(
                    ZoomAppUi(
                        id = "timer",
                        name = "Timer",
                        description = "Keep control of your workday with the Timer app.",
                        iconName = "Timer"
                    ),
                    ZoomAppUi(
                        id = "backgrounds",
                        name = "Virtual Backgrounds",
                        description = "Discover the perfect virtual background for your meetings.",
                        iconName = "Background"
                    ),
                    ZoomAppUi(
                        id = "music",
                        name = "Music",
                        description = "Play music in your meetings.",
                        iconName = "Music",
                        isDimmed = true
                    ),
                    ZoomAppUi(
                        id = "group_photo",
                        name = "Group Photo",
                        description = "Take instant high-quality group photos.",
                        iconName = "GroupPhoto",
                        isDimmed = true
                    )
                )
            )
        )
    }
}
