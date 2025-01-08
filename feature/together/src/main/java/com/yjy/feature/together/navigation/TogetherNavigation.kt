package com.yjy.feature.together.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.together.DetailRoute
import com.yjy.feature.together.ListRoute
import com.yjy.feature.together.R
import com.yjy.model.challenge.core.Category

typealias TogetherStrings = R.string

fun NavController.navigateToTogether(navOptions: NavOptions? = null) {
    navigate(ServiceRoute.MainTab.Together, navOptions)
}

private fun NavController.navigateToTogetherDetail(category: Category) {
    navigate(ServiceRoute.MainTab.Together.Detail(category))
}

fun NavGraphBuilder.togetherNavGraph(
    navController: NavHostController,
    onWaitingChallengeClick: (challengeId: Int) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    navigation<ServiceRoute.MainTab.Together>(
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() },
        startDestination = ServiceRoute.MainTab.Together.List::class,
    ) {
        composable<ServiceRoute.MainTab.Together.List> {
            ListRoute(
                onCategorySelected = navController::navigateToTogetherDetail,
            )
        }

        composable<ServiceRoute.MainTab.Together.Detail>(
            enterTransition = { slideInToLeft() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { slideOutToRight() },
        ) { entry ->
            val category = entry.toRoute<ServiceRoute.MainTab.Together.Detail>().category

            DetailRoute(
                category = category,
                onBackClick = navController::popBackStack,
                onWaitingChallengeClick = { onWaitingChallengeClick(it.id) },
                onShowSnackbar = onShowSnackbar,
            )
        }
    }
}
