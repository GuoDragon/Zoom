package com.example.zoom.presentation.meetingmoredetailed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.automirrored.filled.ScreenShare
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MeetingMoreDetailedOverlay(
    onRaiseHand: () -> Unit,
    onDismiss: () -> Unit
) {
    var uiState by remember { mutableStateOf<MeetingMoreDetailedUiState?>(null) }
    var showReactionsPage by remember { mutableStateOf(false) }

    val view = remember {
        object : MeetingMoreDetailedContract.View {
            override fun showContent(content: MeetingMoreDetailedUiState) {
                uiState = content
            }
        }
    }

    LaunchedEffect(Unit) {
        MeetingMoreDetailedPresenter(view).loadData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() }
    ) {
        uiState?.let { state ->
            if (showReactionsPage) {
                ReactionsPage(
                    state = state,
                    onRaiseHand = onRaiseHand,
                    onClose = { showReactionsPage = false }
                )
            } else {
                PrimaryMorePage(
                    state = state,
                    onRaiseHand = onRaiseHand,
                    onMoreEmojisClick = { showReactionsPage = true },
                    onDismiss = onDismiss
                )
            }
        }
    }
}

// ── Primary More Page (White) ──────────────────────────────────────────

@Composable
private fun PrimaryMorePage(
    state: MeetingMoreDetailedUiState,
    onRaiseHand: () -> Unit,
    onMoreEmojisClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color.White)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {}
                .padding(top = 8.dp, bottom = 24.dp)
        ) {
            // Drag handle
            DragHandle()

            Spacer(modifier = Modifier.height(16.dp))

            // Quick emoji row
            QuickEmojiRow(
                emojis = state.quickEmojis,
                onRaiseHand = onRaiseHand,
                onMoreClick = onMoreEmojisClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Grid items
            MoreGrid(items = state.gridItems)

            Spacer(modifier = Modifier.height(20.dp))

            // Red Cancel button
            CancelButton(onDismiss = onDismiss)
        }
    }
}

// ── Reactions Page (Dark) ──────────────────────────────────────────────

@Composable
private fun ReactionsPage(
    state: MeetingMoreDetailedUiState,
    onRaiseHand: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color(0xFF2A2A2D))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {}
                .padding(top = 8.dp, bottom = 24.dp)
        ) {
            // Drag handle
            DragHandle()

            Spacer(modifier = Modifier.height(8.dp))

            // Header: X + "Reactions"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Reactions",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Raise hand button
            RaiseHandButton(
                onRaiseHand = onRaiseHand
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Non-verbal feedback row
            NonVerbalFeedbackRow(items = state.feedbackIcons)

            Spacer(modifier = Modifier.height(20.dp))

            // REACTIONS section
            EmojiSection(
                title = "REACTIONS",
                emojis = state.reactionEmojis
            )

            Spacer(modifier = Modifier.height(20.dp))

            // SEND WITH EFFECT section
            EmojiSection(
                title = "SEND WITH EFFECT",
                emojis = state.effectEmojis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your camera must be on to use effects.",
                color = Color(0xFF8E8E93),
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

// ── Shared Components ─────��────────────────────────────────────────────

@Composable
private fun DragHandle() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFFD0D0D0))
        )
    }
}

@Composable
private fun QuickEmojiRow(
    emojis: List<String>,
    onRaiseHand: () -> Unit,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Raise Hand capsule button
        Box(
            modifier = Modifier
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF0F0F0))
                .clickable { onRaiseHand() }
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "🖐", fontSize = 22.sp)
                Text(
                    text = "Raise Hand",
                    color = Color(0xFF333333),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        emojis.forEach { emoji ->
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F0F0))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 22.sp)
            }
        }

        // "..." button
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F0F0))
                .clickable(onClick = onMoreClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "More reactions",
                tint = Color(0xFF333333),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun MoreGrid(items: List<MoreGridItem>) {
    val columns = 3
    val rows = (items.size + columns - 1) / columns

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    if (index < items.size) {
                        MoreGridCell(
                            item = items[index],
                            icon = getIconForItem(items[index].label),
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun MoreGridCell(
    item: MoreGridItem,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val iconTint = if (item.enabled) Color(0xFF333333) else Color(0xFFBBBBBB)
    val labelColor = if (item.enabled) Color(0xFF333333) else Color(0xFFBBBBBB)

    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFF0F0F0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = item.label,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = item.label,
            color = labelColor,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 14.sp
        )
    }
}

@Composable
private fun CancelButton(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFDE8E8))
            .clickable(onClick = onDismiss)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Cancel",
            color = Color(0xFFE53935),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun RaiseHandButton(
    onRaiseHand: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF3A3A3D))
            .clickable { onRaiseHand() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🖐  Raise hand",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun NonVerbalFeedbackRow(items: List<FeedbackItem>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF3A3A3D))
                    .clickable { }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.emoji, fontSize = 22.sp)
            }
        }
    }
}

@Composable
private fun EmojiSection(
    title: String,
    emojis: List<String>
) {
    Text(
        text = title,
        color = Color(0xFF8E8E93),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF3A3A3D))
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        emojis.forEach { emoji ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 22.sp)
            }
        }

        // "..." more button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "More",
                tint = Color(0xFF8E8E93),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun getIconForItem(label: String): ImageVector {
    return when (label) {
        "Participants" -> Icons.Default.Groups
        "Share" -> Icons.AutoMirrored.Filled.ScreenShare
        "Show CC" -> Icons.Default.ClosedCaption
        "Notes" -> Icons.Default.Description
        "Apps" -> Icons.Default.Apps
        "Meeting info" -> Icons.Default.Info
        "Host tools" -> Icons.Default.AdminPanelSettings
        "Settings" -> Icons.Default.Settings
        else -> Icons.Default.Settings
    }
}
