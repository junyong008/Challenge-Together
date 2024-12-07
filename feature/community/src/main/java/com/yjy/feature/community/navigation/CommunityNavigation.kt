package com.yjy.feature.community.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.community.AuthoredRoute
import com.yjy.feature.community.BookmarkedRoute
import com.yjy.feature.community.CommentedRoute
import com.yjy.feature.community.ListRoute
import com.yjy.feature.community.R

typealias CommunityStrings = R.string

fun NavController.navigateToCommunity(navOptions: NavOptions? = null) {
    navigate(ServiceRoute.MainTab.Community, navOptions)
}

private fun NavController.navigateToBookmarked() {
    navigate(ServiceRoute.MainTab.Community.Bookmarked)
}

private fun NavController.navigateToAuthored() {
    navigate(ServiceRoute.MainTab.Community.Authored)
}

private fun NavController.navigateToCommented() {
    navigate(ServiceRoute.MainTab.Community.Commented)
}

fun NavGraphBuilder.communityNavGraph(
    navController: NavHostController,
    onPostClick: (postId: Int) -> Unit,
) {
    navigation<ServiceRoute.MainTab.Community>(
        startDestination = ServiceRoute.MainTab.Community.All::class,
        enterTransition = { slideInToLeft() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutToRight() },
    ) {
        composable<ServiceRoute.MainTab.Community.All>(
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
        ) {
            ListRoute(
                onPostClick = onPostClick,
                onBookmarkedClick = navController::navigateToBookmarked,
                onAuthoredClick = navController::navigateToAuthored,
                onCommentedClick = navController::navigateToCommented,
            )
        }

        composable<ServiceRoute.MainTab.Community.Bookmarked> {
            BookmarkedRoute(
                onBackClick = navController::popBackStack,
                onPostClick = onPostClick,
            )
        }

        composable<ServiceRoute.MainTab.Community.Authored> {
            AuthoredRoute(
                onBackClick = navController::popBackStack,
                onPostClick = onPostClick,
            )
        }

        composable<ServiceRoute.MainTab.Community.Commented> {
            CommentedRoute(
                onBackClick = navController::popBackStack,
                onPostClick = onPostClick,
            )
        }
    }
}
