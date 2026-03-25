package com.example.zoom.presentation.meetingmoredetailed

class MeetingMoreDetailedPresenter(
    private val view: MeetingMoreDetailedContract.View
) : MeetingMoreDetailedContract.Presenter {
    override fun loadData() {
        view.showContent(
            MeetingMoreDetailedUiState(
                quickEmojis = listOf("👍", "👏", "❤️"),
                gridItems = listOf(
                    MoreGridItem("Participants"),
                    MoreGridItem("Share"),
                    MoreGridItem("Show CC"),
                    MoreGridItem("Notes"),
                    MoreGridItem("Apps"),
                    MoreGridItem("Meeting info"),
                    MoreGridItem("Host tools"),
                    MoreGridItem("Settings")
                ),
                reactionEmojis = listOf("👏", "👍", "❤️", "😂", "😘", "🎉"),
                effectEmojis = listOf("🎈", "🚀", "👍", "😂", "🎉", "❤️"),
                feedbackIcons = listOf(
                    FeedbackItem("Agree", "✅"),
                    FeedbackItem("Disagree", "❌"),
                    FeedbackItem("Slow down", "⏪"),
                    FeedbackItem("Speed up", "⏩"),
                    FeedbackItem("Break", "☕")
                )
            )
        )
    }
}
