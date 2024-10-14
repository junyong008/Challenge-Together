package com.yjy.common.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
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
    gap: Dp = 4.dp,
) {
    val coercedProgress = progress().coerceIn(0f, 1f)

    Canvas(modifier = modifier) {
        val strokeWidth = size.height
        val gapPx = gap.toPx()
        val gapOffset = gapPx / 2

        drawRoundRect(
            color = backgroundColor,
            size = Size(size.width, strokeWidth),
            cornerRadius = CornerRadius(strokeWidth / 2, strokeWidth / 2)
        )
        drawRoundRect(
            color = progressColor,
            size = Size(size.width * coercedProgress - gapPx, strokeWidth - gapPx),
            topLeft = Offset(gapOffset, gapOffset),
            cornerRadius = CornerRadius(strokeWidth / 2, strokeWidth / 2)
        )
    }
}

@ComponentPreviews
@Composable
fun RoundedLinearProgressBarPreview() {
    ChallengeTogetherTheme {
        RoundedLinearProgressBar(
            progress = { 1.0f },
            modifier = Modifier
                .height(20.dp)
                .fillMaxWidth(),
        )
    }
}
