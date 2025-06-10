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
import java.time.temporal.ChronoUnit
import java.util.Locale

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

@Composable
fun formatTopTwoTimeUnits(seconds: Long): String {
    if (seconds <= 0L) return "0${stringResource(id = R.string.common_core_time_second)}"

    val days = seconds / SECONDS_PER_DAY
    val hours = (seconds % SECONDS_PER_DAY) / SECONDS_PER_HOUR
    val minutes = (seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE
    val secs = seconds % SECONDS_PER_MINUTE

    val parts = listOf(
        days to stringResource(id = R.string.common_core_time_day),
        hours to stringResource(id = R.string.common_core_time_hour),
        minutes to stringResource(id = R.string.common_core_time_minute),
        secs to stringResource(id = R.string.common_core_time_second),
    )

    val nonZeroParts = parts.filter { it.first > 0 }.take(2)

    return if (nonZeroParts.isNotEmpty()) {
        nonZeroParts.joinToString(" ") { "${it.first}${it.second}" }
    } else {
        "0${stringResource(id = R.string.common_core_time_second)}"
    }
}

@Composable
fun formatLargestTimeDuration(seconds: Long): String {
    if (seconds <= 0L) return "0${stringResource(R.string.common_core_time_second)}"

    val days = seconds / SECONDS_PER_DAY
    val hours = (seconds % SECONDS_PER_DAY) / SECONDS_PER_HOUR
    val minutes = (seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE
    val secs = seconds % SECONDS_PER_MINUTE

    return when {
        days > 0 -> "${days}${stringResource(R.string.common_core_time_day)}"
        hours > 0 -> "${hours}${stringResource(R.string.common_core_time_hour)}"
        minutes > 0 -> "${minutes}${stringResource(R.string.common_core_time_minute)}"
        else -> "${secs}${stringResource(R.string.common_core_time_second)}"
    }
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

private const val RECENT_DAYS_THRESHOLD = 7

@Composable
fun LocalDateTime.toDisplayTimeFormat(
    now: LocalDateTime = LocalDateTime.now(),
): String {
    val daysDifference = ChronoUnit.DAYS.between(this, now)
    val hoursDifference = ChronoUnit.HOURS.between(this, now)
    val minutesDifference = ChronoUnit.MINUTES.between(this, now)
    val secondsDifference = ChronoUnit.SECONDS.between(this, now)

    return when {
        daysDifference == 0L -> {
            when {
                hoursDifference == 0L -> {
                    when {
                        minutesDifference == 0L ->
                            stringResource(R.string.common_core_time_seconds_ago, secondsDifference)

                        else -> stringResource(R.string.common_core_time_minutes_ago, minutesDifference)
                    }
                }

                else -> stringResource(R.string.common_core_time_hours_ago, hoursDifference)
            }
        }

        daysDifference in 1..RECENT_DAYS_THRESHOLD ->
            stringResource(R.string.common_core_time_days_ago, daysDifference)

        year == now.year -> {
            String.format(
                Locale.getDefault(),
                "%02d/%02d %02d:%02d",
                monthValue,
                dayOfMonth,
                hour,
                minute,
            )
        }

        else -> {
            String.format(
                Locale.getDefault(),
                "%d/%02d/%02d %02d:%02d",
                year,
                monthValue,
                dayOfMonth,
                hour,
                minute,
            )
        }
    }
}
