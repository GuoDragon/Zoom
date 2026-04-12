package com.example.zoom.presentation.settings

interface SettingsContract {
    interface View {
        fun showSettings(content: SettingsUiState)
    }

    interface Presenter {
        fun loadData()
        fun updateAutoConnectAudio(enabled: Boolean)
        fun updateAutoTurnOnCamera(enabled: Boolean)
    }
}

data class SettingsUiState(
    val groups: List<SettingsGroup>,
    val accountActions: List<SettingsItem>,
    val autoConnectAudioOn: Boolean,
    val autoTurnOnCameraOn: Boolean
)

data class SettingsGroup(
    val items: List<SettingsItem>
)

data class SettingsItem(
    val id: String,
    val title: String
)
