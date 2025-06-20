package com.yjy.common.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yjy.common.core.constants.TimeConst.HOURS_PER_HALF_DAY
import com.yjy.common.core.constants.TimeConst.MIDNIGHT_HOUR
import com.yjy.common.core.constants.TimeConst.NOON_HOUR
import com.yjy.common.core.util.to12HourFormat
import com.yjy.common.designsystem.R
import com.yjy.common.designsystem.ThemePreviews
import com.yjy.common.designsystem.extensions.getDisplayNameResId
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.model.common.ReportReason
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

private const val DEFAULT_MIN_YEAR = 1900
private const val DEFAULT_MIN_MONTH = 1
private const val DEFAULT_MIN_DAY = 1
private const val DEFAULT_MAX_YEAR = 2100
private const val DEFAULT_MAX_MONTH = 12
private const val DEFAULT_MAX_DAY = 31

@Composable
fun ChallengeTogetherDialog(
    title: String,
    description: String = "",
    onClickPositive: () -> Unit,
    onClickNegative: () -> Unit,
    @StringRes positiveTextRes: Int = R.string.common_designsystem_dialog_confirm,
    @StringRes negativeTextRes: Int = R.string.common_designsystem_dialog_cancel,
    positiveTextColor: Color = CustomColorProvider.colorScheme.brand,
    negativeTextColor: Color = CustomColorProvider.colorScheme.onSurfaceMuted,
) {
    BaseDialog(
        onDismissRequest = onClickNegative,
        title = title,
        description = description,
    ) {
        DialogButtonRow(
            onClickNegative = onClickNegative,
            onClickPositive = onClickPositive,
            positiveTextRes = positiveTextRes,
            negativeTextRes = negativeTextRes,
            positiveTextColor = positiveTextColor,
            negativeTextColor = negativeTextColor,
        )
    }
}

@Composable
fun PremiumDialog(
    onExploreClick: () -> Unit,
    onDismiss: () -> Unit,
    title: String = stringResource(id = R.string.common_designsystem_dialog_premium_feature_title),
    description: String = stringResource(id = R.string.common_designsystem_dialog_premium_feature_message),
) {
    BaseDialog(
        onDismissRequest = onDismiss,
        title = title,
        description = description,
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        StableImage(
            drawableResId = R.drawable.image_fire,
            descriptionResId = R.string.common_designsystem_dialog_premium_feature_title,
            modifier = Modifier.size(130.dp),
        )
        Spacer(modifier = Modifier.height(32.dp))
        ChallengeTogetherButton(
            onClick = onExploreClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(id = R.string.common_designsystem_dialog_go_to_premium),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        ClickableText(
            text = stringResource(id = R.string.common_designsystem_dialog_cancel),
            onClick = onDismiss,
            color = CustomColorProvider.colorScheme.onSurfaceMuted,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
        )
    }
}

@Composable
fun MaintenanceDialog(
    expectedCompletionTime: String,
    onDismiss: () -> Unit,
) {
    BaseDialog(
        onDismissRequest = onDismiss,
        title = stringResource(id = R.string.common_designsystem_dialog_system_maintenance_title),
        description = stringResource(id = R.string.common_designsystem_dialog_system_maintenance_description),
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.extraLarge)
                .background(CustomColorProvider.colorScheme.background)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.common_designsystem_dialog_expected_completion),
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.common_designsystem_dialog_until, expectedCompletionTime),
                color = CustomColorProvider.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        ClickableText(
            text = stringResource(id = R.string.common_designsystem_dialog_exit),
            onClick = onDismiss,
            color = CustomColorProvider.colorScheme.onSurfaceMuted,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
        )
    }
}

@Composable
fun BanDialog(
    banReason: String,
    banEndTime: String,
    onDismiss: () -> Unit,
) {
    BaseDialog(
        onDismissRequest = onDismiss,
        title = stringResource(id = R.string.common_designsystem_dialog_service_restriction_title),
        description = stringResource(
            id = R.string.common_designsystem_dialog_service_restriction_description,
        ),
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .background(CustomColorProvider.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.common_designsystem_dialog_restriction_reason),
                    color = CustomColorProvider.colorScheme.onBackgroundMuted,
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(
                    text = banReason,
                    color = CustomColorProvider.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = CustomColorProvider.colorScheme.divider,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.common_designsystem_dialog_restriction_period),
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.Start),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.common_designsystem_dialog_until, banEndTime),
                color = CustomColorProvider.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End,
                modifier = Modifier.align(Alignment.End),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        ClickableText(
            text = stringResource(id = R.string.common_designsystem_dialog_exit),
            onClick = onDismiss,
            color = CustomColorProvider.colorScheme.onSurfaceMuted,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
        )
    }
}

@Composable
fun PasswordDialog(
    value: String = "",
    onValueChange: (String) -> Unit = {},
    onConfirm: (password: String) -> Unit,
    onClickNegative: () -> Unit,
) {
    BaseDialog(
        onDismissRequest = onClickNegative,
        title = stringResource(id = R.string.common_designsystem_dialog_password_title),
        description = stringResource(id = R.string.common_designsystem_dialog_password_description),
    ) {
        SingleLineTextField(
            value = value,
            onValueChange = onValueChange,
            leadingIcon = {
                Icon(
                    ImageVector.vectorResource(id = ChallengeTogetherIcons.Lock),
                    contentDescription = stringResource(
                        id = R.string.common_designsystem_dialog_password_placeholder,
                    ),
                    tint = CustomColorProvider.colorScheme.onBackground,
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            textColor = CustomColorProvider.colorScheme.onBackground,
            backgroundColor = CustomColorProvider.colorScheme.background,
            placeholderText = stringResource(id = R.string.common_designsystem_dialog_password_placeholder),
            placeholderColor = CustomColorProvider.colorScheme.onBackground.copy(alpha = 0.2f),
            isPassword = true,
        )
        Spacer(modifier = Modifier.size(16.dp))
        DialogButtonRow(
            onClickNegative = onClickNegative,
            onClickPositive = { onConfirm(value) },
        )
    }
}

@Composable
fun TextInputDialog(
    title: String = "",
    description: String = "",
    value: String = "",
    placeholder: String = "",
    maxTextLength: Int = 0,
    enableConfirmButton: Boolean = true,
    onValueChange: (String) -> Unit = {},
    onConfirm: (text: String) -> Unit,
    onClickNegative: () -> Unit,
) {
    BaseDialog(
        onDismissRequest = onClickNegative,
        title = title,
        description = description,
        dismissOnClickOutside = false,
    ) {
        SingleLineTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
            ),
            textColor = CustomColorProvider.colorScheme.onBackground,
            backgroundColor = CustomColorProvider.colorScheme.background,
            placeholderText = placeholder,
            placeholderColor = CustomColorProvider.colorScheme.onBackground.copy(alpha = 0.2f),
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = "${value.length}/$maxTextLength",
            color = CustomColorProvider.colorScheme.onSurfaceMuted,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End,
        )
        Spacer(modifier = Modifier.size(8.dp))
        DialogButtonRow(
            enablePositiveText = enableConfirmButton,
            onClickNegative = onClickNegative,
            onClickPositive = { onConfirm(value) },
        )
    }
}

@Composable
fun ReportDialog(
    onClickReport: (ReportReason) -> Unit,
    onClickNegative: () -> Unit,
) {
    var selectedReason by remember { mutableStateOf(ReportReason.COMMERCIAL_CONTENT) }

    BaseDialog(
        onDismissRequest = onClickNegative,
        title = stringResource(id = R.string.common_designsystem_dialog_report_title),
        description = stringResource(id = R.string.common_designsystem_dialog_report_description),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ReportReason.entries.forEach { reason ->
                ReportItem(
                    title = stringResource(id = reason.getDisplayNameResId()),
                    isSelected = selectedReason == reason,
                    onClick = { selectedReason = reason },
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        DialogButtonRow(
            onClickNegative = onClickNegative,
            onClickPositive = { onClickReport(selectedReason) },
            positiveTextRes = R.string.common_designsystem_dialog_report_title,
        )
    }
}

@Composable
private fun ReportItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.medium
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(CustomColorProvider.colorScheme.background)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = CustomColorProvider.colorScheme.brand,
                        shape = shape,
                    )
                } else {
                    Modifier
                },
            )
            .clickable(
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = CustomColorProvider.colorScheme.onBackground,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(1f),
        )
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = CustomColorProvider.colorScheme.brand,
                unselectedColor = CustomColorProvider.colorScheme.onSurfaceMuted,
            ),
        )
    }
}

@Composable
fun CalendarDialog(
    initialDateTime: LocalDateTime,
    outRangeText: String,
    onDismissRequest: () -> Unit,
    onConfirmEdit: (LocalDateTime) -> Unit,
    minDateTime: LocalDateTime,
    maxDateTime: LocalDateTime,
) {
    var selectedDateTime by rememberSaveable { mutableStateOf(initialDateTime) }
    var isOutRanged by remember { mutableStateOf(false) }
    val selectedDate = selectedDateTime.toLocalDate()
    val (hour, minute, isAm) = selectedDateTime.to12HourFormat()

    BaseDialog(
        onDismissRequest = onDismissRequest,
        title = stringResource(id = R.string.common_designsystem_dialog_calendar_title),
        description = stringResource(id = R.string.common_designsystem_dialog_calendar_description),
    ) {
        Calendar(
            selectionMode = SelectionMode.SingleDate(selectedDate),
            onDateSelected = {
                val newDateTime = LocalDateTime.of(it, selectedDateTime.toLocalTime())
                selectedDateTime = when {
                    isSameUpToMinute(minDateTime, maxDateTime) -> initialDateTime
                    newDateTime > maxDateTime -> maxDateTime
                    newDateTime <= minDateTime -> minDateTime.plusMinutes(1).withSecond(0)
                    else -> newDateTime
                }
            },
            minDate = minDateTime.toLocalDate(),
            maxDate = maxDateTime.toLocalDate(),
            showAdjacentMonthsDays = false,
            enableWeekModeOnDataSelected = true,
            calendarColors = CalendarColors(
                containerColor = CustomColorProvider.colorScheme.background,
                contentColor = CustomColorProvider.colorScheme.onBackground,
                weekDayColor = CustomColorProvider.colorScheme.onBackgroundMuted,
                selectedBackgroundColor = CustomColorProvider.colorScheme.brand,
                selectedTextColor = CustomColorProvider.colorScheme.onBrand,
                rangeBackgroundColor = CustomColorProvider.colorScheme.brandBright,
                rangeTextColor = CustomColorProvider.colorScheme.onBrandBright,
                disabledColor = CustomColorProvider.colorScheme.disable,
                dividerColor = CustomColorProvider.colorScheme.divider,
                todayIndicatorColor = CustomColorProvider.colorScheme.brandDim,
            ),
        )
        Spacer(modifier = Modifier.size(8.dp))
        TimePicker(
            hour = hour,
            minute = minute,
            isAm = isAm,
            onTimeChanged = { newHour, newMinute, newAmPm ->
                val hour24Format = convertTo24HourFormat(newHour, newAmPm)
                val newDateTime = selectedDate.atTime(hour24Format, newMinute)
                isOutRanged = newDateTime > maxDateTime || newDateTime <= minDateTime
                selectedDateTime = when {
                    isSameUpToMinute(minDateTime, maxDateTime) -> initialDateTime
                    newDateTime > maxDateTime -> maxDateTime
                    newDateTime <= minDateTime -> minDateTime.plusMinutes(1).withSecond(0)
                    else -> newDateTime
                }
            },
            containerColor = CustomColorProvider.colorScheme.background,
            contentColor = CustomColorProvider.colorScheme.onBackground,
            textBackground = CustomColorProvider.colorScheme.surface,
            textColor = CustomColorProvider.colorScheme.onSurface,
        )
        if (isOutRanged) {
            ErrorIndicator(
                text = outRangeText,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        DialogButtonRow(
            positiveTextRes = R.string.common_designsystem_dialog_calendar_edit,
            onClickNegative = onDismissRequest,
            onClickPositive = { onConfirmEdit(selectedDateTime) },
        )
    }
}

@Composable
fun YearMonthPickerDialog(
    initialYear: Int,
    initialMonth: Int,
    onDismissRequest: () -> Unit,
    onConfirm: (Int, Int) -> Unit,
    minDate: LocalDate = LocalDate.of(DEFAULT_MIN_YEAR, DEFAULT_MIN_MONTH, DEFAULT_MIN_DAY),
    maxDate: LocalDate = LocalDate.of(DEFAULT_MAX_YEAR, DEFAULT_MAX_MONTH, DEFAULT_MAX_DAY),
) {
    var year by remember { mutableIntStateOf(initialYear) }
    var month by remember { mutableIntStateOf(initialMonth) }

    val correctedDate = YearMonth.of(year, month).coerceIn(
        YearMonth.from(minDate),
        YearMonth.from(maxDate),
    )

    val correctedYear = correctedDate.year
    val correctedMonth = correctedDate.monthValue

    BaseDialog(
        onDismissRequest = onDismissRequest,
        title = stringResource(id = R.string.common_designsystem_dialog_year_month_picker_title),
        description = stringResource(id = R.string.common_designsystem_dialog_year_month_picker_description),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            NumberInputField(
                value = year,
                onValueChange = { year = it },
                minLimit = 0,
                maxLimit = maxDate.year,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(8.dp))
            NumberInputField(
                value = month,
                onValueChange = { month = it },
                minLimit = 1,
                maxLimit = 12,
                modifier = Modifier.width(100.dp),
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        DialogButtonRow(
            onClickNegative = onDismissRequest,
            onClickPositive = { onConfirm(correctedYear, correctedMonth) },
        )
    }
}

@Composable
private fun BaseDialog(
    onDismissRequest: () -> Unit,
    title: String,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    description: String = "",
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside,
        ),
    ) {
        Column(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .background(CustomColorProvider.colorScheme.surface)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                color = CustomColorProvider.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            if (description.isNotBlank()) {
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = description,
                    color = CustomColorProvider.colorScheme.onSurfaceMuted,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.size(8.dp))
            }
            Spacer(modifier = Modifier.size(16.dp))
            content()
        }
    }
}

@Composable
private fun NumberInputField(
    value: Int,
    onValueChange: (Int) -> Unit,
    minLimit: Int,
    maxLimit: Int,
    modifier: Modifier = Modifier,
) {
    CursorLessNumberTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.displaySmall,
        textBackground = CustomColorProvider.colorScheme.background,
        textColor = CustomColorProvider.colorScheme.onBackground,
        minLimit = minLimit,
        maxLimit = maxLimit,
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
    )
}

@Composable
private fun DialogButtonRow(
    onClickNegative: () -> Unit,
    onClickPositive: () -> Unit,
    enablePositiveText: Boolean = true,
    enableNegativeText: Boolean = true,
    @StringRes positiveTextRes: Int = R.string.common_designsystem_dialog_confirm,
    @StringRes negativeTextRes: Int = R.string.common_designsystem_dialog_cancel,
    positiveTextColor: Color = CustomColorProvider.colorScheme.brandDim,
    negativeTextColor: Color = CustomColorProvider.colorScheme.onSurfaceMuted,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ClickableText(
            text = stringResource(id = negativeTextRes),
            onClick = onClickNegative,
            color = negativeTextColor,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            enabled = enableNegativeText,
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
        )
        ClickableText(
            text = stringResource(id = positiveTextRes),
            onClick = onClickPositive,
            color = positiveTextColor,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            enabled = enablePositiveText,
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
        )
    }
}

private fun convertTo24HourFormat(hour: Int, isAm: Boolean): Int = when {
    hour == HOURS_PER_HALF_DAY && isAm -> MIDNIGHT_HOUR
    hour == HOURS_PER_HALF_DAY && !isAm -> NOON_HOUR
    isAm -> hour
    else -> hour + HOURS_PER_HALF_DAY
}

private fun isSameUpToMinute(dt1: LocalDateTime, dt2: LocalDateTime): Boolean {
    return dt1.withSecond(0).withNano(0) == dt2.withSecond(0).withNano(0)
}

@ThemePreviews
@Composable
fun ChallengeTogetherDialogPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            ChallengeTogetherDialog(
                title = "Title",
                description = "Description",
                onClickPositive = {},
                onClickNegative = {},
            )
        }
    }
}

@ThemePreviews
@Composable
fun PremiumDialogPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            PremiumDialog(
                onExploreClick = {},
                onDismiss = {},
            )
        }
    }
}

@ThemePreviews
@Composable
fun PasswordDialogPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            PasswordDialog(
                onConfirm = {},
                onClickNegative = {},
            )
        }
    }
}

@ThemePreviews
@Composable
fun TextInputDialogPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            TextInputDialog(
                onConfirm = {},
                onClickNegative = {},
                maxTextLength = 100,
            )
        }
    }
}

@ThemePreviews
@Composable
fun ReportDialogPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            ReportDialog(
                onClickReport = {},
                onClickNegative = {},
            )
        }
    }
}

@ThemePreviews
@Composable
fun CalendarDialogPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            CalendarDialog(
                initialDateTime = LocalDateTime.now(),
                outRangeText = "Out Of Range",
                onDismissRequest = {},
                onConfirmEdit = {},
                minDateTime = LocalDateTime.now(),
                maxDateTime = LocalDateTime.now(),
            )
        }
    }
}

@ThemePreviews
@Composable
fun YearMonthPickerDialogPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            YearMonthPickerDialog(
                initialMonth = 10,
                initialYear = 2024,
                onDismissRequest = {},
                onConfirm = { _, _ -> },
            )
        }
    }
}
