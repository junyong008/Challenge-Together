package com.yjy.common.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun ChallengeTogetherDialog(
    title: String,
    description: String = "",
    @StringRes positiveTextRes: Int = R.string.common_designsystem_dialog_confirm,
    @StringRes negativeTextRes: Int = R.string.common_designsystem_dialog_cancel,
    positiveTextColor: Color = CustomColorProvider.colorScheme.brand,
    negativeTextColor: Color = CustomColorProvider.colorScheme.onSurfaceMuted,
    onClickPositive: () -> Unit,
    onClickNegative: () -> Unit,
) {
    Dialog(
        onDismissRequest = onClickNegative,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
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
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ClickableText(
                    text = stringResource(id = negativeTextRes),
                    onClick = onClickNegative,
                    color = negativeTextColor,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.None,
                )
                ClickableText(
                    text = stringResource(id = positiveTextRes),
                    onClick = onClickPositive,
                    color = positiveTextColor,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.None,
                )
            }
        }
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
