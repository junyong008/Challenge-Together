package com.yjy.feature.changepassword.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.yjy.common.core.ui.NavigationAnimation.slideInToLeft
import com.yjy.common.core.ui.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.Route
import com.yjy.feature.changepassword.ChangePasswordRoute
import com.yjy.feature.changepassword.R

typealias ChangePasswordStrings = R.string

fun NavController.navigateToChangePassword(navOptions: NavOptions? = null) {
    navigate(Route.ChangePassword, navOptions)
}

fun NavGraphBuilder.changePasswordScreen(
    onBackClick: () -> Unit,
    onPasswordChanged: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<Route.ChangePassword>(
        enterTransition = { slideInToLeft() },
        popExitTransition = { slideOutToRight() },
    ) {
        ChangePasswordRoute(
            onBackClick = onBackClick,
            onPasswordChanged = onPasswordChanged,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
