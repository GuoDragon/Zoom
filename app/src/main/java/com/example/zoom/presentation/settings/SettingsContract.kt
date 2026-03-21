package com.example.zoom.presentation.settings

interface SettingsContract {
    interface View {
        fun showSettings(content: SettingsUiState)
    }

    interface Presenter {
        fun loadData()
    }
}

data class SettingsUiState(
    val groups: List<SettingsGroup>,
    val accountActions: List<SettingsItem>
)

data class SettingsGroup(
    val items: List<SettingsItem>
)

data class SettingsItem(
    val id: String,
    val title: String
)
