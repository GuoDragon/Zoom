package com.example.zoom.presentation.meetingmorepages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ScreenShare
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun MeetingShareOverlay(
    screenSharingEnabled: Boolean,
    sharePaused: Boolean,
    onDismiss: () -> Unit,
    onShareScreenChanged: (Boolean) -> Unit,
    onPauseShare: () -> Unit = {},
    onResumeShare: () -> Unit = {}
) {
    val state = rememberMeetingMorePagesUiState()
    var selectedLabel by remember { mutableStateOf<String?>(null) }

    MeetingDarkSheetOverlay(
        title = "Share",
        onDismiss = onDismiss,
        sheetHeightFraction = 0.62f
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (screenSharingEnabled) Color(0xFF21463D) else Color(0xFF34353A))
                .clickable {
                    val updatedSharingEnabled = !screenSharingEnabled
                    selectedLabel = null
                    onShareScreenChanged(updatedSharingEnabled)
                }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ScreenShare,
                contentDescription = null,
                tint = Color(0xFF20D4B2),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Share screen",
                color = Color(0xFF20D4B2),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val pauseEnabled = screenSharingEnabled && !sharePaused
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF34353A))
                    .clickable {
                        if (pauseEnabled) {
                            onPauseShare()
                        }
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Pause share",
                    color = if (pauseEnabled) Color.White else Color(0xFF7D8089),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            val resumeEnabled = screenSharingEnabled && sharePaused
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF34353A))
                    .clickable {
                        if (resumeEnabled) {
                            onResumeShare()
                        }
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Resume share",
                    color = if (resumeEnabled) Color.White else Color(0xFF7D8089),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        state?.shareOptions?.forEach { option ->
            MeetingListRow(
                label = option.label,
                icon = shareOptionIcon(option.iconName),
                selected = selectedLabel == option.label,
                showChevron = false,
                onClick = {
                    selectedLabel = if (selectedLabel == option.label) null else option.label
                    if (screenSharingEnabled) {
                        onShareScreenChanged(false)
                    }
                }
            )
        }
    }
}

@Composable
fun MeetingHostToolsOverlay(
    onDismiss: () -> Unit,
    onToolSelected: (String) -> Unit = {}
) {
    val state = rememberMeetingMorePagesUiState()
    var selectedLabel by remember { mutableStateOf<String?>(null) }
    var suspended by remember { mutableStateOf(false) }

    MeetingDarkSheetOverlay(
        title = "Host tools",
        onDismiss = onDismiss,
        sheetHeightFraction = 0.56f
    ) {
        state?.hostToolOptions?.forEach { option ->
            MeetingListRow(
                label = option.label,
                icon = hostToolIcon(option.iconName),
                selected = selectedLabel == option.label,
                onClick = {
                    selectedLabel = if (selectedLabel == option.label) null else option.label
                    onToolSelected(option.label)
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (suspended) Color(0xFF5A2B31) else Color(0xFF3A3A3D))
                .clickable { suspended = !suspended }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Suspend participant activities",
                color = Color(0xFFFF6B76),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MeetingSettingsOverlay(onDismiss: () -> Unit) {
    val state = rememberMeetingMorePagesUiState()
    var selectedLabel by remember { mutableStateOf<String?>(null) }

    MeetingDarkSheetOverlay(
        title = "Settings",
        onDismiss = onDismiss,
        sheetHeightFraction = 0.66f
    ) {
        state?.meetingSettingOptions?.forEach { option ->
            MeetingListRow(
                label = option.label,
                icon = meetingSettingIcon(option.iconName),
                selected = selectedLabel == option.label,
                onClick = {
                    selectedLabel = if (selectedLabel == option.label) null else option.label
                }
            )
        }
    }
}

@Composable
fun MeetingShowCcToast(
    visible: Boolean,
    onAutoDismiss: () -> Unit
) {
    if (!visible) return

    LaunchedEffect(visible) {
        delay(2400)
        onAutoDismiss()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 108.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFE7E7E7))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF454545))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ClosedCaption,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Closed captioning is turned on",
                color = Color(0xFF303030),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
