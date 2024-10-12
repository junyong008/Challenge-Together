package com.yjy.feature.findpassword.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.yjy.common.core.extensions.sharedViewModel
import com.yjy.common.core.ui.NavigationAnimation.slideInToLeft
import com.yjy.common.core.ui.NavigationAnimation.slideInToRight
import com.yjy.common.core.ui.NavigationAnimation.slideOutToLeft
import com.yjy.common.core.ui.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.Route
import com.yjy.feature.findpassword.FindPasswordViewModel
import com.yjy.feature.findpassword.InputEmailRoute
import com.yjy.feature.findpassword.R
import com.yjy.feature.findpassword.VerifyRoute

typealias FindPasswordStrings = R.string

fun NavController.navigateToFindPassword() {
    navigate(Route.FindPassword)
}

private fun NavController.navigateToVerifyCode() {
    navigate(Route.FindPassword.VerifyCode)
}

fun NavGraphBuilder.findPasswordNavGraph(
    navController: NavHostController,
    onVerifySuccess: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    navigation<Route.FindPassword>(
        startDestination = Route.FindPassword.InputEmail::class,
    ) {
        composable<Route.FindPassword.InputEmail>(
            enterTransition = { slideInToLeft() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInToRight() },
            popExitTransition = { slideOutToRight() },
        ) { entry ->
            val viewModel = entry.sharedViewModel<FindPasswordViewModel>(navController)

            InputEmailRoute(
                onBackClick = navController::popBackStack,
                onVerifyCodeSent = navController::navigateToVerifyCode,
                onShowSnackbar = onShowSnackbar,
                viewModel = viewModel,
            )
        }

        composable<Route.FindPassword.VerifyCode>(
            enterTransition = { slideInToLeft() },
            exitTransition = { slideOutToLeft() },
            popExitTransition = { slideOutToRight() },
        ) { entry ->
            val viewModel = entry.sharedViewModel<FindPasswordViewModel>(navController)

            VerifyRoute(
                onBackClick = navController::popBackStack,
                onVerifySuccess = onVerifySuccess,
                onShowSnackbar = onShowSnackbar,
                viewModel = viewModel,
            )
        }
    }
}
