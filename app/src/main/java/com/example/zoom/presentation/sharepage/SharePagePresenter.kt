package com.example.zoom.presentation.sharepage

import com.example.zoom.data.DataRepository
import com.example.zoom.ui.components.MeetingAudioOption
import com.example.zoom.ui.components.MeetingSessionConfig

class SharePagePresenter(
    private val view: SharePageContract.View
) : SharePageContract.Presenter {

    override fun loadData() {
        view.showContent(
            SharePageUiState(
                title = "Share screen",
                hint = "Enter room code or meeting ID to share to a Zoom Room",
                placeholder = "Room code or meeting ID",
                cancelLabel = "Cancel",
                confirmLabel = "OK"
            )
        )
    }

    override fun onCancel() {
        view.dismiss()
    }

    override fun onConfirmShare(shareCode: String) {
        val normalizedShareCode = shareCode.filter { it.isDigit() }.take(8)
        if (normalizedShareCode.length != 8) return

        DataRepository.startShareScreenSession(normalizedShareCode)
        view.navigateToMeeting(
            MeetingSessionConfig(
                microphoneOn = false,
                cameraOn = false,
                audioOption = MeetingAudioOption.WifiOrCellular,
                screenSharingEnabled = true
            )
        )
    }
}
