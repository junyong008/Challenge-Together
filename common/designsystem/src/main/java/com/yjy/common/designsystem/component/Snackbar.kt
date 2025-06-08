package com.yjy.common.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.R
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider

@Composable
fun CustomSnackbarHost(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier,
        snackbar = { snackbarData ->
            val snackbarType = when (snackbarData.visuals.actionLabel) {
                SnackbarType.SUCCESS.name -> SnackbarType.SUCCESS
                SnackbarType.ERROR.name -> SnackbarType.ERROR
                else -> SnackbarType.MESSAGE
            }

            ChallengeTogetherSnackbar(
                snackbarType = snackbarType,
                message = snackbarData.visuals.message,
            )
        },
    )
}

@Composable
fun ChallengeTogetherSnackbar(
    snackbarType: SnackbarType,
    message: String,
) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(color = CustomColorProvider.colorScheme.snackbar.copy(alpha = 0.95f))
            .fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart)
                .padding(horizontal = 20.dp),
        ) {
            when (snackbarType) {
                SnackbarType.SUCCESS -> LottieImage(
                    modifier = Modifier
                        .size(30.dp),
                    animationResId = R.raw.anim_success,
                    repeatCount = 1,
                )
                SnackbarType.ERROR -> LottieImage(
                    modifier = Modifier
                        .size(30.dp),
                    animationResId = R.raw.anim_error,
                    repeatCount = 1,
                )
                SnackbarType.MESSAGE -> Unit
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = message,
                color = CustomColorProvider.colorScheme.onSnackbar,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(vertical = 20.dp),
            )
        }
    }
}

enum class SnackbarType {
    SUCCESS,
    ERROR,
    MESSAGE,
}

@ComponentPreviews
@Composable
fun PreviewCustomSnackbar() {
    ChallengeTogetherTheme {
        ChallengeTogetherSnackbar(
            snackbarType = SnackbarType.MESSAGE,
            message = "커스텀 스낵바",
        )
    }
}
