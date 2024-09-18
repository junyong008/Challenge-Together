package com.yjy.challengetogether.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.yjy.challengetogether.navigation.ChallengeTogetherNavHost
import com.yjy.core.designsystem.component.ChallengeTogetherBackground
import com.yjy.core.designsystem.component.ChallengeTogetherGradientBackground
import com.yjy.core.designsystem.theme.GradientColors
import com.yjy.core.designsystem.theme.LocalGradientColors
import com.yjy.feature.login.navigation.LOGIN_ROUTE

@Composable
fun ChallengeTogetherApp(
    appState: ChallengeTogetherAppState = rememberChallengeTogetherAppState(),
) {
    val shouldShowGradientBackground = appState.currentDestination?.route == LOGIN_ROUTE

    ChallengeTogetherBackground {
        ChallengeTogetherGradientBackground(
            gradientColors = if (shouldShowGradientBackground) {
                LocalGradientColors.current
            } else {
                GradientColors()
            },
        ) {
            Scaffold(
                containerColor = Color.Transparent,
            ) { padding ->
                Box(
                    modifier = Modifier.padding(padding),
                ) {
                    ChallengeTogetherNavHost(
                        appState = appState,
                    )
                }
            }
        }
    }
}