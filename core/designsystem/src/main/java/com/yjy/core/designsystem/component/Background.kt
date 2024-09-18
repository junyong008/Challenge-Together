package com.yjy.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yjy.core.designsystem.theme.ChallengeTogetherTheme
import com.yjy.core.designsystem.theme.GradientColors
import com.yjy.core.designsystem.theme.LocalBackgroundTheme
import com.yjy.core.designsystem.theme.LocalGradientColors
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ChallengeTogetherBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val color = LocalBackgroundTheme.current.color
    val tonalElevation = LocalBackgroundTheme.current.tonalElevation
    Surface(
        color = if (color == Color.Unspecified) Color.Transparent else color,
        tonalElevation = if (tonalElevation == Dp.Unspecified) 0.dp else tonalElevation,
        modifier = modifier.fillMaxSize(),
    ) {
        CompositionLocalProvider(LocalAbsoluteTonalElevation provides 0.dp) {
            content()
        }
    }
}

@Composable
fun ChallengeTogetherGradientBackground(
    modifier: Modifier = Modifier,
    gradientColors: GradientColors = LocalGradientColors.current,
    content: @Composable () -> Unit,
) {
    val currentTopColor by rememberUpdatedState(gradientColors.top)
    val currentBottomColor by rememberUpdatedState(gradientColors.bottom)

    Surface(
        color = Color.Transparent,
        modifier = modifier.fillMaxSize(),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .drawWithCache {
                    val angle = 225f
                    val radians = Math.toRadians(angle.toDouble()).toFloat()
                    val cosAngle = cos(radians.toDouble()).toFloat()
                    val sinAngle = sin(radians.toDouble()).toFloat()

                    val start = Offset(
                        size.width * 0.5f + (size.width * 0.5f * cosAngle),
                        size.height * 0.5f + (size.height * 0.5f * sinAngle)
                    )
                    val end = Offset(
                        size.width * 0.5f - (size.width * 0.5f * cosAngle),
                        size.height * 0.5f - (size.height * 0.5f * sinAngle)
                    )

                    val gradientBrush = Brush.linearGradient(
                        colors = listOf(currentTopColor, currentBottomColor),
                        start = start,
                        end = end
                    )

                    onDrawBehind {
                        drawRect(gradientBrush)
                    }
                },
        ) {
            content()
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light theme")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark theme")
annotation class ThemePreviews

@ThemePreviews
@Composable
fun BackgroundPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground(Modifier.size(100.dp), content = {})
    }
}

@ThemePreviews
@Composable
fun GradientBackgroundPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherGradientBackground(Modifier.size(100.dp), content = {})
    }
}