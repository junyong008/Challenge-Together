package com.yjy.feature.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.AuthRoute
import com.yjy.feature.login.LoginRoute
import com.yjy.feature.login.R

typealias LoginStrings = R.string

fun NavController.navigateToLogin() {
    navigate(AuthRoute.Login) {
        popUpTo(graph.findStartDestination().id) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavGraphBuilder.loginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    onFindPasswordClick: () -> Unit,
    onShowToast: (String) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<AuthRoute.Login>(
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
    ) {
        LoginRoute(
            onLoginSuccess = onLoginSuccess,
            onSignUpClick = onSignUpClick,
            onFindPasswordClick = onFindPasswordClick,
            onShowToast = onShowToast,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
