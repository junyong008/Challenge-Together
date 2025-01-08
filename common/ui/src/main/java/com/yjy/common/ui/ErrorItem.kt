package com.yjy.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.ThemePreviews
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherOutlinedButton
import com.yjy.common.designsystem.component.LottieImage
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider

@Composable
fun ErrorItem(
    onClickRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            LottieImage(
                modifier = Modifier.size(50.dp),
                animationResId = R.raw.anim_error_item,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.common_ui_error_description),
                style = MaterialTheme.typography.labelSmall,
                color = CustomColorProvider.colorScheme.onBackground,
                textAlign = TextAlign.Start,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        ChallengeTogetherOutlinedButton(
            onClick = onClickRetry,
            shape = MaterialTheme.shapes.extraLarge,
            borderColor = CustomColorProvider.colorScheme.onBackgroundMuted.copy(alpha = 0.5f),
            contentColor = CustomColorProvider.colorScheme.onBackground,
            content = {
                Text(
                    text = stringResource(id = R.string.common_ui_error_retry),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                )
            },
        )
    }
}

@ThemePreviews
@Composable
fun ErrorItemPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground(modifier = Modifier.height(80.dp)) {
            ErrorItem(onClickRetry = {})
        }
    }
}
