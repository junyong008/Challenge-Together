package com.yjy.challengetogether.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yjy.challengetogether.navigation.ChallengeTogetherNavHost
import com.yjy.core.designsystem.component.ChallengeTogetherBackground

@Composable
fun ChallengeTogetherApp(
    appState: ChallengeTogetherAppState = rememberChallengeTogetherAppState(),
) {
    ChallengeTogetherBackground {
        Scaffold(

        ) { padding ->
            Box(
                modifier = Modifier.padding(padding)
            ) {
                ChallengeTogetherNavHost(
                    appState = appState,
                )
            }
        }
    }
}