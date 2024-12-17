package com.yjy.navigation.service.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.yjy.feature.addchallenge.navigation.navigateToAddChallenge
import com.yjy.feature.community.navigation.navigateToAddCommunityPost
import com.yjy.feature.community.navigation.navigateToCommunity
import com.yjy.feature.home.navigation.navigateToHome
import com.yjy.feature.my.navigation.navigateToMy
import com.yjy.feature.together.navigation.navigateToTogether

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
            currentDestination?.hierarchy?.any {
                it.hasRoute(tab::class)
            } == true
        }

    fun navigateToMainTab(tab: MainTab) {
        val navOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id)
            launchSingleTop = true
        }

        when (tab) {
            MainTab.HOME -> navController.navigateToHome(navOptions)
            MainTab.TOGETHER -> navController.navigateToTogether(navOptions)
            MainTab.COMMUNITY -> navController.navigateToCommunity(navOptions)
            MainTab.MY -> navController.navigateToMy(navOptions)
        }
    }

    fun navigateToAddChallenge() {
        navController.navigateToAddChallenge()
    }

    fun navigateToAddCommunityPost() {
        navController.navigateToAddCommunityPost()
    }

    @Composable
    fun isOnMainTab(): Boolean = currentTab != null
}
