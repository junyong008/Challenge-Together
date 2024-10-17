package com.yjy.common.core.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yjy.common.core.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun formatTimeDuration(seconds: Long): String {
    val days = seconds / (24 * 3600)
    val hours = (seconds % (24 * 3600)) / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    val dayPart = if (days > 0) "$days${stringResource(id = R.string.common_core_time_day)}" else ""
    val hourPart = if (hours > 0) "$hours${stringResource(id = R.string.common_core_time_hour)}" else ""
    val minutePart = if (minutes > 0) "$minutes${stringResource(id = R.string.common_core_time_minute)}" else ""
    val secondPart = if (secs > 0) "$secs${stringResource(id = R.string.common_core_time_second)}" else ""

    return listOf(dayPart, hourPart, minutePart, secondPart)
        .filter { it.isNotEmpty() }
        .joinToString(" ")
}

fun LocalDateTime.to12HourFormat(): Triple<Int, Int, Boolean> {
    val hour = if (this.hour % 12 == 0) 12 else this.hour % 12
    val minute = this.minute
    val isAm = this.hour < 12
    return Triple(hour, minute, isAm)
}

fun formatLocalDateTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return dateTime.format(formatter)
}
