package com.yjy.feature.intro.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.AuthRoute
import com.yjy.feature.intro.IntroRoute

fun NavGraphBuilder.introScreen(
    onLoginSuccess: () -> Unit,
    onStartWithEmailClick: () -> Unit,
    onSignUpWithKakaoClick: (id: String) -> Unit,
    onSignUpWithGoogleClick: (id: String) -> Unit,
    onSignUpWithNaver: (id: String) -> Unit,
    onSignUpWithGuest: (id: String) -> Unit,
    onShowToast: (String) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<AuthRoute.Intro>(
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() },
    ) {
        IntroRoute(
            onLoginSuccess = onLoginSuccess,
            onStartWithEmailClick = onStartWithEmailClick,
            onSignUpWithKakao = onSignUpWithKakaoClick,
            onSignUpWithGoogle = onSignUpWithGoogleClick,
            onSignUpWithNaver = onSignUpWithNaver,
            onSignUpWithGuest = onSignUpWithGuest,
            onShowToast = onShowToast,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
