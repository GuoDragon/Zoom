package com.example.zoom.presentation.teamchat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.components.ExpansionMenuItemUiState
import com.example.zoom.ui.components.ExpansionMenuOverlay
import com.example.zoom.ui.components.TopBarIconAction
import com.example.zoom.ui.components.ZoomTopBar
import com.example.zoom.ui.theme.ZoomBlue
import com.example.zoom.data.DataRepository

@Composable
fun TeamChatScreen(
    onAvatarClick: () -> Unit,
    onSearchClick: () -> Unit,
    onContactsClick: () -> Unit,
    onDirectChatClick: (String) -> Unit
) {
    val chatItems = remember { mutableStateListOf<TeamChatThreadUi>() }
    var avatarInitial by remember { mutableStateOf("?") }
    var unreadSessionCount by remember { mutableIntStateOf(0) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showMoreOverlay by remember { mutableStateOf(false) }
    val tabs = listOf("All", "Mentions", "Chats", "Channels", "Meeting Chats", "Shared Spaces", "More")
    val expansionMenuItems = remember {
        listOf(
            ExpansionMenuItemUiState("Meet with Personal ID", "ID"),
            ExpansionMenuItemUiState("Scan QR code", "QR"),
            ExpansionMenuItemUiState("Transfer a meeting", "TR")
        )
    }

    val view = remember {
        object : TeamChatContract.View {
            override fun showUiState(state: TeamChatUiState) {
                avatarInitial = state.currentUserInitial
                chatItems.clear()
                chatItems.addAll(state.chats)
                unreadSessionCount = state.unreadSessionCount
            }
        }
    }
    val presenter = remember(view) { TeamChatPresenter(view) }
    val runtimeVersion by DataRepository.observeMeetingDataVersion().collectAsState()

    LaunchedEffect(runtimeVersion) {
        presenter.loadData()
    }

    Scaffold(
        topBar = {
            ZoomTopBar(
                title = "Team Chat",
                avatarInitial = avatarInitial,
                onAvatarClick = onAvatarClick,
                actions = {
                    TopBarIconAction(
                        icon = Icons.Default.Search,
                        contentDescription = "Search",
                        onClick = onSearchClick
                    )
                    TopBarIconAction(
                        icon = Icons.Default.MoreHoriz,
                        contentDescription = "More",
                        onClick = { showMoreOverlay = true }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onContactsClick,
                containerColor = ZoomBlue
            ) {
                Text(
                    text = "+",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = Color.White,
                contentColor = ZoomBlue
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontSize = 13.sp) }
                    )
                }
            }

            Text(
                text = "Unread sessions: $unreadSessionCount",
                color = Color(0xFF6D7785),
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(chatItems) { chat ->
                    ChatItem(
                        chat = chat,
                        onClick = {
                            chat.directUserId?.let(onDirectChatClick)
                        }
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                }
            }

            if (showMoreOverlay) {
                ExpansionMenuOverlay(
                    items = expansionMenuItems,
                    footerText = "Add a calendar",
                    onDismiss = { showMoreOverlay = false }
                )
            }
        }
    }
}

@Composable
private fun ChatItem(
    chat: TeamChatThreadUi,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = chat.avatarText,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = chat.dateLabel,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = chat.preview,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (chat.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .background(ZoomBlue, CircleShape)
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chat.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
