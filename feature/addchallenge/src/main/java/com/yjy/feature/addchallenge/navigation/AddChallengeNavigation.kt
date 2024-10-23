package com.yjy.feature.addchallenge.navigation

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
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.addchallenge.AddChallengeViewModel
import com.yjy.feature.addchallenge.ConfirmRoute
import com.yjy.feature.addchallenge.R
import com.yjy.feature.addchallenge.SetCategoryRoute
import com.yjy.feature.addchallenge.SetModeRoute
import com.yjy.feature.addchallenge.SetStartDateRoute
import com.yjy.feature.addchallenge.SetTargetDayRoute
import com.yjy.feature.addchallenge.SetTitleRoute
import com.yjy.feature.addchallenge.SetTogetherRoute

typealias AddChallengeStrings = R.string

fun NavController.navigateToAddChallenge() {
    navigate(ServiceRoute.AddChallenge)
}

private fun NavController.navigateToSetCategory() {
    navigate(ServiceRoute.AddChallenge.SetCategory)
}

private fun NavController.navigateToSetTitle() {
    navigate(ServiceRoute.AddChallenge.SetTitle)
}

private fun NavController.navigateToSetStartDate() {
    navigate(ServiceRoute.AddChallenge.SetStartDate)
}

private fun NavController.navigateToSetTargetDay() {
    navigate(ServiceRoute.AddChallenge.SetTargetDay)
}

private fun NavController.navigateToConfirm() {
    navigate(ServiceRoute.AddChallenge.Confirm)
}

private fun NavController.navigateToSetTogether() {
    navigate(ServiceRoute.AddChallenge.SetTogether)
}

fun NavGraphBuilder.addChallengeNavGraph(
    navController: NavHostController,
    onAddChallenge: (String) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    navigation<ServiceRoute.AddChallenge>(
        startDestination = ServiceRoute.AddChallenge.SetMode::class,
        enterTransition = { slideInToLeft() },
        exitTransition = { slideOutToLeft() },
        popEnterTransition = { slideInToRight() },
        popExitTransition = { slideOutToRight() },
    ) {
        composable<ServiceRoute.AddChallenge.SetMode> { entry ->
            val viewModel = entry.sharedViewModel<AddChallengeViewModel>(navController)

            SetModeRoute(
                onBackClick = navController::popBackStack,
                onSelectMode = navController::navigateToSetCategory,
                viewModel = viewModel,
            )
        }

        composable<ServiceRoute.AddChallenge.SetCategory> { entry ->
            val viewModel = entry.sharedViewModel<AddChallengeViewModel>(navController)

            SetCategoryRoute(
                onBackClick = navController::popBackStack,
                onContinue = navController::navigateToSetTitle,
                viewModel = viewModel,
            )
        }

        composable<ServiceRoute.AddChallenge.SetTitle> { entry ->
            val viewModel = entry.sharedViewModel<AddChallengeViewModel>(navController)

            SetTitleRoute(
                onBackClick = navController::popBackStack,
                onContinueToSetStartDate = navController::navigateToSetStartDate,
                onContinueToSetTargetDay = navController::navigateToSetTargetDay,
                viewModel = viewModel,
            )
        }

        composable<ServiceRoute.AddChallenge.SetStartDate> { entry ->
            val viewModel = entry.sharedViewModel<AddChallengeViewModel>(navController)

            SetStartDateRoute(
                onBackClick = navController::popBackStack,
                onContinue = navController::navigateToSetTargetDay,
                onShowSnackbar = onShowSnackbar,
                viewModel = viewModel,
            )
        }

        composable<ServiceRoute.AddChallenge.SetTargetDay> { entry ->
            val viewModel = entry.sharedViewModel<AddChallengeViewModel>(navController)

            SetTargetDayRoute(
                onBackClick = navController::popBackStack,
                onContinue = navController::navigateToConfirm,
                viewModel = viewModel,
            )
        }

        composable<ServiceRoute.AddChallenge.Confirm> { entry ->
            val viewModel = entry.sharedViewModel<AddChallengeViewModel>(navController)

            ConfirmRoute(
                onBackClick = navController::popBackStack,
                onSetTogetherClick = navController::navigateToSetTogether,
                onAddChallenge = onAddChallenge,
                onShowSnackbar = onShowSnackbar,
                viewModel = viewModel,
            )
        }

        composable<ServiceRoute.AddChallenge.SetTogether> { entry ->
            val viewModel = entry.sharedViewModel<AddChallengeViewModel>(navController)

            SetTogetherRoute(
                onBackClick = navController::popBackStack,
                onAddChallenge = onAddChallenge,
                onShowSnackbar = onShowSnackbar,
                viewModel = viewModel,
            )
        }
    }
}
