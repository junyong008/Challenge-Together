package com.yjy.common.core.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import timber.log.Timber

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavHostController,
): T {
    val parentRoute = destination.parent?.route
    val parentEntry = remember(this, parentRoute) { safeGetBackStackEntry(navController, parentRoute) }
    return if (parentEntry != null) {
        hiltViewModel(parentEntry)
    } else {
        hiltViewModel()
    }
}

fun safeGetBackStackEntry(
    navController: NavHostController,
    route: String?,
): NavBackStackEntry? {
    if (route == null) return null
    return try {
        navController.getBackStackEntry(route)
    } catch (e: IllegalArgumentException) {
        Timber.e(e, "BackStackEntry not found for route: $route")
        null
    }
}
