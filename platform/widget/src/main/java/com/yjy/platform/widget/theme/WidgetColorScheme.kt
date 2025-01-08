package com.yjy.platform.widget.theme

import androidx.glance.color.ColorProvider
import com.yjy.common.designsystem.theme.WidgetColorProvider

object WidgetColorScheme {
    fun brand(alpha: Float = 1f) = ColorProvider(
        day = WidgetColorProvider.lightColorScheme.brand.copy(alpha = alpha),
        night = WidgetColorProvider.darkColorScheme.brand.copy(alpha = alpha),
    )

    fun background(alpha: Float = 1f) = ColorProvider(
        day = WidgetColorProvider.lightColorScheme.background.copy(alpha = alpha),
        night = WidgetColorProvider.darkColorScheme.background.copy(alpha = alpha),
    )

    fun onBackground(alpha: Float = 1f) = ColorProvider(
        day = WidgetColorProvider.lightColorScheme.onBackground.copy(alpha = alpha),
        night = WidgetColorProvider.darkColorScheme.onBackground.copy(alpha = alpha),
    )

    fun onBackgroundMuted(alpha: Float = 1f) = ColorProvider(
        day = WidgetColorProvider.lightColorScheme.onBackgroundMuted.copy(alpha = alpha),
        night = WidgetColorProvider.darkColorScheme.onBackgroundMuted.copy(alpha = alpha),
    )

    fun surface(alpha: Float = 1f) = ColorProvider(
        day = WidgetColorProvider.lightColorScheme.surface.copy(alpha = alpha),
        night = WidgetColorProvider.darkColorScheme.surface.copy(alpha = alpha),
    )

    fun onSurface(alpha: Float = 1f) = ColorProvider(
        day = WidgetColorProvider.lightColorScheme.onSurface.copy(alpha = alpha),
        night = WidgetColorProvider.darkColorScheme.onSurface.copy(alpha = alpha),
    )

    fun onSurfaceMuted(alpha: Float = 1f) = ColorProvider(
        day = WidgetColorProvider.lightColorScheme.onSurfaceMuted.copy(alpha = alpha),
        night = WidgetColorProvider.darkColorScheme.onSurfaceMuted.copy(alpha = alpha),
    )
}
