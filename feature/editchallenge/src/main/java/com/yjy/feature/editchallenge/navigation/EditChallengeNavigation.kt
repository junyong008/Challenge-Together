package com.yjy.feature.editchallenge.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.yjy.common.core.util.NavigationAnimation.slideInToLeft
import com.yjy.common.core.util.NavigationAnimation.slideOutToRight
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.navigation.ServiceRoute
import com.yjy.feature.editchallenge.EditCategoryRoute
import com.yjy.feature.editchallenge.EditTargetDayRoute
import com.yjy.feature.editchallenge.EditTitleDescriptionRoute
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.TargetDays

fun NavController.navigateToEditCategory(
    challengeId: Int,
    category: Category,
) {
    navigate(ServiceRoute.EditCategory(challengeId, category))
}

fun NavController.navigateToEditTitleDescription(
    challengeId: Int,
    title: String,
    description: String,
) {
    navigate(ServiceRoute.EditTitleDescription(challengeId, title, description))
}

fun NavController.navigateToEditTargetDays(
    challengeId: Int,
    targetDays: String,
    currentDays: Int,
) {
    navigate(ServiceRoute.EditTargetDays(challengeId, targetDays, currentDays))
}

fun NavGraphBuilder.editChallengeScreen(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
) {
    composable<ServiceRoute.EditCategory>(
        enterTransition = { slideInToLeft() },
        popExitTransition = { slideOutToRight() },
    ) { entry ->
        val challengeId = entry.toRoute<ServiceRoute.EditCategory>().challengeId
        val category = entry.toRoute<ServiceRoute.EditCategory>().category

        EditCategoryRoute(
            challengeId = challengeId,
            selectedCategory = category,
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar,
        )
    }

    composable<ServiceRoute.EditTitleDescription>(
        enterTransition = { slideInToLeft() },
        popExitTransition = { slideOutToRight() },
    ) { entry ->
        val challengeId = entry.toRoute<ServiceRoute.EditTitleDescription>().challengeId
        val title = entry.toRoute<ServiceRoute.EditTitleDescription>().title
        val description = entry.toRoute<ServiceRoute.EditTitleDescription>().description

        EditTitleDescriptionRoute(
            challengeId = challengeId,
            title = title,
            description = description,
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar,
        )
    }

    composable<ServiceRoute.EditTargetDays>(
        enterTransition = { slideInToLeft() },
        popExitTransition = { slideOutToRight() },
    ) { entry ->
        val challengeId = entry.toRoute<ServiceRoute.EditTargetDays>().challengeId
        val targetDays = entry.toRoute<ServiceRoute.EditTargetDays>().targetDays
        val currentDays = entry.toRoute<ServiceRoute.EditTargetDays>().currentDays

        EditTargetDayRoute(
            challengeId = challengeId,
            targetDays = TargetDays.fromRouteString(targetDays),
            currentRecordDays = currentDays,
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
