package com.yjy.feature.signup.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.yjy.core.common.constants.AnimationConst.DEFAULT_ANIMATION_DURATION
import com.yjy.core.navigation.Route
import com.yjy.feature.signup.EmailPasswordRoute
import com.yjy.feature.signup.NicknameRoute
import com.yjy.feature.signup.R

typealias SignUpStrings = R.string

fun NavController.navigateToSignUpEmailPassword() {
    navigate(Route.SignUp.EmailPassword)
}

fun NavGraphBuilder.signUpNavGraph(
    onBackClick: () -> Unit,
) {
    navigation<Route.SignUp>(
        startDestination = Route.SignUp.EmailPassword::class,
    ) {
        composable<Route.SignUp.EmailPassword>(
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(DEFAULT_ANIMATION_DURATION),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(DEFAULT_ANIMATION_DURATION),
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                )
            },
        ) {
            EmailPasswordRoute(onBackClick = onBackClick)
        }

        composable<Route.SignUp.Nickname> {
            NicknameRoute()
        }
    }
}
