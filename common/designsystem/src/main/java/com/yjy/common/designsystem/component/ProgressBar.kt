package com.yjy.common.designsystem.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider

@Composable
fun RoundedLinearProgressBar(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    backgroundColor: Color = CustomColorProvider.colorScheme.divider,
    progressColor: Color = CustomColorProvider.colorScheme.brand,
    disableProgressColor: Color = CustomColorProvider.colorScheme.disable,
    enabled: Boolean = true,
    gap: Dp = 4.dp,
) {
    val targetProgress = progress().coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing,
        ),
        label = "Progress Animation"
    )

    Canvas(modifier = modifier) {
        val strokeWidth = size.height
        val gapPx = gap.toPx()
        val gapOffset = gapPx / 2

        drawRoundRect(
            color = backgroundColor,
            size = Size(size.width, strokeWidth),
            cornerRadius = CornerRadius(strokeWidth / 2, strokeWidth / 2)
        )

        if (animatedProgress > 0f) {
            val progressWidth = (size.width * animatedProgress - gapPx).coerceAtLeast(0f)
            drawRoundRect(
                color = if (enabled) progressColor else disableProgressColor,
                size = Size(progressWidth, strokeWidth - gapPx),
                topLeft = Offset(gapOffset, gapOffset),
                cornerRadius = CornerRadius(strokeWidth / 2, strokeWidth / 2)
            )
        }
    }
}

@ComponentPreviews
@Composable
fun RoundedLinearProgressBarPreview() {
    ChallengeTogetherTheme {
        RoundedLinearProgressBar(
            progress = { 0.5f },
            modifier = Modifier
                .height(20.dp)
                .fillMaxWidth(),
        )
    }
}
