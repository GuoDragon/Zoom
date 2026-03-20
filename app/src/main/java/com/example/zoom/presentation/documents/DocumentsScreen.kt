package com.example.zoom.presentation.documents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.components.TopBarEmojiAction
import com.example.zoom.ui.components.ZoomTopBar
import com.example.zoom.ui.theme.ZoomBlue

@Composable
fun DocumentsScreen(onAvatarClick: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Recent", "Started", "My docs", "Shared folders", "Shared with me", "Trash")
    var isEmpty by remember { mutableStateOf(false) }

    val view = remember {
        object : DocumentsContract.View {
            override fun showEmpty() {
                isEmpty = true
            }
        }
    }

    LaunchedEffect(Unit) {
        DocumentsPresenter(view).loadData()
    }

    Scaffold(
        topBar = {
            ZoomTopBar(
                title = "Docs",
                onAvatarClick = onAvatarClick,
                actions = {
                    TopBarEmojiAction(emoji = "🔍", contentDescription = "Search")
                    TopBarEmojiAction(emoji = "🔔", contentDescription = "Notifications")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = ZoomBlue
            ) {
                Text(text = "＋", color = Color.White, fontSize = 24.sp)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
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

            if (isEmpty) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📄", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("There are no files here yet", fontSize = 16.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
