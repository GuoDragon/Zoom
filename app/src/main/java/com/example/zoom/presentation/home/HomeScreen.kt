package com.example.zoom.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.R
import com.example.zoom.presentation.sharepage.SharePageScreen
import com.example.zoom.ui.components.ExpansionMenuItemUiState
import com.example.zoom.ui.components.ExpansionMenuOverlay
import com.example.zoom.ui.components.MeetingSessionConfig
import com.example.zoom.ui.components.ZoomTopBar
import com.example.zoom.ui.theme.ZoomBlue
import com.example.zoom.ui.theme.ZoomOrange

@Composable
fun HomeScreen(
    onAvatarClick: () -> Unit,
    onSearchClick: () -> Unit,
    onHostMeetingClick: () -> Unit,
    onJoinMeetingClick: () -> Unit,
    onScheduleMeetingClick: () -> Unit,
    onShareMeetingClick: (MeetingSessionConfig) -> Unit,
    onScheduledMeetingClick: (String) -> Unit
) {
    val upcomingMeetings = remember { mutableStateListOf<HomeMeetingCardUi>() }
    var avatarInitial by remember { mutableStateOf("?") }
    var showShareOverlay by remember { mutableStateOf(false) }
    var showMoreOverlay by remember { mutableStateOf(false) }
    val expansionMenuItems = listOf(
        ExpansionMenuItemUiState(stringResource(R.string.home_expansion_personal_id), "ID"),
        ExpansionMenuItemUiState(stringResource(R.string.home_expansion_scan_qr), "QR"),
        ExpansionMenuItemUiState(stringResource(R.string.home_expansion_transfer_meeting), "TR")
    )

    val view = remember {
        object : HomeContract.View {
            override fun showHomeState(state: HomeUiState) {
                avatarInitial = state.currentUser.username.firstOrNull()?.uppercase() ?: "?"
                upcomingMeetings.clear()
                upcomingMeetings.addAll(state.upcomingMeetings)
            }
        }
    }

    val presenter = remember(view) { HomePresenter(view) }
    val meetingDataVersion by presenter.observeRuntimeVersion().collectAsState()

    LaunchedEffect(meetingDataVersion) {
        presenter.loadData()
    }

    Scaffold(
        topBar = {
            ZoomTopBar(
                title = stringResource(R.string.app_name),
                avatarInitial = avatarInitial,
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
                            label = stringResource(R.string.home_action_meet),
                            color = ZoomOrange,
                            onClick = onHostMeetingClick
                        )
                        HomeActionCard(
                            icon = Icons.AutoMirrored.Filled.Login,
                            label = stringResource(R.string.home_action_join),
                            color = ZoomBlue,
                            onClick = onJoinMeetingClick
                        )
                        HomeActionCard(
                            icon = Icons.Default.CalendarMonth,
                            label = stringResource(R.string.home_action_schedule),
                            color = ZoomBlue,
                            onClick = onScheduleMeetingClick
                        )
                        HomeActionCard(
                            icon = Icons.AutoMirrored.Filled.ScreenShare,
                            label = stringResource(R.string.home_action_share),
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
                                Text(
                                    stringResource(R.string.home_no_upcoming_meetings),
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    stringResource(R.string.home_upcoming_meetings_hint),
                                    fontSize = 14.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Text(
                            stringResource(R.string.home_upcoming_meetings_title),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(upcomingMeetings) { meeting ->
                        MeetingItem(
                            meeting = meeting,
                            onClick = onScheduledMeetingClick
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            if (showShareOverlay) {
                SharePageScreen(
                    onDismiss = { showShareOverlay = false },
                    onShareSuccess = { config ->
                        showShareOverlay = false
                        onShareMeetingClick(config)
                    }
                )
            }

            if (showMoreOverlay) {
                ExpansionMenuOverlay(
                    items = expansionMenuItems,
                    footerText = stringResource(R.string.home_expansion_footer_add_calendar),
                    onDismiss = { showMoreOverlay = false }
                )
            }
        }
    }
}

@Composable
private fun MeetingItem(
    meeting: HomeMeetingCardUi,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(meeting.meeting.meetingId) },
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
            Column(modifier = Modifier.weight(1f)) {
                Text(meeting.meeting.topic, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = meeting.timeLabel,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
