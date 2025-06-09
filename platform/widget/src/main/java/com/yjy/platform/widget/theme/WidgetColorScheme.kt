package com.yjy.platform.widget.theme

import androidx.compose.ui.graphics.Color
import androidx.glance.color.ColorProvider
import androidx.glance.unit.ColorProvider
import com.yjy.common.designsystem.theme.WidgetColorProvider
import com.yjy.platform.widget.model.ThemeType

object WidgetColorScheme {

    fun brand(theme: ThemeType, alpha: Float = 1f): ColorProvider =
        resolveColor(
            theme,
            WidgetColorProvider.lightColorScheme.brand,
            WidgetColorProvider.darkColorScheme.brand,
            alpha,
        )

    fun background(theme: ThemeType, alpha: Float = 1f): ColorProvider =
        resolveColor(
            theme,
            WidgetColorProvider.lightColorScheme.background,
            WidgetColorProvider.darkColorScheme.background,
            alpha,
        )

    fun onBackground(theme: ThemeType, alpha: Float = 1f): ColorProvider =
        resolveColor(
            theme,
            WidgetColorProvider.lightColorScheme.onBackground,
            WidgetColorProvider.darkColorScheme.onBackground,
            alpha,
        )

    fun onBackgroundMuted(theme: ThemeType, alpha: Float = 1f): ColorProvider =
        resolveColor(
            theme,
            WidgetColorProvider.lightColorScheme.onBackgroundMuted,
            WidgetColorProvider.darkColorScheme.onBackgroundMuted,
            alpha,
        )

    fun surface(theme: ThemeType, alpha: Float = 1f): ColorProvider =
        resolveColor(
            theme,
            WidgetColorProvider.lightColorScheme.surface,
            WidgetColorProvider.darkColorScheme.surface,
            alpha,
        )

    fun onSurface(theme: ThemeType, alpha: Float = 1f): ColorProvider =
        resolveColor(
            theme,
            WidgetColorProvider.lightColorScheme.onSurface,
            WidgetColorProvider.darkColorScheme.onSurface,
            alpha,
        )

    fun onSurfaceMuted(theme: ThemeType, alpha: Float = 1f): ColorProvider =
        resolveColor(
            theme,
            WidgetColorProvider.lightColorScheme.onSurfaceMuted,
            WidgetColorProvider.darkColorScheme.onSurfaceMuted,
            alpha,
        )

    private fun resolveColor(theme: ThemeType, light: Color, dark: Color, alpha: Float): ColorProvider {
        return when (theme) {
            ThemeType.SYSTEM -> ColorProvider(
                day = light.copy(alpha = alpha),
                night = dark.copy(alpha = alpha),
            )
            ThemeType.LIGHT -> ColorProvider(light.copy(alpha = alpha))
            ThemeType.DARK -> ColorProvider(dark.copy(alpha = alpha))
        }
    }
}
