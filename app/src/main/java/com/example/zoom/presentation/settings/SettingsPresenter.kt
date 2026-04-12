package com.example.zoom.presentation.settings

import com.example.zoom.data.DataRepository

class SettingsPresenter(
    private val view: SettingsContract.View
) : SettingsContract.Presenter {
    override fun loadData() {
        val preferences = DataRepository.getMeetingPreferencesSignal()
        view.showSettings(
            SettingsUiState(
                groups = listOf(
                    SettingsGroup(
                        items = listOf(
                            SettingsItem("general", "General")
                        )
                    ),
                    SettingsGroup(
                        items = listOf(
                            SettingsItem("notifications", "Notifications & sounds"),
                            SettingsItem("video", "Video & effects"),
                            SettingsItem("audio", "Audio"),
                            SettingsItem("accessibility", "Accessibility")
                        )
                    ),
                    SettingsGroup(
                        items = listOf(
                            SettingsItem("meetings", "Meetings"),
                            SettingsItem("team_chat", "Team Chat"),
                            SettingsItem("calendar", "Calendar")
                        )
                    ),
                    SettingsGroup(
                        items = listOf(
                            SettingsItem("siri", "Siri shortcuts"),
                            SettingsItem("qr", "Scan QR code"),
                            SettingsItem("about", "About")
                        )
                    )
                ),
                accountActions = listOf(
                    SettingsItem("switch_account", "Switch account"),
                    SettingsItem("sign_out", "Sign out")
                ),
                autoConnectAudioOn = preferences.autoConnectAudioOn,
                autoTurnOnCameraOn = preferences.autoTurnOnCameraOn
            )
        )
    }

    override fun updateAutoConnectAudio(enabled: Boolean) {
        DataRepository.updateMeetingPreferences(autoConnectAudioOn = enabled)
        loadData()
    }

    override fun updateAutoTurnOnCamera(enabled: Boolean) {
        DataRepository.updateMeetingPreferences(autoTurnOnCameraOn = enabled)
        loadData()
    }
}
