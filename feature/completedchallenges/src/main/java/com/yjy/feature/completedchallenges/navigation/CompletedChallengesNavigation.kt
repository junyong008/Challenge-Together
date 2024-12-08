package com.yjy.feature.completedchallenges.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.completedchallenges.CompletedChallengesRoute

fun NavController.navigateToCompletedChallenges() {
    navigate(ServiceRoute.CompletedChallenges)
}

fun NavGraphBuilder.completedChallengesScreen(
    onBackClick: () -> Unit,
    onCompletedChallengeClick: (challengeId: Int) -> Unit,
) {
    composable<ServiceRoute.CompletedChallenges>(
        enterTransition = { slideInToLeft() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutToRight() },
    ) {
        CompletedChallengesRoute(
            onBackClick = onBackClick,
            onCompletedChallengeClick = { onCompletedChallengeClick(it.id) },
        )
    }
}
