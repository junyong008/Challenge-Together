package com.yjy.navigation.service.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.addchallenge.navigation.addChallengeNavGraph
import com.yjy.feature.challengeboard.navigation.challengeBoardScreen
import com.yjy.feature.challengeboard.navigation.navigateToChallengeBoard
import com.yjy.feature.challengeranking.navigation.challengeRankingScreen
import com.yjy.feature.challengeranking.navigation.navigateToChallengeRanking
import com.yjy.feature.changepassword.navigation.changePasswordScreen
import com.yjy.feature.community.navigation.communityNavGraph
import com.yjy.feature.community.navigation.navigateToCommunityPost
import com.yjy.feature.completedchallenges.navigation.completedChallengesScreen
import com.yjy.feature.completedchallenges.navigation.navigateToCompletedChallenges
import com.yjy.feature.editchallenge.navigation.editChallengeScreen
import com.yjy.feature.editchallenge.navigation.navigateToEditCategory
import com.yjy.feature.editchallenge.navigation.navigateToEditTargetDays
import com.yjy.feature.editchallenge.navigation.navigateToEditTitleDescription
import com.yjy.feature.home.navigation.homeScreen
import com.yjy.feature.notification.navigation.navigateToNotification
import com.yjy.feature.notification.navigation.notificationScreen
import com.yjy.feature.notificationsetting.navigation.navigateToNotificationSetting
import com.yjy.feature.notificationsetting.navigation.notificationSettingScreen
import com.yjy.feature.resetrecord.navigation.navigateToResetRecord
import com.yjy.feature.resetrecord.navigation.resetRecordScreen
import com.yjy.feature.startedchallenge.navigation.navigateToStartedChallenge
import com.yjy.feature.startedchallenge.navigation.startedChallengeScreen
import com.yjy.feature.together.navigation.togetherNavGraph
import com.yjy.feature.waitingchallenge.navigation.navigateToWaitingChallenge
import com.yjy.feature.waitingchallenge.navigation.waitingChallengeScreen

@Composable
internal fun ServiceNavHost(
    navigateToAuth: () -> Unit,
    navController: NavHostController,
    onShowToast: (String) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    startDestination: ServiceRoute = ServiceRoute.MainTab.Home,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() },
        modifier = modifier,
    ) {
        homeScreen(
            onWaitingChallengeClick = navController::navigateToWaitingChallenge,
            onStartedChallengeClick = navController::navigateToStartedChallenge,
            onCompletedChallengeClick = navController::navigateToCompletedChallenges,
            onNotificationClick = navController::navigateToNotification,
        )
        togetherNavGraph(
            navController = navController,
            onWaitingChallengeClick = navController::navigateToWaitingChallenge,
        )
        communityNavGraph(
            navController = navController,
            onShowSnackbar = onShowSnackbar,
        )
        completedChallengesScreen(
            onBackClick = navController::popBackStack,
            onCompletedChallengeClick = navController::navigateToStartedChallenge,
        )
        notificationScreen(
            onBackClick = navController::popBackStack,
            onSettingClick = navController::navigateToNotificationSetting,
            onShowSnackbar = onShowSnackbar,
            onWaitingChallengeNotificationClick = navController::navigateToWaitingChallenge,
            onStartedChallengeNotificationClick = navController::navigateToStartedChallenge,
            onCommunityPostNotificationClick = navController::navigateToCommunityPost,
        )
        notificationSettingScreen(
            onBackClick = navController::popBackStack,
        )
        addChallengeNavGraph(
            navController = navController,
            onChallengeStarted = { challengeId ->
                val navOptions = navOptions {
                    popUpTo(ServiceRoute.AddChallenge) { inclusive = true }
                    launchSingleTop = true
                }
                navController.navigateToStartedChallenge(challengeId, navOptions)
            },
            onWaitingChallengeCreated = { challengeId ->
                val navOptions = navOptions {
                    popUpTo(ServiceRoute.AddChallenge) { inclusive = true }
                    launchSingleTop = true
                }
                navController.navigateToWaitingChallenge(challengeId, navOptions)
            },
            onShowSnackbar = onShowSnackbar,
        )
        editChallengeScreen(
            onBackClick = navController::popBackStack,
            onShowSnackbar = onShowSnackbar,
        )
        waitingChallengeScreen(
            onBackClick = navController::popBackStack,
            onChallengeStart = { challengeId ->
                navController.popBackStack()
                navController.navigateToStartedChallenge(challengeId)
            },
            onBoardClick = navController::navigateToChallengeBoard,
            onShowSnackbar = onShowSnackbar,
        )
        startedChallengeScreen(
            onBackClick = navController::popBackStack,
            onEditCategoryClick = navController::navigateToEditCategory,
            onEditTitleClick = navController::navigateToEditTitleDescription,
            onEditTargetDaysClick = navController::navigateToEditTargetDays,
            onResetRecordClick = navController::navigateToResetRecord,
            onBoardClick = navController::navigateToChallengeBoard,
            onRankingClick = navController::navigateToChallengeRanking,
            onShowSnackbar = onShowSnackbar,
        )
        resetRecordScreen(
            onBackClick = navController::popBackStack,
        )
        challengeBoardScreen(
            onBackClick = navController::popBackStack,
            onShowSnackbar = onShowSnackbar,
        )
        challengeRankingScreen(
            onBackClick = navController::popBackStack,
            onShowSnackbar = onShowSnackbar,
        )
        changePasswordScreen(
            onBackClick = navController::popBackStack,
            onPasswordChanged = navigateToAuth,
            onShowToast = onShowToast,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
