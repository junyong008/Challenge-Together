package com.yjy.challengetogether.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun rememberChallengeTogetherAppState(
    navController: NavHostController = rememberNavController(),
): ChallengeTogetherAppState {
    return remember(
        navController,
    ) {
        ChallengeTogetherAppState(
            navController,
        )
    }
}

@Stable
class ChallengeTogetherAppState(
    val navController: NavHostController,
) {
}