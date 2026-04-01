package com.example.zoom.presentation.schedulemeetingdetailedcalendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.components.ZoomInsetDivider
import com.example.zoom.ui.components.ZoomSettingValueRow
import com.example.zoom.ui.components.ZoomTopBarInsets
import com.example.zoom.ui.theme.ZoomBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleMeetingDetailedInCalendarScreen(
    meetingId: String,
    onBackClick: () -> Unit,
    onStartClick: (String) -> Unit
) {
    var uiState by remember { mutableStateOf<ScheduleMeetingDetailedInCalendarUiState?>(null) }

    val view = remember {
        object : ScheduleMeetingDetailedInCalendarContract.View {
            override fun showContent(content: ScheduleMeetingDetailedInCalendarUiState) {
                uiState = content
            }
        }
    }
    val presenter = remember(view) { ScheduleMeetingDetailedInCalendarPresenter(view) }

    LaunchedEffect(presenter, meetingId) {
        presenter.loadData(meetingId)
    }

    val state = uiState ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Schedule meeting",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    Row(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable(onClick = onBackClick),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ZoomBlue
                        )
                        Text(
                            text = "Back",
                            color = ZoomBlue,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                },
                windowInsets = ZoomTopBarInsets,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF7F9FC), RoundedCornerShape(14.dp))
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = state.meetingTitle,
                    fontSize = 22.sp,
                    color = Color(0xFF2B3645),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                )
                ZoomInsetDivider()
                ZoomSettingValueRow(
                    title = "Starts",
                    value = state.startsLabel,
                    showChevron = false
                )
                ZoomInsetDivider()
                ZoomSettingValueRow(
                    title = "Duration",
                    value = state.durationLabel,
                    showChevron = false
                )
                ZoomInsetDivider()
                ZoomSettingValueRow(
                    title = "Invitees",
                    value = state.inviteeSummary,
                    showChevron = false
                )
                ZoomInsetDivider()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Description",
                        color = Color(0xFF6D7785),
                        fontSize = 13.sp
                    )
                    Text(
                        text = state.description,
                        color = Color(0xFF2B3645),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            Text(
                text = "Start",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 14.dp)
                    .fillMaxWidth()
                    .background(ZoomBlue, RoundedCornerShape(14.dp))
                    .clickable { onStartClick(state.meetingId) }
                    .padding(vertical = 14.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
