package com.yjy.navigation.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
internal fun rememberAuthNavController(
    navController: NavHostController = rememberNavController(),
): AuthNavController {
    return remember(
        navController,
    ) {
        AuthNavController(
            navController,
        )
    }
}

internal class AuthNavController(
    val navController: NavHostController,
) {

}