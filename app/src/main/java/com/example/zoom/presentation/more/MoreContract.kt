package com.example.zoom.presentation.more

import androidx.compose.ui.graphics.vector.ImageVector

interface MoreContract {
    interface View {
        fun showContent(content: MoreUiState)
    }

    interface Presenter {
        fun loadData()
    }
}

enum class MoreShortcutAction {
    WHITEBOARDS,
    EVENTS,
    APPS,
    CONTACTS,
    CLIPS,
    NOTES,
    TASKS,
    SCHEDULER
}

data class MoreUiState(
    val shortcutActions: List<MoreShortcutUiState>,
    val promoBadge: String,
    val promoText: String,
    val promoLinkText: String,
    val reorderText: String
)

data class MoreShortcutUiState(
    val action: MoreShortcutAction,
    val label: String,
    val icon: ImageVector
)
