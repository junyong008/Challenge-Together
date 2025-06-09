package com.yjy.feature.themesetting.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.themesetting.ThemeSettingRoute

fun NavController.navigateToThemeSetting() {
    navigate(ServiceRoute.ThemeSetting)
}

fun NavGraphBuilder.themeSettingScreen(
    onBackClick: () -> Unit,
) {
    composable<ServiceRoute.ThemeSetting>(
        enterTransition = { slideInToLeft() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutToRight() },
    ) {
        ThemeSettingRoute(
            onBackClick = onBackClick,
        )
    }
}
