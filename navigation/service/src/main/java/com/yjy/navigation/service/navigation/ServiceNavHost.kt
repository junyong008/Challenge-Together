package com.yjy.navigation.service.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.addchallenge.navigation.addChallengeNavGraph
import com.yjy.feature.applock.navigation.appLockNavGraph
import com.yjy.feature.applock.navigation.navigateToAppLockSetting
import com.yjy.feature.challengeboard.navigation.challengeBoardScreen
import com.yjy.feature.challengeboard.navigation.navigateToChallengeBoard
import com.yjy.feature.challengeprogress.navigation.challengeProgressScreen
import com.yjy.feature.challengeprogress.navigation.navigateToChallengeProgress
import com.yjy.feature.challengeranking.navigation.challengeRankingScreen
import com.yjy.feature.challengeranking.navigation.navigateToChallengeRanking
import com.yjy.feature.challengereward.navigation.challengeRewardScreen
import com.yjy.feature.challengereward.navigation.navigateToChallengeReward
import com.yjy.feature.changename.navigation.changeNameScreen
import com.yjy.feature.changename.navigation.navigateToChangeName
import com.yjy.feature.changepassword.navigation.changePasswordScreen
import com.yjy.feature.changepassword.navigation.navigateToChangePassword
import com.yjy.feature.community.navigation.communityNavGraph
import com.yjy.feature.community.navigation.navigateToCommunityPost
import com.yjy.feature.completedchallenges.navigation.completedChallengesScreen
import com.yjy.feature.completedchallenges.navigation.navigateToCompletedChallenges
import com.yjy.feature.deleteaccount.navigation.deleteAccountScreen
import com.yjy.feature.deleteaccount.navigation.navigateToDeleteAccount
import com.yjy.feature.editchallenge.navigation.editChallengeScreen
import com.yjy.feature.editchallenge.navigation.navigateToEditCategory
import com.yjy.feature.editchallenge.navigation.navigateToEditTargetDays
import com.yjy.feature.editchallenge.navigation.navigateToEditTitleDescription
import com.yjy.feature.home.navigation.homeScreen
import com.yjy.feature.linkaccount.navigation.linkAccountScreen
import com.yjy.feature.linkaccount.navigation.navigateToLinkAccount
import com.yjy.feature.my.navigation.myScreen
import com.yjy.feature.notification.navigation.navigateToNotification
import com.yjy.feature.notification.navigation.notificationScreen
import com.yjy.feature.notificationsetting.navigation.navigateToNotificationSetting
import com.yjy.feature.notificationsetting.navigation.notificationSettingScreen
import com.yjy.feature.premium.navigation.navigateToPremium
import com.yjy.feature.premium.navigation.premiumScreen
import com.yjy.feature.resetrecord.navigation.navigateToResetRecord
import com.yjy.feature.resetrecord.navigation.resetRecordScreen
import com.yjy.feature.startedchallenge.navigation.navigateToStartedChallenge
import com.yjy.feature.startedchallenge.navigation.startedChallengeScreen
import com.yjy.feature.together.navigation.togetherNavGraph
import com.yjy.feature.waitingchallenge.navigation.navigateToWaitingChallenge
import com.yjy.feature.waitingchallenge.navigation.waitingChallengeScreen
import com.yjy.navigation.service.util.AdType

@Composable
internal fun ServiceNavHost(
    navController: NavHostController,
    onShowAd: (AdType) -> Unit,
    onShowToast: (String) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    startDestination: ServiceRoute = ServiceRoute.MainTab.Home,
) {
    val crashlytics = FirebaseCrashlytics.getInstance()

    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val route = destination.route ?: "unknown_route"
            crashlytics.log("Navigated to $route")
            crashlytics.setCustomKey("last_screen", route)
        }
        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

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
            onShowSnackbar = onShowSnackbar,
        )
        communityNavGraph(
            navController = navController,
            onLinkedChallengeClick = navController::navigateToWaitingChallenge,
            onShowSnackbar = onShowSnackbar,
        )
        myScreen(
            onNotificationSettingClick = navController::navigateToNotificationSetting,
            onAppLockSettingClick = navController::navigateToAppLockSetting,
            onAccountLinkClick = navController::navigateToLinkAccount,
            onPremiumClick = navController::navigateToPremium,
            onChangeNicknameClick = navController::navigateToChangeName,
            onChangePasswordClick = navController::navigateToChangePassword,
            onDeleteAccountClick = navController::navigateToDeleteAccount,
            onShowSnackbar = onShowSnackbar,
        )
        appLockNavGraph(
            navController = navController,
            onPremiumExploreClick = navController::navigateToPremium,
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
        premiumScreen(
            onBackClick = navController::popBackStack,
            onShowSnackbar = onShowSnackbar,
        )
        changeNameScreen(
            onBackClick = navController::popBackStack,
            onShowSnackbar = onShowSnackbar,
        )
        linkAccountScreen(
            onBackClick = navController::popBackStack,
            onShowSnackbar = onShowSnackbar,
        )
        deleteAccountScreen(
            onBackClick = navController::popBackStack,
            onShowSnackbar = onShowSnackbar,
        )
        addChallengeNavGraph(
            navController = navController,
            onCreateClick = { onShowAd(AdType.CHALLENGE_CREATE) },
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
            onResetClick = { onShowAd(AdType.CHALLENGE_RESET) },
            onDeleteClick = { onShowAd(AdType.CHALLENGE_DELETE) },
            onEditCategoryClick = navController::navigateToEditCategory,
            onEditTitleClick = navController::navigateToEditTitleDescription,
            onEditTargetDaysClick = navController::navigateToEditTargetDays,
            onResetRecordClick = navController::navigateToResetRecord,
            onBoardClick = navController::navigateToChallengeBoard,
            onRankingClick = navController::navigateToChallengeRanking,
            onProgressClick = navController::navigateToChallengeProgress,
            onRewardClick = navController::navigateToChallengeReward,
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
        challengeProgressScreen(
            onBackClick = navController::popBackStack,
        )
        challengeRewardScreen(
            onBackClick = navController::popBackStack,
        )
        changePasswordScreen(
            onBackClick = navController::popBackStack,
            onShowToast = onShowToast,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
