package com.yjy.common.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

object CustomColorProvider {
    val colorScheme: CustomColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalCustomColorScheme.current
}

internal val LocalCustomColorScheme = staticCompositionLocalOf { customLightColorScheme() }

@Immutable
data class CustomColorScheme(
    val brand: Color,
    val onBrand: Color,
    val brandBright: Color,
    val onBrandBright: Color,
    val brandDim: Color,
    val onBrandDim: Color,
    val background: Color,
    val onBackground: Color,
    val onBackgroundMuted: Color,
    val surface: Color,
    val onSurface: Color,
    val onSurfaceMuted: Color,
    val divider: Color,
    val disable: Color,
    val onDisable: Color,
    val red: Color,
    val snackbar: Color,
    val onSnackbar: Color,
    val kakaoBackground: Color,
    val googleBackground: Color,
    val naverBackground: Color,
)

private object CustomColorLightTokens {
    val brand = brandLight
    val onBrand = onBrandLight
    val brandBright = brandBrightLight
    val onBrandBright = onBrandBrightLight
    val brandDim = brandDimLight
    val onBrandDim = onBrandDimLight
    val background = backgroundLight
    val onBackground = onBackgroundLight
    val onBackgroundMuted = onBackgroundMutedLight
    val surface = surfaceLight
    val onSurface = onSurfaceLight
    val onSurfaceMuted = onSurfaceMutedLight
    val divider = dividerLight
    val disable = disableLight
    val onDisable = onDisableLight
    val red = redLight
    val snackbar = snackbarLight
    val onSnackbar = onSnackbarLight
    val kakaoBackground = kakaoBrand
    val googleBackground = googleBrand
    val naverBackground = naverBrand
}

private object CustomColorDarkTokens {
    val brand = brandDark
    val onBrand = onBrandDark
    val brandBright = brandBrightDark
    val onBrandBright = onBrandBrightDark
    val brandDim = brandDimDark
    val onBrandDim = onBrandDimDark
    val background = backgroundDark
    val onBackground = onBackgroundDark
    val onBackgroundMuted = onBackgroundMutedDark
    val surface = surfaceDark
    val onSurface = onSurfaceDark
    val onSurfaceMuted = onSurfaceMutedDark
    val divider = dividerDark
    val disable = disableDark
    val onDisable = onDisableDark
    val red = redDark
    val snackbar = snackbarDark
    val onSnackbar = onSnackbarDark
    val kakaoBackground = kakaoBrand
    val googleBackground = googleBrand
    val naverBackground = naverBrand
}

internal fun customLightColorScheme(
    brand: Color = CustomColorLightTokens.brand,
    onBrand: Color = CustomColorLightTokens.onBrand,
    brandBright: Color = CustomColorLightTokens.brandBright,
    onBrandBright: Color = CustomColorLightTokens.onBrandBright,
    brandDim: Color = CustomColorLightTokens.brandDim,
    onBrandDim: Color = CustomColorLightTokens.onBrandDim,
    background: Color = CustomColorLightTokens.background,
    onBackground: Color = CustomColorLightTokens.onBackground,
    onBackgroundMuted: Color = CustomColorLightTokens.onBackgroundMuted,
    surface: Color = CustomColorLightTokens.surface,
    onSurface: Color = CustomColorLightTokens.onSurface,
    onSurfaceMuted: Color = CustomColorLightTokens.onSurfaceMuted,
    divider: Color = CustomColorLightTokens.divider,
    disable: Color = CustomColorLightTokens.disable,
    onDisable: Color = CustomColorLightTokens.onDisable,
    red: Color = CustomColorLightTokens.red,
    snackbar: Color = CustomColorLightTokens.snackbar,
    onSnackbar: Color = CustomColorLightTokens.onSnackbar,
    kakaoBackground: Color = CustomColorLightTokens.kakaoBackground,
    googleBackground: Color = CustomColorLightTokens.googleBackground,
    naverBackground: Color = CustomColorLightTokens.naverBackground,
) = CustomColorScheme(
    brand = brand,
    onBrand = onBrand,
    brandBright = brandBright,
    onBrandBright = onBrandBright,
    brandDim = brandDim,
    onBrandDim = onBrandDim,
    background = background,
    onBackground = onBackground,
    onBackgroundMuted = onBackgroundMuted,
    surface = surface,
    onSurface = onSurface,
    onSurfaceMuted = onSurfaceMuted,
    divider = divider,
    disable = disable,
    onDisable = onDisable,
    red = red,
    snackbar = snackbar,
    onSnackbar = onSnackbar,
    kakaoBackground = kakaoBackground,
    googleBackground = googleBackground,
    naverBackground = naverBackground,
)

internal fun customDarkColorScheme(
    brand: Color = CustomColorDarkTokens.brand,
    onBrand: Color = CustomColorDarkTokens.onBrand,
    brandBright: Color = CustomColorDarkTokens.brandBright,
    onBrandBright: Color = CustomColorDarkTokens.onBrandBright,
    brandDim: Color = CustomColorDarkTokens.brandDim,
    onBrandDim: Color = CustomColorDarkTokens.onBrandDim,
    background: Color = CustomColorDarkTokens.background,
    onBackground: Color = CustomColorDarkTokens.onBackground,
    onBackgroundMuted: Color = CustomColorDarkTokens.onBackgroundMuted,
    surface: Color = CustomColorDarkTokens.surface,
    onSurface: Color = CustomColorDarkTokens.onSurface,
    onSurfaceMuted: Color = CustomColorDarkTokens.onSurfaceMuted,
    divider: Color = CustomColorDarkTokens.divider,
    disable: Color = CustomColorDarkTokens.disable,
    onDisable: Color = CustomColorDarkTokens.onDisable,
    red: Color = CustomColorDarkTokens.red,
    snackbar: Color = CustomColorDarkTokens.snackbar,
    onSnackbar: Color = CustomColorDarkTokens.onSnackbar,
    kakaoBackground: Color = CustomColorDarkTokens.kakaoBackground,
    googleBackground: Color = CustomColorDarkTokens.googleBackground,
    naverBackground: Color = CustomColorDarkTokens.naverBackground,
) = CustomColorScheme(
    brand = brand,
    onBrand = onBrand,
    brandBright = brandBright,
    onBrandBright = onBrandBright,
    brandDim = brandDim,
    onBrandDim = onBrandDim,
    background = background,
    onBackground = onBackground,
    onBackgroundMuted = onBackgroundMuted,
    surface = surface,
    onSurface = onSurface,
    onSurfaceMuted = onSurfaceMuted,
    divider = divider,
    disable = disable,
    onDisable = onDisable,
    red = red,
    snackbar = snackbar,
    onSnackbar = onSnackbar,
    kakaoBackground = kakaoBackground,
    googleBackground = googleBackground,
    naverBackground = naverBackground,
)
