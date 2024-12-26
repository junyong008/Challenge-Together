package com.yjy.feature.signup.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.yjy.common.core.extensions.sharedViewModel
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideInToRight
import com.yjy.common.core.util.NavigationAnimation.slideOutToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.AuthRoute
import com.yjy.feature.signup.EmailPasswordRoute
import com.yjy.feature.signup.NicknameRoute
import com.yjy.feature.signup.SignUpViewModel

fun NavController.navigateToSignUp() {
    navigate(AuthRoute.SignUp)
}

fun NavController.navigateToSignUpNickname(
    kakaoId: String = "",
    googleId: String = "",
    naverId: String = "",
    guestId: String = "",
) {
    navigate(
        AuthRoute.SignUp.Nickname(
            kakaoId = kakaoId,
            googleId = googleId,
            naverId = naverId,
            guestId = guestId,
        ),
    )
}

fun NavGraphBuilder.signUpNavGraph(
    navController: NavHostController,
    onSignUpSuccess: () -> Unit,
    onShowToast: (String) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    navigation<AuthRoute.SignUp>(
        startDestination = AuthRoute.SignUp.EmailPassword::class,
    ) {
        composable<AuthRoute.SignUp.EmailPassword>(
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

        composable<AuthRoute.SignUp.Nickname>(
            enterTransition = { slideInToLeft() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { slideOutToRight() },
        ) { entry ->
            val viewModel = entry.sharedViewModel<SignUpViewModel>(navController)
            val arguments = entry.toRoute<AuthRoute.SignUp.Nickname>()

            NicknameRoute(
                kakaoId = arguments.kakaoId,
                googleId = arguments.googleId,
                naverId = arguments.naverId,
                guestId = arguments.guestId,
                onBackClick = navController::popBackStack,
                onSignUpSuccess = onSignUpSuccess,
                onShowToast = onShowToast,
                onShowSnackbar = onShowSnackbar,
                viewModel = viewModel,
            )
        }
    }
}
