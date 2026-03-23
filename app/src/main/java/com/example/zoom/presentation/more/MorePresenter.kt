package com.example.zoom.presentation.more

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PermContactCalendar
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Draw

class MorePresenter(
    private val view: MoreContract.View
) : MoreContract.Presenter {
    override fun loadData() {
        view.showContent(
            MoreUiState(
                shortcutActions = listOf(
                    MoreShortcutUiState("Whiteboards", Icons.Default.Draw),
                    MoreShortcutUiState("Events", Icons.AutoMirrored.Filled.EventNote),
                    MoreShortcutUiState("Apps", Icons.Default.GridView),
                    MoreShortcutUiState("Contacts", Icons.Default.PermContactCalendar),
                    MoreShortcutUiState("Clips", Icons.Default.PlayCircleOutline),
                    MoreShortcutUiState("Notes", Icons.Default.Description),
                    MoreShortcutUiState("Tasks", Icons.Default.TaskAlt),
                    MoreShortcutUiState("Scheduler", Icons.Default.Groups)
                ),
                promoBadge = "FREE TRIAL OFFER",
                promoText = "Unlock longer meetings and AI Companion with a Workplace Pro trial for up to 14 days.",
                promoLinkText = "Get started",
                reorderText = "Reorder"
            )
        )
    }
}
