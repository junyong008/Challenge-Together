package com.yjy.feature.resetrecord.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.resetrecord.ResetRecordRoute

fun NavController.navigateToResetRecord(challengeId: String) {
    navigate(ServiceRoute.ResetRecord(challengeId))
}

fun NavGraphBuilder.resetRecordScreen(
    onBackClick: () -> Unit,
) {
    composable<ServiceRoute.ResetRecord>(
        enterTransition = { slideInToLeft() },
        popExitTransition = { slideOutToRight() },
    ) {
        ResetRecordRoute(
            onBackClick = onBackClick,
        )
    }
}
