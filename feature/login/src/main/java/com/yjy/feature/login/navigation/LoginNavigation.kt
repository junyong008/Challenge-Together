package com.yjy.feature.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.AuthRoute
import com.yjy.feature.login.LoginRoute

fun NavController.navigateToLogin(navOptions: NavOptions? = null) {
    navigate(AuthRoute.Login, navOptions)
}

fun NavGraphBuilder.loginScreen(
    onBackClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    onFindPasswordClick: () -> Unit,
    onShowToast: (String) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<AuthRoute.Login>(
        enterTransition = { slideInToLeft() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutToRight() },
    ) {
        LoginRoute(
            onBackClick = onBackClick,
            onLoginSuccess = onLoginSuccess,
            onSignUpClick = onSignUpClick,
            onFindPasswordClick = onFindPasswordClick,
            onShowToast = onShowToast,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
