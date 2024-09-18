package com.yjy.feature.login.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.yjy.feature.login.LoginRoute
import com.yjy.feature.login.R

typealias LoginStrings = R.string
const val LOGIN_ROUTE = "login_route"

fun NavGraphBuilder.loginScreen() {
    composable(route = LOGIN_ROUTE) {
        LoginRoute()
    }
}