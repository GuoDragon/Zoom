package com.example.zoom.presentation.schedulemeeting

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatScheduleStartLabel(startTimeMillis: Long, timeZoneId: String): String {
    val zone = ZoneId.of(timeZoneId)
    val startDateTime = Instant.ofEpochMilli(startTimeMillis).atZone(zone)
    val today = LocalDate.now(zone)
    val date = startDateTime.toLocalDate()
    val timeLabel = startDateTime.format(DateTimeFormatter.ofPattern("HH:mm", Locale.US))

    return when (date) {
        today -> "Today at $timeLabel"
        today.plusDays(1) -> "Tomorrow at $timeLabel"
        else -> "${startDateTime.format(DateTimeFormatter.ofPattern("MMM d", Locale.US))} at $timeLabel"
    }
}

fun formatDurationLabel(durationMinutes: Int): String {
    val hours = durationMinutes / 60
    val minutes = durationMinutes % 60
    return when {
        hours == 0 -> "$minutes mins"
        minutes == 0 -> "$hours hr"
        else -> "$hours hr $minutes min"
    }
}

fun buildStartTimeOptions(
    selectedTimeMillis: Long,
    timeZoneId: String,
    daysForward: Int = 14
): List<Long> {
    val zone = ZoneId.of(timeZoneId)
    val now = ZonedDateTime.now(zone).withSecond(0).withNano(0)
    val roundedNow = now.plusMinutes(15 - (now.minute % 15).toLong()).withSecond(0).withNano(0)
    val options = mutableListOf<Long>()

    var cursor = roundedNow
    val stepCount = daysForward * 24 * 4
    repeat(stepCount) {
        options.add(cursor.toInstant().toEpochMilli())
        cursor = cursor.plusMinutes(15)
    }

    if (selectedTimeMillis !in options) {
        options.add(selectedTimeMillis)
    }
    return options.sorted()
}
