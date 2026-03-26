package com.example.zoom.presentation.meetingmorepages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
internal fun rememberMeetingMorePagesUiState(): MeetingMorePagesUiState? {
    var uiState by remember { mutableStateOf<MeetingMorePagesUiState?>(null) }

    val view = remember {
        object : MeetingMorePagesContract.View {
            override fun showContent(content: MeetingMorePagesUiState) {
                uiState = content
            }
        }
    }

    LaunchedEffect(Unit) {
        MeetingMorePagesPresenter(view).loadData()
    }

    return uiState
}

@Composable
internal fun MeetingDarkSheetOverlay(
    title: String,
    onDismiss: () -> Unit,
    sheetHeightFraction: Float,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(sheetHeightFraction)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color(0xFF2A2A2D))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {}
                .padding(top = 8.dp, bottom = 18.dp)
        ) {
            MeetingSheetHandle()
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
internal fun MeetingSheetHandle() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(38.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFFD0D0D0))
        )
    }
}

@Composable
internal fun MeetingListRow(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    showChevron: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color(0xFF353E49) else Color(0xFF323338))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) Color(0xFF80B4FF) else Color(0xFFB3B4BA)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (showChevron) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF9A9A9E)
                )
            }
        }
    }
}

internal fun shareOptionIcon(iconName: String): ImageVector = when (iconName) {
    "Docs" -> Icons.Default.Description
    "Whiteboard" -> Icons.Default.Dashboard
    "Photos" -> Icons.Default.PhotoLibrary
    "Camera" -> Icons.Default.CameraAlt
    "Link" -> Icons.Default.Link
    "Bookmark" -> Icons.Default.BookmarkBorder
    else -> Icons.Default.Description
}

internal fun hostToolIcon(iconName: String): ImageVector = when (iconName) {
    "Security" -> Icons.Default.Lock
    "General" -> Icons.Default.Settings
    "Captions" -> Icons.Default.ClosedCaption
    "Participants" -> Icons.Default.Groups
    else -> Icons.Default.Settings
}

internal fun meetingSettingIcon(iconName: String): ImageVector = when (iconName) {
    "Meeting" -> Icons.Default.Videocam
    "Background" -> Icons.Default.Image
    "Filters" -> Icons.Default.Tune
    "Avatars" -> Icons.Default.Face
    "Audio" -> Icons.Default.Headset
    "Captions" -> Icons.Default.ClosedCaption
    "Rooms" -> Icons.Default.Tv
    else -> Icons.Default.Settings
}

internal fun zoomAppIcon(iconName: String): ImageVector = when (iconName) {
    "Timer" -> Icons.Default.Timer
    "Background" -> Icons.Default.Image
    "Music" -> Icons.Default.MusicNote
    "GroupPhoto" -> Icons.Default.CameraAlt
    else -> Icons.Default.Dashboard
}
