package com.yjy.feature.changepassword.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.CommonRoute
import com.yjy.feature.changepassword.ChangePasswordRoute

fun NavController.navigateToChangePassword(navOptions: NavOptions? = null) {
    navigate(CommonRoute.ChangePassword, navOptions)
}

fun NavGraphBuilder.changePasswordScreen(
    onBackClick: () -> Unit,
    onPasswordChanged: () -> Unit,
    onShowToast: (String) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<CommonRoute.ChangePassword>(
        enterTransition = { slideInToLeft() },
        popExitTransition = { slideOutToRight() },
    ) {
        ChangePasswordRoute(
            onBackClick = onBackClick,
            onPasswordChanged = onPasswordChanged,
            onShowToast = onShowToast,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
