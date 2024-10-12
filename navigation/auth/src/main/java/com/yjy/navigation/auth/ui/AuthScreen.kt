package com.yjy.navigation.auth.ui

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.feature.changepassword.navigation.changePasswordScreen
import com.yjy.feature.changepassword.navigation.navigateToChangePassword
import com.yjy.feature.findpassword.navigation.findPasswordNavGraph
import com.yjy.feature.findpassword.navigation.navigateToFindPassword
import com.yjy.feature.login.navigation.loginScreen
import com.yjy.feature.login.navigation.navigateToLogin
import com.yjy.feature.signup.navigation.navigateToSignUp
import com.yjy.feature.signup.navigation.signUpNavGraph
import com.yjy.navigation.auth.route.AuthRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun AuthScreen(
    modifier: Modifier = Modifier,
    snackbarScope: CoroutineScope = rememberCoroutineScope(),
    navigator: AuthNavController = rememberAuthNavController(),
) {
    val navController = navigator.navController

    val snackbarHostState = remember { SnackbarHostState() }
    val showSnackbar: (SnackbarType, String) -> Unit = { type, message ->
        snackbarScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = type.name,
                duration = SnackbarDuration.Short,
            )
        }
    }

    NavHost(
        navController = navController,
        startDestination = AuthRoute.Login,
        modifier = modifier,
    ) {
        loginScreen(
            onLoginSuccess = {  },
            onSignUpClick = navController::navigateToSignUp,
            onFindPasswordClick = navController::navigateToFindPassword,
            onShowSnackbar = showSnackbar,
        )
        signUpNavGraph(
            navController = navController,
            onSignUpSuccess = {  },
            onShowSnackbar = showSnackbar,
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
            onShowSnackbar = showSnackbar,
        )
        changePasswordScreen(
            onBackClick = navController::popBackStack,
            onPasswordChanged = navController::navigateToLogin,
            onShowSnackbar = showSnackbar,
        )
    }
}