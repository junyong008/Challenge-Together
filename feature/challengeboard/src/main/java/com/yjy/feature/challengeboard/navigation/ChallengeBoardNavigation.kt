package com.yjy.feature.challengeboard.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.challengeboard.ChallengeBoardRoute

fun NavController.navigateToChallengeBoard(challengeId: Int, isAlone: Boolean) {
    navigate(ServiceRoute.ChallengeBoard(challengeId, isAlone))
}

fun NavGraphBuilder.challengeBoardScreen(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<ServiceRoute.ChallengeBoard>(
        enterTransition = { slideInToLeft() },
        popExitTransition = { slideOutToRight() },
    ) { entry ->
        val isAlone = entry.toRoute<ServiceRoute.ChallengeBoard>().isAlone

        ChallengeBoardRoute(
            isAlone = isAlone,
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}