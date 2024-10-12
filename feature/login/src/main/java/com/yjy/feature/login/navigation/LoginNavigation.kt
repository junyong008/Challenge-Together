package com.yjy.feature.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yjy.common.core.ui.NavigationAnimation.fadeIn
import com.yjy.common.core.ui.NavigationAnimation.fadeOut
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.Route
import com.yjy.feature.login.LoginRoute
import com.yjy.feature.login.R

typealias LoginStrings = R.string

fun NavController.navigateToLogin() {
    navigate(Route.Login) {
        popUpTo(graph.findStartDestination().id) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavGraphBuilder.loginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    onFindPasswordClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<Route.Login>(
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
    ) {
        LoginRoute(
            onLoginSuccess = onLoginSuccess,
            onSignUpClick = onSignUpClick,
            onFindPasswordClick = onFindPasswordClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
