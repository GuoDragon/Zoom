package com.example.zoom.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.ScreenShare
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.model.Meeting
import com.example.zoom.ui.components.ExpansionMenuItemUiState
import com.example.zoom.ui.components.ExpansionMenuOverlay
import com.example.zoom.ui.components.ZoomTopBar
import com.example.zoom.ui.theme.ZoomBlue
import com.example.zoom.ui.theme.ZoomOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onAvatarClick: () -> Unit,
    onSearchClick: () -> Unit,
    onHostMeetingClick: () -> Unit,
    onJoinMeetingClick: () -> Unit,
    onScheduleMeetingClick: () -> Unit
) {
    val upcomingMeetings = remember { mutableStateListOf<Meeting>() }
    var showShareOverlay by remember { mutableStateOf(false) }
    var showMoreOverlay by remember { mutableStateOf(false) }
    val expansionMenuItems = remember {
        listOf(
            ExpansionMenuItemUiState("Meet with Personal ID", "ID"),
            ExpansionMenuItemUiState("Scan QR code", "QR"),
            ExpansionMenuItemUiState("Transfer a meeting", "TR")
        )
    }

    val view = remember {
        object : HomeContract.View {
            override fun showUpcomingMeetings(meetings: List<Meeting>) {
                upcomingMeetings.clear()
                upcomingMeetings.addAll(meetings)
            }
            override fun showCurrentUser(user: com.example.zoom.model.User) = Unit
        }
    }

    LaunchedEffect(Unit) {
        HomePresenter(view).loadData()
    }

    Scaffold(
        topBar = {
            ZoomTopBar(
                title = "Zoom",
                onAvatarClick = onAvatarClick,
                onSearchClick = onSearchClick,
                onMoreClick = { showMoreOverlay = true }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        HomeActionCard(
                            icon = Icons.Default.Add,
                            label = "Meet",
                            color = ZoomOrange,
                            onClick = onHostMeetingClick
                        )
                        HomeActionCard(
                            icon = Icons.AutoMirrored.Filled.Login,
                            label = "Join",
                            color = ZoomBlue,
                            onClick = onJoinMeetingClick
                        )
                        HomeActionCard(
                            icon = Icons.Default.CalendarMonth,
                            label = "Schedule",
                            color = ZoomBlue,
                            onClick = onScheduleMeetingClick
                        )
                        HomeActionCard(
                            icon = Icons.AutoMirrored.Filled.ScreenShare,
                            label = "Share",
                            color = ZoomBlue,
                            onClick = { showShareOverlay = true }
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                if (upcomingMeetings.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No upcoming meetings", fontSize = 16.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Your upcoming meetings will appear here",
                                    fontSize = 14.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Text("Upcoming Meetings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(upcomingMeetings) { meeting ->
                        MeetingItem(meeting)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            if (showShareOverlay) {
                ShareScreenOverlay(onDismiss = { showShareOverlay = false })
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
private fun MeetingItem(meeting: Meeting) {
    val sdf = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(ZoomBlue, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(meeting.topic, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    sdf.format(Date(meeting.startTime)) +
                        if (meeting.endTime != null) " - ${sdf.format(Date(meeting.endTime))}" else "",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
