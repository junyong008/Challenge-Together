package com.yjy.navigation.service.navigation

import androidx.compose.runtime.Composable
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.community.navigation.CommunityStrings
import com.yjy.feature.home.navigation.HomeStrings
import com.yjy.feature.together.navigation.TogetherStrings

internal enum class MainTab(
    val selectedIconResId: Int,
    val unselectedIconResId: Int,
    val iconTextId: Int,
    val route: ServiceRoute.MainTab,
) {
    HOME(
        selectedIconResId = ChallengeTogetherIcons.Home,
        unselectedIconResId = ChallengeTogetherIcons.Home,
        iconTextId = HomeStrings.feature_home_navigation_title,
        route = ServiceRoute.MainTab.Home,
    ),
    TOGETHER(
        selectedIconResId = ChallengeTogetherIcons.Together,
        unselectedIconResId = ChallengeTogetherIcons.Together,
        iconTextId = TogetherStrings.feature_together_title,
        route = ServiceRoute.MainTab.Together,
    ),
    COMMUNITY(
        selectedIconResId = ChallengeTogetherIcons.Community,
        unselectedIconResId = ChallengeTogetherIcons.Community,
        iconTextId = CommunityStrings.feature_community_title,
        route = ServiceRoute.MainTab.Community,
    ),
    ;

    companion object {
        @Composable
        fun find(predicate: @Composable (ServiceRoute.MainTab) -> Boolean): MainTab? {
            return entries.find { predicate(it.route) }
        }
    }
}
