package com.yjy.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

private object CustomColorLightTokens {
    val brand = brandLight
    val brandBright = brandBrightLight
    val background = backgroundLight
    val onBackground = onBackgroundLight
    val surface = surfaceLight
    val surfaceDim = surfaceDimLight
    val constantBlack = black
    val kakaoBackground = kakaoBrand
    val googleBackground = googleBrand
    val naverBackground = naverBrand
}

private object CustomColorDarkTokens {
    val brand = brandDark
    val brandBright = brandBrightDark
    val background = backgroundDark
    val onBackground = onBackgroundDark
    val surface = surfaceDark
    val surfaceDim = surfaceDimDark
    val constantBlack = black
    val kakaoBackground = kakaoBrand
    val googleBackground = googleBrand
    val naverBackground = naverBrand
}

@Immutable
data class CustomColorScheme(
    val brand: Color,
    val brandBright: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val surfaceDim: Color,
    val constantBlack: Color,
    val kakaoBackground: Color,
    val googleBackground: Color,
    val naverBackground: Color,
)

internal fun customLightColorScheme(
    brand: Color = CustomColorLightTokens.brand,
    brandBright: Color = CustomColorLightTokens.brandBright,
    background: Color = CustomColorLightTokens.background,
    onBackground: Color = CustomColorLightTokens.onBackground,
    surface: Color = CustomColorLightTokens.surface,
    surfaceDim: Color = CustomColorLightTokens.surfaceDim,
    constantBlack: Color = CustomColorLightTokens.constantBlack,
    kakaoBackground: Color = CustomColorLightTokens.kakaoBackground,
    googleBackground: Color = CustomColorLightTokens.googleBackground,
    naverBackground: Color = CustomColorLightTokens.naverBackground,
) = CustomColorScheme(
    brand = brand,
    brandBright = brandBright,
    background = background,
    onBackground = onBackground,
    surface = surface,
    surfaceDim = surfaceDim,
    constantBlack = constantBlack,
    kakaoBackground = kakaoBackground,
    googleBackground = googleBackground,
    naverBackground = naverBackground,
)

internal fun customDarkColorScheme(
    brand: Color = CustomColorDarkTokens.brand,
    brandBright: Color = CustomColorDarkTokens.brandBright,
    background: Color = CustomColorDarkTokens.background,
    onBackground: Color = CustomColorDarkTokens.onBackground,
    surface: Color = CustomColorDarkTokens.surface,
    surfaceDim: Color = CustomColorDarkTokens.surfaceDim,
    constantBlack: Color = CustomColorDarkTokens.constantBlack,
    kakaoBackground: Color = CustomColorDarkTokens.kakaoBackground,
    googleBackground: Color = CustomColorDarkTokens.googleBackground,
    naverBackground: Color = CustomColorDarkTokens.naverBackground,
) = CustomColorScheme(
    brand = brand,
    brandBright = brandBright,
    background = background,
    onBackground = onBackground,
    surface = surface,
    surfaceDim = surfaceDim,
    constantBlack = constantBlack,
    kakaoBackground = kakaoBackground,
    googleBackground = googleBackground,
    naverBackground = naverBackground,
)