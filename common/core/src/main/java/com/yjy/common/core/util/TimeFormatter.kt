package com.yjy.common.core.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yjy.common.core.R
import com.yjy.common.core.constants.TimeConst.HOURS_PER_HALF_DAY
import com.yjy.common.core.constants.TimeConst.SECONDS_PER_DAY
import com.yjy.common.core.constants.TimeConst.SECONDS_PER_HOUR
import com.yjy.common.core.constants.TimeConst.SECONDS_PER_MINUTE
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun formatTimeDuration(seconds: Long): String {
    if (seconds <= 0L) return "0${stringResource(id = R.string.common_core_time_second)}"

    val days = seconds / SECONDS_PER_DAY
    val hours = (seconds % SECONDS_PER_DAY) / SECONDS_PER_HOUR
    val minutes = (seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE
    val secs = seconds % SECONDS_PER_MINUTE

    var hasLargerUnit = false

    val parts = mutableListOf<String>()

    if (days > 0) {
        parts.add("$days${stringResource(id = R.string.common_core_time_day)}")
        hasLargerUnit = true
    }

    if (hasLargerUnit || hours > 0) {
        parts.add("$hours${stringResource(id = R.string.common_core_time_hour)}")
        hasLargerUnit = true
    }

    if (hasLargerUnit || minutes > 0) {
        parts.add("$minutes${stringResource(id = R.string.common_core_time_minute)}")
        hasLargerUnit = true
    }

    if (hasLargerUnit || secs > 0) {
        parts.add("$secs${stringResource(id = R.string.common_core_time_second)}")
    }

    return parts.joinToString(" ")
}

fun LocalDateTime.to12HourFormat(): Triple<Int, Int, Boolean> {
    val hour = if (this.hour % HOURS_PER_HALF_DAY == 0) HOURS_PER_HALF_DAY else this.hour % HOURS_PER_HALF_DAY
    val minute = this.minute
    val isAm = this.hour < HOURS_PER_HALF_DAY
    return Triple(hour, minute, isAm)
}

fun formatLocalDateTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return dateTime.format(formatter)
}
