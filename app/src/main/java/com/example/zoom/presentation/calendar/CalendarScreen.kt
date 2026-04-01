package com.example.zoom.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.model.Meeting
import com.example.zoom.ui.components.TopBarIconAction
import com.example.zoom.ui.components.ZoomTopBar
import com.example.zoom.ui.theme.ZoomBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private enum class CalendarViewMode {
    Weekly,
    Agenda
}

@Composable
fun CalendarScreen(
    onAvatarClick: () -> Unit,
    onSearchClick: () -> Unit,
    onMeetingClick: (String) -> Unit
) {
    val meetingItems = remember { mutableStateListOf<Meeting>() }
    var avatarInitial by remember { mutableStateOf("?") }
    var isEmpty by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableIntStateOf(0) }
    var viewMode by remember { mutableStateOf(CalendarViewMode.Weekly) }

    val weekDays = remember {
        (0..6).map { offset ->
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, offset) }
        }
    }
    val dayNames = remember { listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat") }
    val monthFormatter = remember { SimpleDateFormat("MMMM", Locale.ENGLISH) }

    val view = remember {
        object : CalendarContract.View {
            override fun showUiState(state: CalendarUiState) {
                avatarInitial = state.currentUserInitial
                isEmpty = state.isEmpty
                meetingItems.clear()
                meetingItems.addAll(state.meetings)
            }
        }
    }
    val presenter = remember(view) { CalendarPresenter(view) }
    val meetingDataVersion by presenter.observeRuntimeVersion().collectAsState()

    LaunchedEffect(selectedDay, meetingDataVersion) {
        presenter.loadData(weekDays[selectedDay].timeInMillis)
    }

    Scaffold(
        topBar = {
            ZoomTopBar(
                title = "Calendar",
                avatarInitial = avatarInitial,
                onAvatarClick = onAvatarClick,
                actions = {
                    TopBarIconAction(
                        icon = Icons.Default.Search,
                        contentDescription = "Search",
                        onClick = onSearchClick
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
                    fontSize = 24.sp,
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
            CalendarFunctionBar(
                monthLabel = monthFormatter.format(weekDays[selectedDay].time),
                viewMode = viewMode,
                onWeeklyClick = { viewMode = CalendarViewMode.Weekly },
                onAgendaClick = { viewMode = CalendarViewMode.Agenda }
            )

            WeekDaySelector(
                weekDays = weekDays,
                dayNames = dayNames,
                selectedDay = selectedDay,
                onDayClick = { selectedDay = it }
            )

            if (isEmpty) {
                EmptyCalendarState()
            } else if (viewMode == CalendarViewMode.Agenda) {
                AgendaMeetingList(
                    meetings = meetingItems,
                    onMeetingClick = onMeetingClick
                )
            } else {
                WeeklyMeetingList(
                    meetings = meetingItems,
                    onMeetingClick = onMeetingClick
                )
            }
        }
    }
}

@Composable
private fun CalendarFunctionBar(
    monthLabel: String,
    viewMode: CalendarViewMode,
    onWeeklyClick: () -> Unit,
    onAgendaClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = Color(0xFF444444),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = monthLabel, fontSize = 24.sp, fontWeight = FontWeight.Medium)
        }

        ViewModeButton(
            label = "W",
            selected = viewMode == CalendarViewMode.Weekly,
            onClick = onWeeklyClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        ViewModeButton(
            label = "A",
            selected = viewMode == CalendarViewMode.Agenda,
            onClick = onAgendaClick
        )
    }
}

@Composable
private fun ViewModeButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Color(0xFFEFF4FF) else Color(0xFFF7F7F7))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) ZoomBlue else Color(0xFF5E6B85)
        )
    }
}

@Composable
private fun WeekDaySelector(
    weekDays: List<Calendar>,
    dayNames: List<String>,
    selectedDay: Int,
    onDayClick: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        itemsIndexed(weekDays) { index, cal ->
            val isSelected = index == selectedDay
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onDayClick(index) }
            ) {
                Text(
                    text = dayNames[cal.get(Calendar.DAY_OF_WEEK) - 1],
                    fontSize = 12.sp,
                    color = if (isSelected) ZoomBlue else Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) ZoomBlue else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${cal.get(Calendar.DAY_OF_MONTH)}",
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCalendarState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3F5F8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color(0xFF98A5B5),
                    modifier = Modifier.size(44.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "You haven't connected your calendar yet. Connect now to manage all your meetings and events in one place.",
                fontSize = 18.sp,
                color = Color(0xFF4A4A4A)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Connect calendar",
                fontSize = 22.sp,
                color = ZoomBlue
            )
        }
    }
}

@Composable
private fun WeeklyMeetingList(
    meetings: List<Meeting>,
    onMeetingClick: (String) -> Unit
) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
        items(meetings) { meeting ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onMeetingClick(meeting.meetingId) },
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
                        Text(text = meeting.topic, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = timeFormatter.format(Date(meeting.startTime)) +
                                if (meeting.endTime != null) {
                                    " - ${timeFormatter.format(Date(meeting.endTime))}"
                                } else {
                                    ""
                                },
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AgendaMeetingList(
    meetings: List<Meeting>,
    onMeetingClick: (String) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("EEE, MMM d", Locale.ENGLISH) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
        items(meetings) { meeting ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onMeetingClick(meeting.meetingId) },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = dateFormatter.format(Date(meeting.startTime)),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = meeting.topic, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Time: " + timeFormatter.format(Date(meeting.startTime)) +
                            if (meeting.endTime != null) {
                                " - ${timeFormatter.format(Date(meeting.endTime))}"
                            } else {
                                ""
                            },
                        fontSize = 13.sp,
                        color = Color(0xFF5E6B85)
                    )
                }
            }
        }
    }
}
