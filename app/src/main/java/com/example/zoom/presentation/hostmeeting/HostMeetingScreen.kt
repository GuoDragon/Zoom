package com.example.zoom.presentation.hostmeeting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import com.example.zoom.ui.components.MeetingAudioOption
import com.example.zoom.ui.components.MeetingSessionConfig
import com.example.zoom.ui.components.ZoomActionPageTopBar
import com.example.zoom.ui.components.ZoomInsetDivider
import com.example.zoom.ui.components.ZoomPageSurface
import com.example.zoom.ui.components.ZoomPrimaryActionButton
import com.example.zoom.ui.components.ZoomSettingSwitchRow

@Composable
fun HostMeetingScreen(
    onBackClick: () -> Unit,
    onStartMeetingClick: (String, MeetingSessionConfig) -> Unit
) {
    var uiState by remember { mutableStateOf<HostMeetingUiState?>(null) }

    val view = remember {
        object : HostMeetingContract.View {
            override fun showContent(content: HostMeetingUiState) {
                uiState = content
            }
        }
    }
    val presenter = remember(view) { HostMeetingPresenter(view) }

    LaunchedEffect(presenter) {
        presenter.loadData()
    }

    uiState?.let { screenState ->
        var meetingTitle by remember(screenState) { mutableStateOf(screenState.meetingTitle) }
        var videoOn by remember(screenState) { mutableStateOf(screenState.videoOn) }
        var audioConnectedByDefault by remember(screenState) { mutableStateOf(screenState.audioConnectedByDefault) }
        var usePersonalMeetingId by remember(screenState) { mutableStateOf(screenState.usePersonalMeetingId) }
        var waitingRoomEnabled by remember(screenState) { mutableStateOf(screenState.waitingRoomEnabled) }
        var allowJoinBeforeHost by remember(screenState) { mutableStateOf(screenState.allowJoinBeforeHost) }

        Scaffold(
            topBar = {
                ZoomActionPageTopBar(
                    title = "Start a meeting",
                    onCancelClick = onBackClick
                )
            }
        ) { padding ->
            ZoomPageSurface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(padding)
            ) {
                TextField(
                    value = meetingTitle,
                    onValueChange = { meetingTitle = it },
                    placeholder = { Text("Meeting topic") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF2F4F7),
                        unfocusedContainerColor = Color(0xFFF2F4F7),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                ZoomInsetDivider()
                ZoomSettingSwitchRow(
                    title = "Video on",
                    checked = videoOn,
                    onCheckedChange = { videoOn = it }
                )
                ZoomInsetDivider()
                ZoomSettingSwitchRow(
                    title = "Auto-connect audio",
                    checked = audioConnectedByDefault,
                    onCheckedChange = { audioConnectedByDefault = it }
                )
                ZoomInsetDivider()
                ZoomSettingSwitchRow(
                    title = "Use personal meeting ID (PMI)",
                    subtitle = screenState.personalMeetingId,
                    checked = usePersonalMeetingId,
                    onCheckedChange = { usePersonalMeetingId = it }
                )
                ZoomInsetDivider()
                ZoomSettingSwitchRow(
                    title = "Enable waiting room",
                    checked = waitingRoomEnabled,
                    onCheckedChange = { waitingRoomEnabled = it }
                )
                ZoomInsetDivider()
                ZoomSettingSwitchRow(
                    title = "Allow participants to join before host",
                    checked = allowJoinBeforeHost,
                    onCheckedChange = { allowJoinBeforeHost = it }
                )
                Spacer(modifier = Modifier.height(28.dp))
                ZoomPrimaryActionButton(
                    text = "Start a meeting",
                    onClick = {
                        val meetingId = presenter.prepareMeetingSession(
                            usePersonalMeetingId = usePersonalMeetingId,
                            videoOn = videoOn,
                            topic = meetingTitle,
                            waitingRoomEnabled = waitingRoomEnabled,
                            allowJoinBeforeHost = allowJoinBeforeHost
                        )
                        onStartMeetingClick(
                            meetingId,
                            MeetingSessionConfig(
                                microphoneOn = false,
                                cameraOn = videoOn,
                                audioOption = if (audioConnectedByDefault) {
                                    MeetingAudioOption.WifiOrCellular
                                } else {
                                    MeetingAudioOption.NoAudio
                                }
                            )
                        )
                    },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}
