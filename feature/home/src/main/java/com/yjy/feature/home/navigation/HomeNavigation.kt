package com.yjy.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.yjy.common.navigation.MainTabRoute
import com.yjy.feature.home.HomeRoute
import com.yjy.feature.home.R

typealias HomeStrings = R.string

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    navigate(MainTabRoute.Home, navOptions)
}

fun NavGraphBuilder.homeScreen(
) {
    composable<MainTabRoute.Home> {
        HomeRoute(

        )
    }
}