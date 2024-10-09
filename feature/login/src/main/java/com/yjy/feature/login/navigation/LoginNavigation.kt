package com.yjy.feature.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yjy.core.designsystem.component.SnackbarType
import com.yjy.core.navigation.Route
import com.yjy.feature.login.LoginRoute
import com.yjy.feature.login.R

typealias LoginStrings = R.string

fun NavController.navigateToLogin() {
    navigate(Route.Login) {
        popUpTo(graph.startDestinationId) { inclusive = true }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavGraphBuilder.loginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    onFindPasswordClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<Route.Login> {
        LoginRoute(
            onLoginSuccess = onLoginSuccess,
            onSignUpClick = onSignUpClick,
            onFindPasswordClick = onFindPasswordClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
