package com.yjy.platform.widget.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.glance.GlanceComposable
import com.yjy.common.core.constants.TimeConst.SECONDS_PER_DAY
import com.yjy.common.core.constants.TimeConst.SECONDS_PER_HOUR
import com.yjy.platform.widget.R

@Composable
@GlanceComposable
fun formatGlanceSimpleTimeDuration(seconds: Long, context: Context): String {
    if (seconds <= 0L) return "0${context.getString(R.string.platform_widget_time_day)}"

    val days = seconds / SECONDS_PER_DAY
    val hours = (seconds % SECONDS_PER_DAY) / SECONDS_PER_HOUR

    return when {
        days > 0 -> "$days${context.getString(R.string.platform_widget_time_day)} " +
            "$hours${context.getString(R.string.platform_widget_time_hour)}"
        else -> "$hours${context.getString(R.string.platform_widget_time_hour)}"
    }
}

@Composable
@GlanceComposable
fun formatGlanceDaysOnly(seconds: Long, context: Context): String {
    if (seconds <= 0L) return "0 ${context.getString(R.string.platform_widget_time_d)}"

    val days = seconds / SECONDS_PER_DAY
    return "$days ${context.getString(R.string.platform_widget_time_d)}"
}

@Composable
fun formatPreviewSimpleTimeDuration(seconds: Long): String {
    if (seconds <= 0L) return "0${stringResource(R.string.platform_widget_time_day)}"

    val days = seconds / SECONDS_PER_DAY
    val hours = (seconds % SECONDS_PER_DAY) / SECONDS_PER_HOUR

    val dayUnit = stringResource(R.string.platform_widget_time_day)
    val hourUnit = stringResource(R.string.platform_widget_time_hour)

    return when {
        days > 0 -> "$days$dayUnit $hours$hourUnit"
        else -> "$hours$hourUnit"
    }
}

@Composable
fun formatPreviewDaysOnly(seconds: Long): String {
    if (seconds <= 0L) return "0 ${stringResource(R.string.platform_widget_time_d)}"

    val days = seconds / SECONDS_PER_DAY
    return "$days ${stringResource(R.string.platform_widget_time_d)}"
}
