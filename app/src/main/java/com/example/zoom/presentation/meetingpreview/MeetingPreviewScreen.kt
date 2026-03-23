package com.example.zoom.presentation.meetingpreview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import com.example.zoom.ui.components.ZoomPrimaryActionButton
import com.example.zoom.ui.theme.ZoomBlue

@Composable
fun MeetingPreviewScreen(
    onLeaveClick: () -> Unit,
    onStartClick: () -> Unit
) {
    var uiState by remember { mutableStateOf<MeetingPreviewUiState?>(null) }

    val view = remember {
        object : MeetingPreviewContract.View {
            override fun showContent(content: MeetingPreviewUiState) {
                uiState = content
            }
        }
    }

    LaunchedEffect(Unit) {
        MeetingPreviewPresenter(view).loadData()
    }

    uiState?.let { screenState ->
        var microphoneOn by remember(screenState) { mutableStateOf(screenState.microphoneOn) }
        var cameraOn by remember(screenState) { mutableStateOf(screenState.cameraOn) }
        var alwaysShowPreview by remember(screenState) { mutableStateOf(screenState.alwaysShowPreview) }
        var showAudioMenu by remember { mutableStateOf(false) }
        var selectedAudioOption by remember { mutableStateOf("wifi") }

        Scaffold(containerColor = Color.White) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(padding)
                    .padding(horizontal = 18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onLeaveClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Leave",
                            tint = Color(0xFF242C35)
                        )
                    }
                    Spacer(modifier = Modifier.size(48.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.Black)
                        .clickable(enabled = showAudioMenu) { showAudioMenu = false },
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { showAudioMenu = !showAudioMenu },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E2E31))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speaker",
                            tint = Color.White
                        )
                    }

                    if (showAudioMenu) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 52.dp)
                                .width(208.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                        ) {
                            AudioOptionRow(
                                label = "Wi-Fi or cellular data",
                                selected = selectedAudioOption == "wifi",
                                onClick = {
                                    selectedAudioOption = "wifi"
                                    showAudioMenu = false
                                }
                            )
                            HorizontalDivider(color = Color(0xFFE6E8EC))
                            AudioOptionRow(
                                label = "No audio",
                                selected = selectedAudioOption == "none",
                                onClick = {
                                    selectedAudioOption = "none"
                                    showAudioMenu = false
                                }
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(136.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFF72B646)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = screenState.participantInitials,
                            color = Color.White,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        PreviewToggleButton(
                            enabled = microphoneOn,
                            activeIcon = Icons.Default.Mic,
                            inactiveIcon = Icons.Default.MicOff,
                            onClick = { microphoneOn = !microphoneOn }
                        )
                        PreviewToggleButton(
                            enabled = cameraOn,
                            activeIcon = Icons.Default.Videocam,
                            inactiveIcon = Icons.Default.VideocamOff,
                            onClick = { cameraOn = !cameraOn }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = screenState.meetingTitle,
                    color = Color(0xFF212933),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(14.dp))
                ZoomPrimaryActionButton(
                    text = "Start",
                    onClick = onStartClick,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = alwaysShowPreview,
                        onCheckedChange = { alwaysShowPreview = it }
                    )
                    Text(
                        text = "Always show this preview when joining",
                        color = Color(0xFF556274),
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun PreviewToggleButton(
    enabled: Boolean,
    activeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    inactiveIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(Color(0xFF1F1F22))
    ) {
        Icon(
            imageVector = if (enabled) activeIcon else inactiveIcon,
            contentDescription = null,
            tint = if (enabled) ZoomBlue else Color(0xFFE65B5B)
        )
    }
}

@Composable
private fun AudioOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = if (selected) Color(0xFF1F2A36) else Color.Transparent,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            color = Color(0xFF1F2A36),
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
