package com.yjy.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

object CustomMaterialTheme {
    val colorScheme: CustomColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalCustomColorScheme.current

    val typography: CustomTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalCustomTypography.current
}

internal val LocalCustomColorScheme = staticCompositionLocalOf { customLightColorScheme() }
internal val LocalCustomTypography = staticCompositionLocalOf { CustomTypography() }