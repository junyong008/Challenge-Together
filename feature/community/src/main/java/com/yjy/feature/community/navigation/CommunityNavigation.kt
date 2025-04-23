package com.yjy.feature.community.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.yjy.common.core.constants.DeepLinkConfig.SCHEME_AND_HOST
import com.yjy.common.core.constants.DeepLinkPath.POST
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
import com.yjy.feature.community.EditPostRoute
import com.yjy.feature.community.ListRoute
import com.yjy.feature.community.PostRoute
import com.yjy.feature.community.R

const val COMMUNITY_POST_ID = "postId"
private const val DEEP_LINK_URI_PATTERN = "${SCHEME_AND_HOST}/$POST/{$COMMUNITY_POST_ID}"

typealias CommunityStrings = R.string

fun NavController.navigateToCommunity(navOptions: NavOptions? = null) {
    navigate(ServiceRoute.MainTab.Community, navOptions)
}

fun NavController.navigateToAddCommunityPost() {
    navigate(ServiceRoute.Community.AddPost)
}

fun NavController.navigateToCommunityPost(postId: Int) {
    navigate(ServiceRoute.Community.Post(postId))
}

private fun NavController.navigateToEditPost(postId: Int, content: String) {
    navigate(ServiceRoute.Community.EditPost(postId, content))
}

private fun NavController.navigateToBookmarked() {
    navigate(ServiceRoute.Community.BookmarkedPosts)
}

private fun NavController.navigateToAuthored() {
    navigate(ServiceRoute.Community.AuthoredPosts)
}

private fun NavController.navigateToCommented() {
    navigate(ServiceRoute.Community.CommentedPosts)
}

fun NavGraphBuilder.communityNavGraph(
    navController: NavHostController,
    onLinkedChallengeClick: (challengeId: Int) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    navigation<ServiceRoute.Community>(
        enterTransition = { slideInToLeft() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutToRight() },
        startDestination = ServiceRoute.MainTab.Community::class,
    ) {
        composable<ServiceRoute.MainTab.Community>(
            enterTransition = { fadeIn() },
            popExitTransition = { fadeOut() },
        ) { entry ->
            val viewModel = entry.sharedViewModel<CommunityViewModel>(navController)

            ListRoute(
                onPostClick = navController::navigateToCommunityPost,
                onBookmarkedClick = navController::navigateToBookmarked,
                onAuthoredClick = navController::navigateToAuthored,
                onCommentedClick = navController::navigateToCommented,
                onShowSnackbar = onShowSnackbar,
                viewModel = viewModel,
            )
        }

        composable<ServiceRoute.Community.AddPost> { entry ->
            val viewModel = entry.sharedViewModel<CommunityViewModel>(navController)

            AddPostRoute(
                onBackClick = navController::popBackStack,
                onShowSnackbar = onShowSnackbar,
                viewModel = viewModel,
            )
        }

        composable<ServiceRoute.Community.BookmarkedPosts> {
            BookmarkedRoute(
                onBackClick = navController::popBackStack,
                onPostClick = navController::navigateToCommunityPost,
            )
        }

        composable<ServiceRoute.Community.AuthoredPosts> {
            AuthoredRoute(
                onBackClick = navController::popBackStack,
                onPostClick = navController::navigateToCommunityPost,
            )
        }

        composable<ServiceRoute.Community.CommentedPosts> {
            CommentedRoute(
                onBackClick = navController::popBackStack,
                onPostClick = navController::navigateToCommunityPost,
            )
        }

        composable<ServiceRoute.Community.Post>(
            deepLinks = listOf(navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }),
        ) {
            PostRoute(
                onBackClick = navController::popBackStack,
                onEditPostClick = navController::navigateToEditPost,
                onLinkedChallengeClick = onLinkedChallengeClick,
                onShowSnackbar = onShowSnackbar,
            )
        }

        composable<ServiceRoute.Community.EditPost> { entry ->
            val postId = entry.toRoute<ServiceRoute.Community.EditPost>().postId
            val content = entry.toRoute<ServiceRoute.Community.EditPost>().content

            EditPostRoute(
                postId = postId,
                content = content,
                onBackClick = navController::popBackStack,
                onShowSnackbar = onShowSnackbar,
            )
        }
    }
}
