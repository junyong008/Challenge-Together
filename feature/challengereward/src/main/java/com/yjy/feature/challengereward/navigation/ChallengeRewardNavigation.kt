package com.yjy.feature.challengereward.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.challengereward.ChallengeRewardRoute

fun NavController.navigateToChallengeReward(challengeId: Int) {
    navigate(ServiceRoute.ChallengeReward(challengeId))
}

fun NavGraphBuilder.challengeRewardScreen(
    onBackClick: () -> Unit,
) {
    composable<ServiceRoute.ChallengeReward>(
        enterTransition = { slideInToLeft() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutToRight() },
    ) {
        ChallengeRewardRoute(
            onBackClick = onBackClick,
        )
    }
}
