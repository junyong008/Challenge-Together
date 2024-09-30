package com.yjy.challengetogether.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.yjy.challengetogether.ui.ChallengeTogetherAppState
import com.yjy.core.designsystem.component.SnackbarType
import com.yjy.core.navigation.Route
import com.yjy.feature.login.navigation.loginNavGraph
import com.yjy.feature.signup.navigation.navigateToSignUpEmailPassword
import com.yjy.feature.signup.navigation.signUpNavGraph

@Composable
fun ChallengeTogetherNavHost(
    appState: ChallengeTogetherAppState,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    startDestination: Route = Route.Login,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        loginNavGraph(
            navigateToSignUp = navController::navigateToSignUpEmailPassword,
            onShowSnackbar = onShowSnackbar,
        )
        signUpNavGraph(
            onBackClick = navController::popBackStack,
        )
    }
}
