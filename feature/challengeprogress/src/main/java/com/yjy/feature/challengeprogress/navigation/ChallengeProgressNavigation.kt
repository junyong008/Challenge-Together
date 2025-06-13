package com.yjy.feature.challengeprogress.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.challengeprogress.ChallengeProgressRoute

fun NavController.navigateToChallengeProgress(challengeId: Int) {
    navigate(ServiceRoute.ChallengeProgress(challengeId))
}

fun NavGraphBuilder.challengeProgressScreen(
    onBackClick: () -> Unit,
) {
    composable<ServiceRoute.ChallengeProgress>(
        enterTransition = { slideInToLeft() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutToRight() },
    ) {
        ChallengeProgressRoute(
            onBackClick = onBackClick,
        )
    }
}
