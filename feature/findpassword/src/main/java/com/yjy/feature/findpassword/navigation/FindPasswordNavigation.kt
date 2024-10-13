package com.yjy.feature.findpassword.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.yjy.common.core.extensions.sharedViewModel
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideInToRight
import com.yjy.common.core.util.NavigationAnimation.slideOutToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.AuthRoute
import com.yjy.feature.findpassword.FindPasswordViewModel
import com.yjy.feature.findpassword.InputEmailRoute
import com.yjy.feature.findpassword.R
import com.yjy.feature.findpassword.VerifyRoute

typealias FindPasswordStrings = R.string

fun NavController.navigateToFindPassword() {
    navigate(AuthRoute.FindPassword)
}

private fun NavController.navigateToVerifyCode() {
    navigate(AuthRoute.FindPassword.VerifyCode)
}

fun NavGraphBuilder.findPasswordNavGraph(
    navController: NavHostController,
    onVerifySuccess: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    navigation<AuthRoute.FindPassword>(
        startDestination = AuthRoute.FindPassword.InputEmail::class,
    ) {
        composable<AuthRoute.FindPassword.InputEmail>(
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

        composable<AuthRoute.FindPassword.VerifyCode>(
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
