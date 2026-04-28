package com.example.zoom.presentation.search.detail.meetingdetail

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.components.ZoomTopBarInsets
import com.example.zoom.ui.theme.ZoomBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMeetingDetailScreen(
    meetingId: String,
    onBackClick: () -> Unit,
    onMeetingCancelled: () -> Unit
) {
    var uiState by remember { mutableStateOf<SearchMeetingDetailUiState?>(null) }

    val view = remember {
        object : SearchMeetingDetailContract.View {
            override fun showContent(content: SearchMeetingDetailUiState) {
                uiState = content
            }
        }
    }
    val presenter = remember(view, meetingId) { SearchMeetingDetailPresenter(view, meetingId) }

    LaunchedEffect(Unit) {
        presenter.loadData()
    }

    uiState?.let { state ->
        Scaffold(
            containerColor = Color.White,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Meeting Details",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF243447)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = ZoomBlue
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                    windowInsets = ZoomTopBarInsets
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    MeetingInfoCard(state)
                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    Text(
                        text = "Participants (${state.participants.size})",
                        color = Color(0xFF3B495A),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(state.participants) { participant ->
                    ParticipantRow(participant)
                    HorizontalDivider(color = Color(0xFFF1F3F6))
                }

                if (state.canCancel) {
                    item {
                        Text(
                            text = "Cancel meeting",
                            color = Color(0xFFD94C45),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .fillMaxWidth()
                                .background(Color(0xFFFFF1F0), RoundedCornerShape(12.dp))
                                .clickable {
                                    if (presenter.cancelMeeting()) {
                                        onMeetingCancelled()
                                    }
                                }
                                .padding(vertical = 14.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun MeetingInfoCard(state: SearchMeetingDetailUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F9FB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = state.topic,
                color = Color(0xFF243447),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Color(0xFF6E7A89),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = state.dateTimeLabel,
                    color = Color(0xFF6E7A89),
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF6E7A89),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Duration: ${state.durationLabel}",
                    color = Color(0xFF6E7A89),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ParticipantRow(participant: ParticipantItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF5CB85C)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = participant.initial,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = participant.username,
                color = Color(0xFF243447),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            if (participant.email.isNotEmpty()) {
                Text(
                    text = participant.email,
                    color = Color(0xFF95A0AE),
                    fontSize = 13.sp
                )
            }
        }
    }
}
