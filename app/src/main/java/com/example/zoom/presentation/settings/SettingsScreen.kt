package com.example.zoom.presentation.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.components.ProfileCard
import com.example.zoom.ui.components.ProfileCardDivider
import com.example.zoom.ui.components.ProfileListRow
import com.example.zoom.ui.components.ProfilePageBackground
import com.example.zoom.ui.components.ZoomTopBarInsets
import com.example.zoom.ui.theme.ZoomBlue
import com.example.zoom.ui.theme.ZoomTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    var uiState by remember { mutableStateOf<SettingsUiState?>(null) }

    val view = remember {
        object : SettingsContract.View {
            override fun showSettings(content: SettingsUiState) {
                uiState = content
            }
        }
    }

    LaunchedEffect(Unit) {
        SettingsPresenter(view).loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                windowInsets = ZoomTopBarInsets,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        uiState?.let { screenState ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item { Spacer(modifier = Modifier.height(12.dp)) }

                itemsIndexed(screenState.groups) { _, group ->
                    ProfilePageBackground(modifier = Modifier.fillParentMaxWidth()) {
                        ProfileCard {
                            group.items.forEachIndexed { index, item ->
                                val icon = itemIcon(item.id)
                                ProfileListRow(
                                    title = item.title,
                                    leadingIcon = icon,
                                    iconTint = ZoomTextSecondary
                                )
                                if (index != group.items.lastIndex) {
                                    ProfileCardDivider()
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }

                item {
                    ProfilePageBackground(modifier = Modifier.fillParentMaxWidth()) {
                        ProfileCard {
                            screenState.accountActions.forEachIndexed { index, item ->
                                AccountActionRow(
                                    title = item.title,
                                    color = if (item.id == "sign_out") Color(0xFFD94C45) else ZoomBlue
                                )
                                if (index != screenState.accountActions.lastIndex) {
                                    ProfileCardDivider()
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountActionRow(title: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = color,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun itemIcon(id: String): ImageVector {
    return when (id) {
        "general" -> Icons.Default.Settings
        "notifications" -> Icons.Default.NotificationsNone
        "video" -> Icons.Default.Videocam
        "audio" -> Icons.Default.Headset
        "accessibility" -> Icons.Default.Accessibility
        "meetings" -> Icons.Default.Videocam
        "team_chat" -> Icons.Default.ChatBubbleOutline
        "calendar" -> Icons.Default.CalendarMonth
        "siri" -> Icons.Default.GraphicEq
        "qr" -> Icons.Default.QrCode2
        else -> Icons.Default.Info
    }
}
