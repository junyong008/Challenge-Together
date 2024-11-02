package com.yjy.common.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yjy.common.core.constants.TimeConst.HOURS_PER_HALF_DAY
import com.yjy.common.core.constants.TimeConst.MINUTES_PER_HOUR
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.R
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider

/**
 * [TimePicker]
 *
 * - 챌린지 생성 시 시작 날짜의 시간 선택을 위한 시간 선택기.
 * - 12시간 형식을 사용 하며, 사용시 적절한 변환을 거쳐야 함.
 */
@Composable
fun TimePicker(
    hour: Int,
    minute: Int,
    isAm: Boolean,
    onTimeChanged: (Int, Int, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = CustomColorProvider.colorScheme.surface,
    contentColor: Color = CustomColorProvider.colorScheme.onSurface,
    textBackground: Color = CustomColorProvider.colorScheme.background,
    textColor: Color = CustomColorProvider.colorScheme.onBackground,
) {
    fun adjustHour(increment: Int) {
        val newHour = (hour + increment + HOURS_PER_HALF_DAY) % HOURS_PER_HALF_DAY
        onTimeChanged(newHour, minute, isAm)
    }

    fun adjustMinute(increment: Int) {
        val newMinute = (minute + increment + MINUTES_PER_HOUR) % MINUTES_PER_HOUR
        onTimeChanged(hour, newMinute, isAm)
    }

    fun toggleAmPm() {
        onTimeChanged(hour, minute, !isAm)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(containerColor),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TimeAdjustmentSection(
            hour = hour,
            minute = minute,
            isAm = isAm,
            adjustHour = { adjustHour(it) },
            adjustMinute = { adjustMinute(it) },
            onTimeChanged = onTimeChanged,
            contentColor = contentColor,
            textBackground = textBackground,
            textColor = textColor,
            modifier = Modifier.weight(1f),
        )
        AmPmToggle(
            isAm = isAm,
            toggleAmPm = { toggleAmPm() },
            contentColor = contentColor,
        )
    }
}

@Composable
private fun TimeAdjustmentSection(
    hour: Int,
    minute: Int,
    isAm: Boolean,
    adjustHour: (Int) -> Unit,
    adjustMinute: (Int) -> Unit,
    onTimeChanged: (Int, Int, Boolean) -> Unit,
    contentColor: Color,
    textBackground: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    val timeTextStyle = MaterialTheme.typography.displaySmall

    Box(
        modifier = modifier.padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TimePickerButton(
                value = hour,
                onValueChange = { newHour -> onTimeChanged(newHour, minute, isAm) },
                onIncrease = { adjustHour(1) },
                onDecrease = { adjustHour(-1) },
                contentColor = contentColor,
                textBackground = textBackground,
                textColor = textColor,
                maxLimit = 12,
                descriptionIncrease = stringResource(id = R.string.common_designsystem_time_picker_increase_hour),
                descriptionDecrease = stringResource(id = R.string.common_designsystem_time_picker_decrease_hour),
            )
            Text(
                text = ":",
                color = contentColor,
                style = timeTextStyle,
                modifier = Modifier.padding(bottom = 5.dp, start = 16.dp, end = 16.dp),
            )
            TimePickerButton(
                value = minute,
                onValueChange = { newMinute -> onTimeChanged(hour, newMinute, isAm) },
                onIncrease = { adjustMinute(1) },
                onDecrease = { adjustMinute(-1) },
                contentColor = contentColor,
                textBackground = textBackground,
                textColor = textColor,
                maxLimit = 59,
                descriptionIncrease = stringResource(id = R.string.common_designsystem_time_picker_increase_minute),
                descriptionDecrease = stringResource(id = R.string.common_designsystem_time_picker_decrease_minute),
            )
        }
    }
}

@Composable
private fun TimePickerButton(
    value: Int,
    onValueChange: (Int) -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    contentColor: Color,
    textBackground: Color,
    textColor: Color,
    descriptionIncrease: String,
    descriptionDecrease: String,
    maxLimit: Int,
    modifier: Modifier = Modifier,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        IconButton(onClick = { onIncrease() }) {
            Icon(
                painter = painterResource(id = ChallengeTogetherIcons.ArrowUp),
                contentDescription = descriptionIncrease,
                tint = contentColor,
            )
        }
        CursorLessNumberTextField(
            value = value,
            onValueChange = onValueChange,
            textBackground = textBackground,
            textColor = textColor,
            maxLimit = maxLimit,
            modifier = Modifier.size(width = 65.dp, height = 55.dp),
        )
        IconButton(onClick = { onDecrease() }) {
            Icon(
                painter = painterResource(id = ChallengeTogetherIcons.ArrowDown),
                contentDescription = descriptionDecrease,
                tint = contentColor,
            )
        }
    }
}

@Composable
private fun AmPmToggle(
    isAm: Boolean,
    toggleAmPm: () -> Unit,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    val amPmTextStyle = MaterialTheme.typography.labelLarge

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(end = 16.dp),
    ) {
        Text(
            text = stringResource(id = R.string.common_designsystem_time_picker_am),
            color = if (isAm) contentColor else contentColor.copy(alpha = 0.3f),
            style = amPmTextStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .clickable { if (!isAm) toggleAmPm() }
                .padding(8.dp),
        )
        Text(
            text = stringResource(id = R.string.common_designsystem_time_picker_pm),
            color = if (!isAm) contentColor else contentColor.copy(alpha = 0.3f),
            style = amPmTextStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .clickable { if (isAm) toggleAmPm() }
                .padding(8.dp),
        )
    }
}

@ComponentPreviews
@Composable
fun TimePickerPreview() {
    ChallengeTogetherTheme {
        TimePicker(
            hour = 12,
            minute = 30,
            isAm = true,
            onTimeChanged = { _, _, _ -> },
        )
    }
}
