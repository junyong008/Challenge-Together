package com.yjy.feature.notificationsetting.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.notificationsetting.NotificationSettingRoute

fun NavController.navigateToNotificationSetting() {
    navigate(ServiceRoute.NotificationSetting)
}

fun NavGraphBuilder.notificationSettingScreen(
    onBackClick: () -> Unit,
) {
    composable<ServiceRoute.NotificationSetting>(
        enterTransition = { slideInToLeft() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutToRight() },
    ) {
        NotificationSettingRoute(
            onBackClick = onBackClick,
        )
    }
}
