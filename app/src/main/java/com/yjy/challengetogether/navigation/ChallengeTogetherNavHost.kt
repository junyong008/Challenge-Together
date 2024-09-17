package com.yjy.challengetogether.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.yjy.challengetogether.ui.ChallengeTogetherAppState
import com.yjy.feature.login.navigation.LOGIN_ROUTE
import com.yjy.feature.login.navigation.loginScreen

@Composable
fun ChallengeTogetherNavHost(
    appState: ChallengeTogetherAppState,
    modifier: Modifier = Modifier,
    startDestination: String = LOGIN_ROUTE,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        loginScreen()
    }
}