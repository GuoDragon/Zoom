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
                    MoreShortcutUiState(MoreShortcutAction.WHITEBOARDS, "Whiteboards", Icons.Default.Draw),
                    MoreShortcutUiState(MoreShortcutAction.EVENTS, "Events", Icons.AutoMirrored.Filled.EventNote),
                    MoreShortcutUiState(MoreShortcutAction.APPS, "Apps", Icons.Default.GridView),
                    MoreShortcutUiState(MoreShortcutAction.CONTACTS, "Contacts", Icons.Default.PermContactCalendar),
                    MoreShortcutUiState(MoreShortcutAction.CLIPS, "Clips", Icons.Default.PlayCircleOutline),
                    MoreShortcutUiState(MoreShortcutAction.NOTES, "Notes", Icons.Default.Description),
                    MoreShortcutUiState(MoreShortcutAction.TASKS, "Tasks", Icons.Default.TaskAlt),
                    MoreShortcutUiState(MoreShortcutAction.SCHEDULER, "Scheduler", Icons.Default.Groups)
                ),
                promoBadge = "FREE TRIAL OFFER",
                promoText = "Unlock longer meetings and AI Companion with a Workplace Pro trial for up to 14 days.",
                promoLinkText = "Get started",
                reorderText = "Reorder"
            )
        )
    }
}
