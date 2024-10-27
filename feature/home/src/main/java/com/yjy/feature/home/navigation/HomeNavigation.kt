package com.yjy.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.home.HomeRoute
import com.yjy.feature.home.R

typealias HomeStrings = R.string

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    navigate(ServiceRoute.MainTab.Home, navOptions)
}

fun NavGraphBuilder.homeScreen(
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<ServiceRoute.MainTab.Home> {
        HomeRoute(
            onShowSnackbar = onShowSnackbar,
        )
    }
}
