package com.yjy.challengetogether.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.yjy.challengetogether.ui.ChallengeTogetherAppState
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.Route
import com.yjy.feature.changepassword.navigation.changePasswordScreen
import com.yjy.feature.changepassword.navigation.navigateToChangePassword
import com.yjy.feature.findpassword.navigation.findPasswordNavGraph
import com.yjy.feature.findpassword.navigation.navigateToFindPassword
import com.yjy.feature.home.navigation.homeScreen
import com.yjy.feature.login.navigation.loginScreen
import com.yjy.feature.login.navigation.navigateToLogin
import com.yjy.feature.signup.navigation.navigateToSignUp
import com.yjy.feature.signup.navigation.signUpNavGraph

@Composable
internal fun ChallengeTogetherNavHost(
    appState: ChallengeTogetherAppState,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    startDestination: Route = Route.Login,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        modifier = modifier,
    ) {
        homeScreen()
        loginScreen(
            onLoginSuccess = { /*appState.navigateToMainTab(MainTab.HOME)*/ },
            onSignUpClick = navController::navigateToSignUp,
            onFindPasswordClick = navController::navigateToFindPassword,
            onShowSnackbar = onShowSnackbar,
        )
        signUpNavGraph(
            navController = navController,
            onSignUpSuccess = { appState.navigateToMainTab(MainTab.HOME) },
            onShowSnackbar = onShowSnackbar,
        )
        findPasswordNavGraph(
            navController = navController,
            onVerifySuccess = {
                val navOptions = navOptions {
                    popUpTo(Route.FindPassword) { inclusive = true }
                    launchSingleTop = true
                }
                navController.navigateToChangePassword(navOptions)
            },
            onShowSnackbar = onShowSnackbar,
        )
        changePasswordScreen(
            onBackClick = navController::popBackStack,
            onPasswordChanged = navController::navigateToLogin,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
