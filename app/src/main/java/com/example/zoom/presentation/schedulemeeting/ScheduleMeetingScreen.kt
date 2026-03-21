package com.example.zoom.presentation.schedulemeeting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.components.ZoomActionPageTopBar
import com.example.zoom.ui.components.ZoomInsetDivider
import com.example.zoom.ui.components.ZoomPageSurface
import com.example.zoom.ui.components.ZoomSettingSwitchRow
import com.example.zoom.ui.components.ZoomSettingValueRow
import com.example.zoom.ui.components.ZoomSettingsSectionTitle
import com.example.zoom.ui.theme.ZoomTextSecondary

@Composable
fun ScheduleMeetingScreen(onBackClick: () -> Unit) {
    var uiState by remember { mutableStateOf<ScheduleMeetingUiState?>(null) }

    val view = remember {
        object : ScheduleMeetingContract.View {
            override fun showContent(content: ScheduleMeetingUiState) {
                uiState = content
            }
        }
    }

    LaunchedEffect(Unit) {
        ScheduleMeetingPresenter(view).loadData()
    }

    uiState?.let { screenState ->
        var meetingTitle by remember(screenState) { mutableStateOf(screenState.meetingTitle) }
        var usePersonalMeetingId by remember(screenState) { mutableStateOf(screenState.usePersonalMeetingId) }
        var requirePasscode by remember(screenState) { mutableStateOf(screenState.requirePasscode) }
        var waitingRoom by remember(screenState) { mutableStateOf(screenState.waitingRoom) }
        var continuousMeetingChat by remember(screenState) { mutableStateOf(screenState.continuousMeetingChat) }
        var hostVideoOn by remember(screenState) { mutableStateOf(screenState.hostVideoOn) }
        var participantVideoOn by remember(screenState) { mutableStateOf(screenState.participantVideoOn) }

        Scaffold(
            topBar = {
                ZoomActionPageTopBar(
                    title = "Schedule meeting",
                    onCancelClick = onBackClick,
                    actionText = "Save"
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(padding)
            ) {
                item {
                    ZoomPageSurface(modifier = Modifier.fillMaxWidth().background(Color.White)) {
                        Text(
                            text = meetingTitle,
                            fontSize = 24.sp,
                            color = Color(0xFFC7CCD4),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
                        )
                        ZoomInsetDivider()
                        ZoomSettingValueRow(title = "Starts", value = screenState.starts)
                        ZoomInsetDivider()
                        ZoomSettingValueRow(title = "Duration", value = screenState.duration)
                        ZoomInsetDivider()
                        ZoomSettingValueRow(title = "Time zone", value = screenState.timeZone)
                        ZoomInsetDivider()
                        ZoomSettingValueRow(title = "Repeat", value = screenState.repeat)
                        ZoomInsetDivider()
                        ZoomSettingValueRow(title = "Calendar", value = screenState.calendar)
                        ZoomInsetDivider()
                        ZoomSettingSwitchRow(
                            title = "Use personal meeting ID",
                            subtitle = screenState.personalMeetingId,
                            checked = usePersonalMeetingId,
                            onCheckedChange = { usePersonalMeetingId = it }
                        )
                    }
                }

                item {
                    Text(
                        text = "If this option is enabled, any meeting options that you change here will be applied to all meetings that use your personal meeting ID",
                        color = ZoomTextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                item {
                    ZoomSettingsSectionTitle(text = "SECURITY")
                    ZoomPageSurface(modifier = Modifier.fillMaxWidth().background(Color.White)) {
                        ZoomSettingSwitchRow(
                            title = "Require meeting passcode",
                            subtitle = "Only users who have the invite link or passcode can join the meeting",
                            checked = requirePasscode,
                            onCheckedChange = { requirePasscode = it }
                        )
                        ZoomInsetDivider()
                        ZoomSettingValueRow(
                            title = "Passcode",
                            value = screenState.passcode,
                            showChevron = false
                        )
                        ZoomInsetDivider()
                        ZoomSettingSwitchRow(
                            title = "Enable waiting room",
                            subtitle = "Only users admitted by the host can join the meeting",
                            checked = waitingRoom,
                            onCheckedChange = { waitingRoom = it }
                        )
                        ZoomInsetDivider()
                        ZoomSettingValueRow(title = "Encryption", value = screenState.encryption)
                        ZoomInsetDivider()
                        ZoomSettingValueRow(title = "Invitees", value = screenState.invitees)
                        ZoomInsetDivider()
                        ZoomSettingSwitchRow(
                            title = "Continuous meeting chat",
                            subtitle = "Chat will continue before and after the meeting",
                            checked = continuousMeetingChat,
                            onCheckedChange = { continuousMeetingChat = it }
                        )
                    }
                }

                item {
                    ZoomSettingsSectionTitle(text = "MEETING OPTIONS")
                    ZoomPageSurface(modifier = Modifier.fillMaxWidth().background(Color.White)) {
                        ZoomSettingSwitchRow(
                            title = "Host video on",
                            checked = hostVideoOn,
                            onCheckedChange = { hostVideoOn = it }
                        )
                        ZoomInsetDivider()
                        ZoomSettingSwitchRow(
                            title = "Participant video on",
                            checked = participantVideoOn,
                            onCheckedChange = { participantVideoOn = it }
                        )
                        ZoomInsetDivider()
                        ZoomSettingValueRow(
                            title = "Advanced options",
                            value = "",
                            showChevron = true
                        )
                    }
                }
            }
        }
    }
}
