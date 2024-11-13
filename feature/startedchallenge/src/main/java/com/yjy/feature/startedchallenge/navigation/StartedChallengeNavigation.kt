package com.yjy.feature.startedchallenge.navigation

import androidx.compose.animation.fadeIn
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.startedchallenge.StartedChallengeRoute
import com.yjy.model.challenge.core.Category

const val STARTED_CHALLENGE_ID = "challengeId"
private const val DEEP_LINK_URI_PATTERN =
    "https://www.challenge-together.apps.com/challenge/started/{$STARTED_CHALLENGE_ID}"

fun NavController.navigateToStartedChallenge(
    challengeId: Int,
    navOptions: NavOptions? = null,
) {
    navigate(ServiceRoute.StartedChallenge(challengeId), navOptions)
}

fun NavGraphBuilder.startedChallengeScreen(
    onBackClick: () -> Unit,
    onEditCategoryClick: (Int, Category) -> Unit,
    onEditTitleClick: (Int, String, String) -> Unit,
    onEditTargetDaysClick: (Int, String, Int) -> Unit,
    onResetRecordClick: (Int) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<ServiceRoute.StartedChallenge>(
        enterTransition = { slideInToLeft() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutToRight() },
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        StartedChallengeRoute(
            onBackClick = onBackClick,
            onEditCategoryClick = onEditCategoryClick,
            onEditTitleClick = onEditTitleClick,
            onEditTargetDaysClick = onEditTargetDaysClick,
            onResetRecordClick = onResetRecordClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
