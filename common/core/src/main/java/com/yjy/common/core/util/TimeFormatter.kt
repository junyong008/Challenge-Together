package com.yjy.common.core.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yjy.common.core.R

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
