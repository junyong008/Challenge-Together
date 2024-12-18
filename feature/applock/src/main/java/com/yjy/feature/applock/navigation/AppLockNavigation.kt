package com.yjy.feature.applock.navigation

import androidx.compose.animation.EnterTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.yjy.common.core.extensions.sharedViewModel
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.applock.AppLockRoute
import com.yjy.feature.applock.AppLockViewModel
import com.yjy.feature.applock.PinSettingRoute
import com.yjy.feature.applock.PinValidationRoute

fun NavController.navigateToAppLockSetting() {
    navigate(ServiceRoute.AppLock.Setting)
}

fun NavController.navigateToAppLockPinValidation(navOptions: NavOptions? = null) {
    navigate(ServiceRoute.AppLock.PinValidation, navOptions)
}

private fun NavController.navigateToAppLockPinSetting() {
    navigate(ServiceRoute.AppLock.PinSetting)
}

fun NavGraphBuilder.appLockNavGraph(
    navController: NavHostController,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    navigation<ServiceRoute.AppLock>(
        enterTransition = { slideInToLeft() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutToRight() },
        startDestination = ServiceRoute.AppLock.Setting::class,
    ) {
        composable<ServiceRoute.AppLock.Setting> { entry ->
            val viewModel = entry.sharedViewModel<AppLockViewModel>(navController)

            AppLockRoute(
                onBackClick = navController::popBackStack,
                onSetPinClick = navController::navigateToAppLockPinSetting,
                onChangePinClick = navController::navigateToAppLockPinSetting,
                viewModel = viewModel,
            )
        }

        composable<ServiceRoute.AppLock.PinSetting> { entry ->
            val viewModel = entry.sharedViewModel<AppLockViewModel>(navController)

            PinSettingRoute(
                onBackClick = navController::popBackStack,
                onShowSnackbar = onShowSnackbar,
                viewModel = viewModel,
            )
        }

        composable<ServiceRoute.AppLock.PinValidation>(
            enterTransition = { EnterTransition.None },
            exitTransition = { fadeOut() },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { fadeOut() },
        ) {
            PinValidationRoute(
                onBackClick = navController::popBackStack,
                onShowSnackbar = onShowSnackbar,
            )
        }
    }
}
