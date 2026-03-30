package com.example.zoom.presentation.schedulemeeting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.theme.ZoomBlue
import com.example.zoom.ui.theme.ZoomTextPrimary
import com.example.zoom.ui.theme.ZoomTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleStartsTimeDialog(
    selectedTimeMillis: Long,
    options: List<Long>,
    timeZoneId: String,
    onSelect: (Long) -> Unit,
    onDone: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = null
    ) {
        SchedulePickerHeader(onDone = onDone)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
        ) {
            items(options) { option ->
                SchedulePickerRow(
                    label = formatScheduleStartLabel(option, timeZoneId),
                    selected = option == selectedTimeMillis,
                    onClick = { onSelect(option) }
                )
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
