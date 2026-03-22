package com.example.zoom.presentation.teamchat

import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.zoom.model.Message
import com.example.zoom.ui.components.TopBarIconAction
import com.example.zoom.ui.components.ZoomTopBar
import com.example.zoom.ui.theme.ZoomBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TeamChatScreen(
    onAvatarClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val chatItems = remember { mutableStateListOf<Message>() }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Mentions", "Chats", "Channels", "Meeting Chats", "Shared Spaces", "More")

    val view = remember {
        object : TeamChatContract.View {
            override fun showChatList(chats: List<Message>) {
                chatItems.clear()
                chatItems.addAll(chats)
            }
        }
    }

    LaunchedEffect(Unit) {
        TeamChatPresenter(view).loadData()
    }

    Scaffold(
        topBar = {
            ZoomTopBar(
                title = "Team Chat",
                onAvatarClick = onAvatarClick,
                actions = {
                    TopBarIconAction(
                        icon = Icons.Default.Search,
                        contentDescription = "Search",
                        onClick = onSearchClick
                    )
                    TopBarIconAction(
                        icon = Icons.Default.MoreHoriz,
                        contentDescription = "More"
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
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

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(chatItems) { chat ->
                    ChatItem(chat)
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                }
            }
        }
    }
}

@Composable
private fun ChatItem(message: Message) {
    val sdf = remember { SimpleDateFormat("MM/dd", Locale.getDefault()) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
                text = message.senderName.first().uppercase(),
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
                    text = message.senderName,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = sdf.format(Date(message.timestamp)),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = message.content,
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
