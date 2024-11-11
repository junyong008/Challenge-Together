package com.yjy.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.ThemePreviews
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherOutlinedButton
import com.yjy.common.designsystem.component.LottieImage
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider

@Composable
fun ErrorBody(
    onClickRetry: () -> Unit,
    modifier: Modifier = Modifier,
    isRetrying: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .background(CustomColorProvider.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isRetrying) {
                CircularProgressIndicator(
                    color = CustomColorProvider.colorScheme.brand,
                    modifier = Modifier
                        .padding(75.dp)
                        .size(50.dp),
                )
            } else {
                LottieImage(
                    modifier = Modifier.height(200.dp),
                    animationResId = R.raw.anim_error_body,
                    repeatCount = 1,
                )
            }
            Text(
                text = stringResource(id = R.string.common_ui_error_body_title),
                color = CustomColorProvider.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.common_ui_error_body_description),
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            ChallengeTogetherOutlinedButton(
                onClick = onClickRetry,
                enabled = !isRetrying,
                shape = MaterialTheme.shapes.extraLarge,
                borderColor = CustomColorProvider.colorScheme.onBackgroundMuted.copy(alpha = 0.5f),
                contentColor = CustomColorProvider.colorScheme.onBackground,
                content = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            ImageVector.vectorResource(id = ChallengeTogetherIcons.Refresh),
                            contentDescription = stringResource(id = R.string.common_ui_error_body_retry),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.common_ui_error_body_retry),
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center,
                        )
                    }
                },
            )
        }
    }
}

@ThemePreviews
@Composable
fun ErrorBodyPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            ErrorBody(onClickRetry = {})
        }
    }
}
