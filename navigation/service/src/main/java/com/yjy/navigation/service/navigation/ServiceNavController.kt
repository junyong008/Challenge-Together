package com.yjy.navigation.service.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.yjy.feature.addchallenge.navigation.navigateToAddChallenge
import com.yjy.feature.home.navigation.navigateToHome

@Composable
internal fun rememberServiceNavController(
    navController: NavHostController = rememberNavController(),
): ServiceNavController {
    return remember(
        navController,
    ) {
        ServiceNavController(
            navController,
        )
    }
}

internal class ServiceNavController(
    val navController: NavHostController,
) {
    private val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTab: MainTab?
        @Composable get() = MainTab.find { tab ->
            currentDestination?.hasRoute(tab::class) == true
        }

    fun navigateToMainTab(tab: MainTab) {
        val navOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            launchSingleTop = true
        }

        when (tab) {
            MainTab.HOME -> navController.navigateToHome(navOptions)
        }
    }

    fun navigateToAddChallenge() {
        navController.navigateToAddChallenge()
    }

    @Composable
    fun isOnMainTab(): Boolean = MainTab.contains {
        currentDestination?.hasRoute(it::class) == true
    }
}
