package com.yjy.navigation.service.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.addchallenge.navigation.addChallengeNavGraph
import com.yjy.feature.changepassword.navigation.changePasswordScreen
import com.yjy.feature.home.navigation.homeScreen

@Composable
internal fun ServiceNavHost(
    navigateToAuth: () -> Unit,
    navController: NavHostController,
    onShowToast: (String) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    startDestination: ServiceRoute = ServiceRoute.MainTab.Home,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        homeScreen()
        addChallengeNavGraph(
            navController = navController,
            onAddChallenge = {
                navController.popBackStack(
                    route = ServiceRoute.AddChallenge,
                    inclusive = true,
                )
            },
            onShowSnackbar = onShowSnackbar,
        )
        changePasswordScreen(
            onBackClick = navController::popBackStack,
            onPasswordChanged = navigateToAuth,
            onShowToast = onShowToast,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
