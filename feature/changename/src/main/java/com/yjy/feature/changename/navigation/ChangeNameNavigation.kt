package com.yjy.feature.changename.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.changename.ChangeNameRoute

fun NavController.navigateToChangeName() {
    navigate(ServiceRoute.ChangeName)
}

fun NavGraphBuilder.changeNameScreen(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<ServiceRoute.ChangeName>(
        enterTransition = { slideInToLeft() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutToRight() },
    ) {
        ChangeNameRoute(
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
