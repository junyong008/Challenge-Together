package com.yjy.feature.login.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yjy.core.designsystem.component.SnackbarType
import com.yjy.feature.login.LoginRoute
import com.yjy.feature.login.R

typealias LoginStrings = R.string
const val LOGIN_ROUTE = "login_route"

fun NavGraphBuilder.loginScreen(
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable(route = LOGIN_ROUTE) {
        LoginRoute(onShowSnackbar = onShowSnackbar)
    }
}
