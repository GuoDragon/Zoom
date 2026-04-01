package com.example.zoom.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.theme.ZoomBlue

enum class MeetingAudioOption(val routeValue: String, val label: String) {
    WifiOrCellular("wifi", "Wi-Fi or cellular data"),
    NoAudio("none", "No audio");

    companion object {
        fun fromRouteValue(value: String?): MeetingAudioOption {
            return entries.firstOrNull { it.routeValue == value } ?: WifiOrCellular
        }
    }
}

data class MeetingSessionConfig(
    val microphoneOn: Boolean,
    val cameraOn: Boolean,
    val audioOption: MeetingAudioOption,
    val screenSharingEnabled: Boolean = false
)

@Composable
fun MeetingMediaToggleButton(
    enabled: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    iconSize: Dp = 24.dp,
    containerColor: Color = Color(0xFF1F1F22)
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor)
    ) {
        Icon(
            imageVector = if (enabled) activeIcon else inactiveIcon,
            contentDescription = null,
            tint = if (enabled) ZoomBlue else Color(0xFFE65B5B),
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun MeetingAudioMenu(
    selectedOption: MeetingAudioOption,
    onOptionSelected: (MeetingAudioOption) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        MeetingAudioOptionRow(
            label = MeetingAudioOption.WifiOrCellular.label,
            selected = selectedOption == MeetingAudioOption.WifiOrCellular,
            onClick = { onOptionSelected(MeetingAudioOption.WifiOrCellular) }
        )
        HorizontalDivider(color = Color(0xFFE6E8EC))
        MeetingAudioOptionRow(
            label = MeetingAudioOption.NoAudio.label,
            selected = selectedOption == MeetingAudioOption.NoAudio,
            onClick = { onOptionSelected(MeetingAudioOption.NoAudio) }
        )
    }
}

@Composable
private fun MeetingAudioOptionRow(
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
