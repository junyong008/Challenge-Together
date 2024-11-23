package com.yjy.feature.challengeranking.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.challengeranking.ChallengeRankingRoute

fun NavController.navigateToChallengeRanking(challengeId: Int) {
    navigate(ServiceRoute.ChallengeRanking(challengeId))
}

fun NavGraphBuilder.challengeRankingScreen(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<ServiceRoute.ChallengeRanking>(
        enterTransition = { slideInToLeft() },
        popExitTransition = { slideOutToRight() },
    ) {
        ChallengeRankingRoute(
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
