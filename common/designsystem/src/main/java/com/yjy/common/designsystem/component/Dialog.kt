package com.yjy.common.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yjy.common.designsystem.R
import com.yjy.common.designsystem.ThemePreviews
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun ChallengeTogetherDialog(
    title: String,
    description: String = "",
    onClickPositive: () -> Unit,
    onClickNegative: () -> Unit,
    @StringRes positiveTextRes: Int = R.string.common_designsystem_dialog_confirm,
    @StringRes negativeTextRes: Int = R.string.common_designsystem_dialog_cancel,
    positiveTextColor: Color = CustomColorProvider.colorScheme.brand,
    negativeTextColor: Color = CustomColorProvider.colorScheme.onBackgroundMuted,
) {
    BaseDialog(
        onDismissRequest = onClickNegative,
        title = title,
        description = description
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
fun YearMonthPickerDialog(
    initialYear: Int,
    initialMonth: Int,
    onDismissRequest: () -> Unit,
    onConfirm: (Int, Int) -> Unit,
    minDate: LocalDate = LocalDate.of(1900, 1, 1),
    maxDate: LocalDate = LocalDate.of(2100, 12, 31),
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
                modifier = Modifier.weight(1f)
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
    description: String = "",
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Column(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .background(CustomColorProvider.colorScheme.background)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = CustomColorProvider.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            if (description.isNotBlank()) {
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = description,
                    color = CustomColorProvider.colorScheme.onBackgroundMuted,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
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
    modifier: Modifier = Modifier
) {
    CursorLessNumberTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.displaySmall,
        minLimit = minLimit,
        maxLimit = maxLimit,
        modifier = modifier.height(80.dp),
    )
}

@Composable
private fun DialogButtonRow(
    onClickNegative: () -> Unit,
    onClickPositive: () -> Unit,
    @StringRes positiveTextRes: Int = R.string.common_designsystem_dialog_confirm,
    @StringRes negativeTextRes: Int = R.string.common_designsystem_dialog_cancel,
    positiveTextColor: Color = CustomColorProvider.colorScheme.brand,
    negativeTextColor: Color = CustomColorProvider.colorScheme.onBackgroundMuted,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ClickableText(
            text = stringResource(id = negativeTextRes),
            onClick = onClickNegative,
            color = negativeTextColor,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None
        )
        ClickableText(
            text = stringResource(id = positiveTextRes),
            onClick = onClickPositive,
            color = positiveTextColor,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None
        )
    }
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
