package com.yjy.feature.login.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yjy.core.designsystem.component.SnackbarType
import com.yjy.core.navigation.Route
import com.yjy.feature.login.LoginRoute
import com.yjy.feature.login.R

typealias LoginStrings = R.string

fun NavGraphBuilder.loginScreen(
    onSignUpClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<Route.Login> {
        LoginRoute(
            onSignUpClick = onSignUpClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
