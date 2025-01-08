package com.yjy.feature.startedchallenge.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.yjy.common.core.constants.DeepLinkConfig.SCHEME_AND_HOST
import com.yjy.common.core.constants.DeepLinkPath.STARTED
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.startedchallenge.StartedChallengeRoute
import com.yjy.model.challenge.core.Category

const val STARTED_CHALLENGE_ID = "challengeId"
private const val DEEP_LINK_URI_PATTERN = "${SCHEME_AND_HOST}/$STARTED/{$STARTED_CHALLENGE_ID}"

fun NavController.navigateToStartedChallenge(
    challengeId: Int,
    navOptions: NavOptions? = null,
) {
    navigate(ServiceRoute.StartedChallenge(challengeId), navOptions)
}

fun NavGraphBuilder.startedChallengeScreen(
    onBackClick: () -> Unit,
    onResetClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditCategoryClick: (challengeId: Int, category: Category) -> Unit,
    onEditTitleClick: (challengeId: Int, title: String, description: String) -> Unit,
    onEditTargetDaysClick: (challengeId: Int, targetDays: String, currentDays: Int) -> Unit,
    onResetRecordClick: (challengeId: Int) -> Unit,
    onBoardClick: (challengeId: Int, isEditable: Boolean) -> Unit,
    onRankingClick: (challengeId: Int) -> Unit,
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
            onResetClick = onResetClick,
            onDeleteClick = onDeleteClick,
            onEditCategoryClick = onEditCategoryClick,
            onEditTitleClick = onEditTitleClick,
            onEditTargetDaysClick = onEditTargetDaysClick,
            onResetRecordClick = onResetRecordClick,
            onBoardClick = onBoardClick,
            onRankingClick = onRankingClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
