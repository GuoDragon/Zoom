package com.example.zoom.presentation.meetingdetailed

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.VideocamOff
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MeetingDetailedScreen(
    onBackClick: () -> Unit,
    onEndClick: () -> Unit
) {
    var uiState by remember { mutableStateOf<MeetingDetailedUiState?>(null) }

    val view = remember {
        object : MeetingDetailedContract.View {
            override fun showContent(content: MeetingDetailedUiState) {
                uiState = content
            }
        }
    }

    LaunchedEffect(Unit) {
        MeetingDetailedPresenter(view).loadData()
    }

    uiState?.let { screenState ->
        Scaffold(
            containerColor = Color.Black,
            bottomBar = {
                MeetingControlBar(
                    controls = screenState.controls,
                    onEndClick = onEndClick
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(padding)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    MeetingDetailedTopBar(
                        title = screenState.title,
                        onBackClick = onBackClick
                    )
                    TopStatusRow()
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(124.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF78A93A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = screenState.participantInitials,
                            color = Color.White,
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = screenState.participantLabel,
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 12.dp, bottom = 84.dp)
                )
            }
        }
    }
}

@Composable
private fun MeetingDetailedTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF161616))
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.OpenInFull,
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Text(
            text = title,
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Speaker",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun TopStatusRow() {
    Row(
        modifier = Modifier
            .padding(start = 14.dp, top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusBadge(
            icon = Icons.Default.MicOff,
            containerColor = Color(0xFF1E1E1E),
            tint = Color(0xFFE75563)
        )
        StatusBadge(
            icon = Icons.Default.CheckCircle,
            containerColor = Color(0xFF0F2213),
            tint = Color(0xFF5BC26B)
        )
    }
}

@Composable
private fun StatusBadge(
    icon: ImageVector,
    containerColor: Color,
    tint: Color
) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun MeetingControlBar(
    controls: List<MeetingControlUiState>,
    onEndClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF161616))
            .padding(horizontal = 6.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        controls.forEach { control ->
            MeetingControlItem(
                control = control,
                onClick = if (control.action == MeetingControlAction.End) onEndClick else ({})
            )
        }
    }
}

@Composable
private fun MeetingControlItem(
    control: MeetingControlUiState,
    onClick: () -> Unit
) {
    val (icon, tint, backgroundColor, borderColor) = when (control.action) {
        MeetingControlAction.Audio -> ControlVisual(
            icon = Icons.Default.MicOff,
            tint = Color(0xFFE75563)
        )
        MeetingControlAction.Video -> ControlVisual(
            icon = Icons.Default.VideocamOff,
            tint = Color(0xFFE75563)
        )
        MeetingControlAction.Chat -> ControlVisual(
            icon = Icons.AutoMirrored.Filled.Chat,
            tint = Color.White
        )
        MeetingControlAction.More -> ControlVisual(
            icon = Icons.Default.MoreHoriz,
            tint = Color.White
        )
        MeetingControlAction.End -> ControlVisual(
            icon = Icons.Default.MicOff,
            tint = Color(0xFFE73561),
            backgroundColor = Color.Transparent,
            borderColor = Color(0xFFE73561)
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(backgroundColor)
        ) {
            if (control.action == MeetingControlAction.End) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = control.label,
                        tint = borderColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = control.label,
                    tint = tint,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = control.label,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

private data class ControlVisual(
    val icon: ImageVector,
    val tint: Color,
    val backgroundColor: Color = Color.Transparent,
    val borderColor: Color = Color.Transparent
)
