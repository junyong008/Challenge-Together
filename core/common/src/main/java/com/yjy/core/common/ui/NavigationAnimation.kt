package com.yjy.core.common.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween

object NavigationAnimation {

    private const val DEFAULT_ANIMATION_DURATION = 650

    fun <T> AnimatedContentTransitionScope<T>.slideInToLeft(): EnterTransition {
        return slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(DEFAULT_ANIMATION_DURATION)
        )
    }

    fun <T> AnimatedContentTransitionScope<T>.slideInToRight(): EnterTransition {
        return slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(DEFAULT_ANIMATION_DURATION)
        )
    }

    fun <T> AnimatedContentTransitionScope<T>.slideOutToLeft(): ExitTransition {
        return slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(DEFAULT_ANIMATION_DURATION)
        )
    }

    fun <T> AnimatedContentTransitionScope<T>.slideOutToRight(): ExitTransition {
        return slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(DEFAULT_ANIMATION_DURATION)
        )
    }
}