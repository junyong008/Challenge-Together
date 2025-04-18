package com.yjy.navigation.auth.navigation

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
import com.yjy.common.navigation.AuthRoute
import com.yjy.common.navigation.CommonRoute
import com.yjy.feature.changepassword.navigation.changePasswordScreen
import com.yjy.feature.changepassword.navigation.navigateToChangePassword
import com.yjy.feature.findpassword.navigation.findPasswordNavGraph
import com.yjy.feature.findpassword.navigation.navigateToFindPassword
import com.yjy.feature.intro.navigation.introScreen
import com.yjy.feature.login.navigation.loginScreen
import com.yjy.feature.login.navigation.navigateToLogin
import com.yjy.feature.signup.navigation.navigateToSignUp
import com.yjy.feature.signup.navigation.navigateToSignUpNickname
import com.yjy.feature.signup.navigation.signUpNavGraph

@Composable
internal fun AuthNavHost(
    navigateToService: () -> Unit,
    navController: NavHostController,
    onShowToast: (String) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    startDestination: AuthRoute = AuthRoute.Intro,
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
        introScreen(
            onLoginSuccess = navigateToService,
            onStartWithEmailClick = navController::navigateToLogin,
            onSignUpWithKakaoClick = { navController.navigateToSignUpNickname(kakaoId = it) },
            onSignUpWithGoogleClick = { navController.navigateToSignUpNickname(googleId = it) },
            onSignUpWithNaver = { navController.navigateToSignUpNickname(naverId = it) },
            onSignUpWithGuest = { navController.navigateToSignUpNickname(guestId = it) },
            onShowToast = onShowToast,
            onShowSnackbar = onShowSnackbar,
        )
        loginScreen(
            onBackClick = navController::popBackStack,
            onLoginSuccess = navigateToService,
            onSignUpClick = navController::navigateToSignUp,
            onFindPasswordClick = navController::navigateToFindPassword,
            onShowToast = onShowToast,
            onShowSnackbar = onShowSnackbar,
        )
        signUpNavGraph(
            navController = navController,
            onSignUpSuccess = navigateToService,
            onShowToast = onShowToast,
            onShowSnackbar = onShowSnackbar,
        )
        findPasswordNavGraph(
            navController = navController,
            onVerifySuccess = {
                val navOptions = navOptions {
                    popUpTo(AuthRoute.FindPassword) { inclusive = true }
                    launchSingleTop = true
                }
                navController.navigateToChangePassword(navOptions)
            },
            onShowSnackbar = onShowSnackbar,
        )
        changePasswordScreen(
            onBackClick = navController::popBackStack,
            onPasswordChanged = {
                val navOptions = navOptions {
                    popUpTo(CommonRoute.ChangePassword) { inclusive = true }
                    launchSingleTop = true
                }
                navController.navigateToLogin(navOptions)
            },
            onShowToast = onShowToast,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
