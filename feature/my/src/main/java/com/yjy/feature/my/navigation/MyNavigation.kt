package com.yjy.feature.my.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.my.MyRoute
import com.yjy.feature.my.R

typealias MyStrings = R.string

fun NavController.navigateToMy(navOptions: NavOptions? = null) {
    navigate(ServiceRoute.MainTab.My, navOptions)
}

fun NavGraphBuilder.myScreen(
    onNotificationSettingClick: () -> Unit,
    onAppLockSettingClick: () -> Unit,
    onChangeNicknameClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<ServiceRoute.MainTab.My>(
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() },
    ) {
        MyRoute(
            onNotificationSettingClick = onNotificationSettingClick,
            onAppLockSettingClick = onAppLockSettingClick,
            onChangeNicknameClick = onChangeNicknameClick,
            onChangePasswordClick = onChangePasswordClick,
            onDeleteAccountClick = onDeleteAccountClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
