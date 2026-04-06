package com.example.zoom.presentation.schedulemeeting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.theme.ZoomBlue
import com.example.zoom.ui.theme.ZoomTextPrimary
import com.example.zoom.ui.theme.ZoomTextSecondary
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleStartsTimeDialog(
    selectedTimeMillis: Long,
    timeZoneId: String,
    onSelect: (Long) -> Unit,
    onDone: () -> Unit,
    onDismiss: () -> Unit
) {
    val zone = remember(timeZoneId) { ZoneId.of(timeZoneId) }
    val initialDateTime = remember(selectedTimeMillis, timeZoneId) {
        Instant.ofEpochMilli(selectedTimeMillis)
            .atZone(zone)
            .withSecond(0)
            .withNano(0)
    }
    val selectableDates = remember(selectedTimeMillis, timeZoneId) {
        buildSelectableDates(initialDateTime.toLocalDate(), zone)
    }
    val quickTimeOptions = remember {
        listOf(
            8 to 0,
            10 to 0,
            12 to 0,
            13 to 0,
            19 to 0
        )
    }

    var selectedDate by remember(selectedTimeMillis, timeZoneId) {
        mutableStateOf(initialDateTime.toLocalDate())
    }
    var selectedHour by remember(selectedTimeMillis, timeZoneId) {
        mutableIntStateOf(initialDateTime.hour)
    }
    var selectedMinute by remember(selectedTimeMillis, timeZoneId) {
        mutableIntStateOf((initialDateTime.minute / 15) * 15)
    }

    fun updateSelection(
        date: LocalDate = selectedDate,
        hour: Int = selectedHour,
        minute: Int = selectedMinute
    ) {
        selectedDate = date
        selectedHour = hour
        selectedMinute = minute
        val updatedMillis = ZonedDateTime.of(date, LocalTime.of(hour, minute), zone)
            .toInstant()
            .toEpochMilli()
        onSelect(updatedMillis)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = null
    ) {
        SchedulePickerHeader(onDone = onDone)

        Column(
            modifier = Modifier
                .heightIn(max = 560.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = formatScheduleStartLabel(selectedTimeMillis, timeZoneId),
                color = ZoomTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = timeZoneId,
                color = ZoomTextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "Date",
                color = ZoomTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 18.dp, bottom = 10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectableDates.forEach { date ->
                    PickerChip(
                        label = formatPickerDateLabel(date, zone),
                        selected = date == selectedDate,
                        onClick = { updateSelection(date = date) }
                    )
                }
            }

            Text(
                text = "Quick time",
                color = ZoomTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 18.dp, bottom = 10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickTimeOptions.forEach { (hour, minute) ->
                    PickerChip(
                        label = formatQuickTimeLabel(hour, minute),
                        selected = hour == selectedHour && minute == selectedMinute,
                        onClick = { updateSelection(hour = hour, minute = minute) }
                    )
                }
            }

            Text(
                text = "Hour",
                color = ZoomTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 22.dp, bottom = 10.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (row in 0 until 6) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (column in 0 until 4) {
                            val hour = row * 4 + column
                            PickerGridChip(
                                label = hour.toString().padStart(2, '0'),
                                selected = hour == selectedHour,
                                modifier = Modifier.weight(1f),
                                onClick = { updateSelection(hour = hour) }
                            )
                        }
                    }
                }
            }

            Text(
                text = "Minute",
                color = ZoomTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 22.dp, bottom = 10.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(0, 15, 30, 45).forEach { minute ->
                    PickerGridChip(
                        label = minute.toString().padStart(2, '0'),
                        selected = minute == selectedMinute,
                        modifier = Modifier.weight(1f),
                        onClick = { updateSelection(minute = minute) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDurationDialog(
    selectedDurationMinutes: Int,
    onSelect: (Int) -> Unit,
    onDone: () -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(15, 30, 45, 60, 90, 120, 180, 240)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = null
    ) {
        SchedulePickerHeader(onDone = onDone)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            items(options) { option ->
                SchedulePickerRow(
                    label = formatDurationLabel(option),
                    selected = option == selectedDurationMinutes,
                    onClick = { onSelect(option) }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SchedulePickerHeader(onDone: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.size(42.dp))
        Text(
            text = "Done",
            color = ZoomBlue,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable(onClick = onDone)
        )
    }
    HorizontalDivider(thickness = 0.6.dp, color = Color(0xFFE7EBF0))
}

@Composable
private fun PickerChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (selected) Color(0xFFEAF2FF) else Color(0xFFF5F7FA),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) ZoomBlue else ZoomTextPrimary,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun PickerGridChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = if (selected) Color(0xFFEAF2FF) else Color(0xFFF5F7FA),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) ZoomBlue else ZoomTextPrimary,
            fontSize = 15.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun SchedulePickerRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (selected) Color(0xFFF5F9FF) else Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = ZoomTextPrimary,
            fontSize = 17.sp
        )
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = ZoomBlue
            )
        } else {
            Text(
                text = "",
                color = ZoomTextSecondary,
                fontSize = 14.sp
            )
        }
    }
    HorizontalDivider(thickness = 0.6.dp, color = Color(0xFFE7EBF0))
}

private fun buildSelectableDates(
    selectedDate: LocalDate,
    zoneId: ZoneId,
    daysForward: Int = 14
): List<LocalDate> {
    val today = LocalDate.now(zoneId)
    val dates = (0 until daysForward)
        .map { today.plusDays(it.toLong()) }
        .toMutableSet()
    dates.add(selectedDate)
    return dates.toList().sorted()
}

private fun formatPickerDateLabel(date: LocalDate, zoneId: ZoneId): String {
    val today = LocalDate.now(zoneId)
    return when (date) {
        today -> "Today"
        today.plusDays(1) -> "Tomorrow"
        else -> date.format(DateTimeFormatter.ofPattern("MMM d", Locale.US))
    }
}

private fun formatQuickTimeLabel(hour: Int, minute: Int): String {
    return String.format(Locale.US, "%02d:%02d", hour, minute)
}
