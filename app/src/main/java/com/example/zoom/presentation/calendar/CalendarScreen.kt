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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.zoom.ui.components.ZoomTopBar
import com.example.zoom.ui.theme.ZoomBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(onAvatarClick: () -> Unit) {
    val meetings = remember { mutableStateListOf<Meeting>() }
    var isEmpty by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableIntStateOf(0) }

    val today = remember { Calendar.getInstance() }
    val weekDays = remember {
        (0..6).map { offset ->
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, offset) }
        }
    }
    val dayNames = remember { listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat") }

    val view = remember {
        object : CalendarContract.View {
            override fun showMeetings(list: List<Meeting>) {
                isEmpty = false
                meetings.clear()
                meetings.addAll(list)
            }
            override fun showEmpty() {
                isEmpty = true
                meetings.clear()
            }
        }
    }

    LaunchedEffect(selectedDay) {
        CalendarPresenter(view).loadData(weekDays[selectedDay].timeInMillis)
    }

    Scaffold(
        topBar = { ZoomTopBar(title = "Calendar", onAvatarClick = onAvatarClick) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                itemsIndexed(weekDays) { index, cal ->
                    val isSelected = index == selectedDay
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable { selectedDay = index }
                    ) {
                        Text(
                            dayNames[cal.get(Calendar.DAY_OF_WEEK) - 1],
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
                                "${cal.get(Calendar.DAY_OF_MONTH)}",
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color.Black
                            )
                        }
                    }
                }
            }

            if (isEmpty) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No meetings scheduled", fontSize = 16.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = ZoomBlue)
                        ) {
                            Text("Connect Calendar")
                        }
                    }
                }
            } else {
                val sdf = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
                LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                    items(meetings) { meeting ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
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
                }
            }
        }
    }
}
