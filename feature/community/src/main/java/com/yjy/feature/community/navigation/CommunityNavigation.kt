package com.yjy.feature.community.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.yjy.common.core.extensions.sharedViewModel
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.community.AddPostRoute
import com.yjy.feature.community.AuthoredRoute
import com.yjy.feature.community.BookmarkedRoute
import com.yjy.feature.community.CommentedRoute
import com.yjy.feature.community.CommunityViewModel
import com.yjy.feature.community.ListRoute
import com.yjy.feature.community.R

typealias CommunityStrings = R.string

fun NavController.navigateToCommunity(navOptions: NavOptions? = null) {
    navigate(ServiceRoute.MainTab.Community, navOptions)
}

fun NavController.navigateToAddCommunityPost() {
    navigate(ServiceRoute.AddCommunityPost)
}

private fun NavController.navigateToBookmarked() {
    navigate(ServiceRoute.BookmarkedCommunityPosts)
}

private fun NavController.navigateToAuthored() {
    navigate(ServiceRoute.AuthoredCommunityPosts)
}

private fun NavController.navigateToCommented() {
    navigate(ServiceRoute.CommentedCommunityPosts)
}

fun NavGraphBuilder.communityNavGraph(
    navController: NavHostController,
    onPostClick: (postId: Int) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    navigation<ServiceRoute.Community>(
        enterTransition = { slideInToLeft() },
        popExitTransition = { slideOutToRight() },
        startDestination = ServiceRoute.MainTab.Community::class,
    ) {
        composable<ServiceRoute.MainTab.Community>(
            enterTransition = { fadeIn() },
            popExitTransition = { fadeOut() },
        ) { entry ->
            val viewModel = entry.sharedViewModel<CommunityViewModel>(navController)

            ListRoute(
                onPostClick = onPostClick,
                onBookmarkedClick = navController::navigateToBookmarked,
                onAuthoredClick = navController::navigateToAuthored,
                onCommentedClick = navController::navigateToCommented,
                viewModel = viewModel,
            )
        }

        composable<ServiceRoute.BookmarkedCommunityPosts> {
            BookmarkedRoute(
                onBackClick = navController::popBackStack,
                onPostClick = onPostClick,
            )
        }

        composable<ServiceRoute.AuthoredCommunityPosts> {
            AuthoredRoute(
                onBackClick = navController::popBackStack,
                onPostClick = onPostClick,
            )
        }

        composable<ServiceRoute.CommentedCommunityPosts> {
            CommentedRoute(
                onBackClick = navController::popBackStack,
                onPostClick = onPostClick,
            )
        }

        composable<ServiceRoute.AddCommunityPost> { entry ->
            val viewModel = entry.sharedViewModel<CommunityViewModel>(navController)

            AddPostRoute(
                onBackClick = navController::popBackStack,
                onShowSnackbar = onShowSnackbar,
                viewModel = viewModel,
            )
        }
    }
}
