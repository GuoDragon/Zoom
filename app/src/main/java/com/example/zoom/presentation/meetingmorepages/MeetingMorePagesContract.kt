package com.example.zoom.presentation.meetingmorepages

enum class MeetingMorePage {
    SHARE,
    NOTES,
    APPS,
    HOST_TOOLS,
    SETTINGS
}

data class ShareOptionUi(
    val label: String,
    val iconName: String
)

data class HostToolOptionUi(
    val label: String,
    val iconName: String
)

data class MeetingSettingOptionUi(
    val label: String,
    val iconName: String
)

data class ZoomAppUi(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val isDimmed: Boolean = false
)

data class MeetingMorePagesUiState(
    val shareOptions: List<ShareOptionUi>,
    val hostToolOptions: List<HostToolOptionUi>,
    val meetingSettingOptions: List<MeetingSettingOptionUi>,
    val notesSortOptions: List<String>,
    val notesOwnerOptions: List<String>,
    val zoomApps: List<ZoomAppUi>
)

interface MeetingMorePagesContract {
    interface View {
        fun showContent(content: MeetingMorePagesUiState)
    }

    interface Presenter {
        fun loadData()
    }
}
