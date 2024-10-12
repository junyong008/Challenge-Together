package com.yjy.challengetogether.navigation

import androidx.compose.runtime.Composable
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.navigation.MainTabRoute
import com.yjy.common.navigation.Route
import com.yjy.feature.home.navigation.HomeStrings

internal enum class MainTab(
    val selectedIconResId: Int,
    val unselectedIconResId: Int,
    val iconTextId: Int,
    val route: MainTabRoute,
) {
    HOME(
        selectedIconResId = ChallengeTogetherIcons.Home,
        unselectedIconResId = ChallengeTogetherIcons.Home,
        iconTextId = HomeStrings.feature_home_navigation_title,
        route = MainTabRoute.Home,
    ),
    ;

    companion object {
        @Composable
        fun find(predicate: @Composable (MainTabRoute) -> Boolean): MainTab? {
            return entries.find { predicate(it.route) }
        }

        @Composable
        fun contains(predicate: @Composable (Route) -> Boolean): Boolean {
            return entries.map { it.route }.any { predicate(it) }
        }
    }
}
