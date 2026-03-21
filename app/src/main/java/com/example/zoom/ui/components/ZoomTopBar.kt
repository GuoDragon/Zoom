package com.example.zoom.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.data.DataRepository
import com.example.zoom.ui.theme.ZoomGreen

val ZoomTopBarInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoomTopBar(
    title: String,
    onAvatarClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {
        TopBarIconAction(icon = Icons.Default.Search, contentDescription = "Search")
        TopBarIconAction(icon = Icons.Default.MoreHoriz, contentDescription = "More")
    }
) {
    val currentUser = DataRepository.getCurrentUser()
    val initial = currentUser.username.first().uppercase()

    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ZoomGreen)
                    .clickable { onAvatarClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        actions = actions,
        windowInsets = ZoomTopBarInsets,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
fun RowScope.TopBarIconAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit = {}
) {
    IconButton(onClick = onClick) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFFF7F8FA))
                .border(width = 1.dp, color = Color(0xFFE7EAF0), shape = CircleShape)
                .semantics { this.contentDescription = contentDescription },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF556274),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun RowScope.TopBarEmojiAction(
    emoji: String,
    contentDescription: String,
    onClick: () -> Unit = {}
) {
    IconButton(onClick = onClick) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFFF7F8FA))
                .border(width = 1.dp, color = Color(0xFFE7EAF0), shape = CircleShape)
                .semantics { this.contentDescription = contentDescription },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                color = Color(0xFF556274),
                fontSize = 14.sp
            )
        }
    }
}
