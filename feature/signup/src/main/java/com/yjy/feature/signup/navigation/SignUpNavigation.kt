package com.yjy.feature.signup.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.yjy.core.common.extensions.sharedViewModel
import com.yjy.core.common.ui.NavigationAnimation.slideInToLeft
import com.yjy.core.common.ui.NavigationAnimation.slideInToRight
import com.yjy.core.common.ui.NavigationAnimation.slideOutToLeft
import com.yjy.core.common.ui.NavigationAnimation.slideOutToRight
import com.yjy.core.designsystem.component.SnackbarType
import com.yjy.core.navigation.Route
import com.yjy.feature.signup.EmailPasswordRoute
import com.yjy.feature.signup.NicknameRoute
import com.yjy.feature.signup.R
import com.yjy.feature.signup.SignUpViewModel

typealias SignUpStrings = R.string

fun NavController.navigateToSignUp() {
    navigate(Route.SignUp)
}

fun NavController.navigateToSignUpNickname(
    kakaoId: String = "",
    googleId: String = "",
    naverId: String = "",
) {
    navigate(
        Route.SignUp.Nickname(
            kakaoId = kakaoId,
            googleId = googleId,
            naverId = naverId,
        )
    )
}

fun NavGraphBuilder.signUpNavGraph(
    navController: NavHostController,
    onSignUpSuccess: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    navigation<Route.SignUp>(
        startDestination = Route.SignUp.EmailPassword::class,
    ) {
        composable<Route.SignUp.EmailPassword>(
            enterTransition = { slideInToLeft() },
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInToRight() },
            popExitTransition = { slideOutToRight() },
        ) { entry ->
            val viewModel = entry.sharedViewModel<SignUpViewModel>(navController)

            EmailPasswordRoute(
                onBackClick = navController::popBackStack,
                onContinue = navController::navigateToSignUpNickname,
                onShowSnackbar = onShowSnackbar,
                viewModel = viewModel,
            )
        }

        composable<Route.SignUp.Nickname>(
            enterTransition = { slideInToLeft() },
            popExitTransition = { slideOutToRight() },
        ) { entry ->
            val viewModel = entry.sharedViewModel<SignUpViewModel>(navController)

            NicknameRoute(
                onBackClick = navController::popBackStack,
                onSignUpSuccess = onSignUpSuccess,
                onShowSnackbar = onShowSnackbar,
                viewModel = viewModel,
            )
        }
    }
}
