package com.yjy.navigation.service.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.addchallenge.navigation.addChallengeNavGraph
import com.yjy.feature.changepassword.navigation.changePasswordScreen
import com.yjy.feature.editchallenge.navigation.editChallengeScreen
import com.yjy.feature.editchallenge.navigation.navigateToEditCategory
import com.yjy.feature.editchallenge.navigation.navigateToEditTargetDays
import com.yjy.feature.editchallenge.navigation.navigateToEditTitleDescription
import com.yjy.feature.home.navigation.homeScreen
import com.yjy.feature.startedchallenge.navigation.navigateToStartedChallenge
import com.yjy.feature.startedchallenge.navigation.startedChallengeScreen

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
        homeScreen(
            onStartedChallengeClick = navController::navigateToStartedChallenge,
        )
        addChallengeNavGraph(
            navController = navController,
            onChallengeStarted = { challengeId ->
                val navOptions = navOptions {
                    popUpTo(ServiceRoute.AddChallenge) { inclusive = true }
                    launchSingleTop = true
                }
                navController.navigateToStartedChallenge(challengeId, navOptions)
            },
            onWaitingChallengeCreated = { challengeId ->
                navController.popBackStack(route = ServiceRoute.AddChallenge, inclusive = true)
            },
            onShowSnackbar = onShowSnackbar,
        )
        editChallengeScreen(
            onBackClick = navController::popBackStack,
            onShowSnackbar = onShowSnackbar,
        )
        startedChallengeScreen(
            onBackClick = navController::popBackStack,
            onEditCategoryClick = navController::navigateToEditCategory,
            onEditTitleClick = navController::navigateToEditTitleDescription,
            onEditTargetDaysClick = navController::navigateToEditTargetDays,
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
