package com.example.zoom.presentation.meetingmoredetailed

interface MeetingMoreDetailedContract {
    interface View {
        fun showContent(content: MeetingMoreDetailedUiState)
    }

    interface Presenter {
        fun loadData()
    }
}

data class MoreGridItem(
    val label: String,
    val enabled: Boolean = true
)

data class FeedbackItem(
    val label: String,
    val emoji: String
)

data class MeetingMoreDetailedUiState(
    val quickEmojis: List<String>,
    val gridItems: List<MoreGridItem>,
    val reactionEmojis: List<String>,
    val effectEmojis: List<String>,
    val feedbackIcons: List<FeedbackItem>
)
