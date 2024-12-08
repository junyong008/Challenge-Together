package com.yjy.feature.waitingchallenge.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.waitingchallenge.WaitingChallengeRoute

const val WAITING_CHALLENGE_ID = "challengeId"
private const val DEEP_LINK_URI_PATTERN =
    "https://www.challenge-together.apps.com/challenge/waiting/{$WAITING_CHALLENGE_ID}"

fun NavController.navigateToWaitingChallenge(
    challengeId: Int,
    navOptions: NavOptions? = null,
) {
    navigate(ServiceRoute.WaitingChallenge(challengeId), navOptions)
}

fun NavGraphBuilder.waitingChallengeScreen(
    onBackClick: () -> Unit,
    onChallengeStart: (challengeId: Int) -> Unit,
    onBoardClick: (challengeId: Int, isEditable: Boolean) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<ServiceRoute.WaitingChallenge>(
        enterTransition = { slideInToLeft() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutToRight() },
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        WaitingChallengeRoute(
            onBackClick = onBackClick,
            onChallengeStart = onChallengeStart,
            onBoardClick = onBoardClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
