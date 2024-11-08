package com.yjy.feature.startedchallenge.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.startedchallenge.StartedChallengeRoute
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.TargetDays

fun NavController.navigateToStartedChallenge(
    challengeId: String,
    navOptions: NavOptions? = null,
) {
    navigate(ServiceRoute.StartedChallenge(challengeId), navOptions)
}

fun NavGraphBuilder.startedChallengeScreen(
    onBackClick: () -> Unit,
    onEditCategoryClick: (String, Category) -> Unit,
    onEditTitleClick: (String, String, String) -> Unit,
    onEditTargetDaysClick: (String, TargetDays) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<ServiceRoute.StartedChallenge>(
        enterTransition = { slideInToLeft() },
        popExitTransition = { slideOutToRight() },
    ) {
        StartedChallengeRoute(
            onBackClick = onBackClick,
            onEditCategoryClick = onEditCategoryClick,
            onEditTitleClick = onEditTitleClick,
            onEditTargetDaysClick = onEditTargetDaysClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
