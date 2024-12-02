package com.yjy.feature.notification.navigation

import androidx.compose.animation.fadeIn
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.notification.NotificationRoute

fun NavController.navigateToNotification() {
    navigate(ServiceRoute.Notification)
}

fun NavGraphBuilder.notificationScreen(
    onBackClick: () -> Unit,
    onSettingClick: () -> Unit,
    onStartedChallengeNotificationClick: (Int) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<ServiceRoute.Notification>(
        enterTransition = { slideInToLeft() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutToRight() },
    ) {
        NotificationRoute(
            onBackClick = onBackClick,
            onSettingClick = onSettingClick,
            onShowSnackbar = onShowSnackbar,
            onStartedChallengeNotificationClick = onStartedChallengeNotificationClick,
        )
    }
}
