package com.yjy.feature.deleteaccount.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.deleteaccount.DeleteAccountRoute

fun NavController.navigateToDeleteAccount() {
    navigate(ServiceRoute.DeleteAccount)
}

fun NavGraphBuilder.deleteAccountScreen(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<ServiceRoute.DeleteAccount>(
        enterTransition = { slideInToLeft() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutToRight() },
    ) {
        DeleteAccountRoute(
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
