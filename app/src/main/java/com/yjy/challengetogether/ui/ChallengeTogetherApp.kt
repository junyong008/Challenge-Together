package com.yjy.challengetogether.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.yjy.challengetogether.navigation.ChallengeTogetherNavHost
import com.yjy.core.designsystem.component.ChallengeTogetherBackground
import com.yjy.core.designsystem.component.CustomSnackbarHost

@Composable
fun ChallengeTogetherApp(
    appState: ChallengeTogetherAppState = rememberChallengeTogetherAppState(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    ChallengeTogetherBackground {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { CustomSnackbarHost(snackbarHostState) },
        ) { padding ->
            Box(
                modifier = Modifier.padding(padding),
            ) {
                ChallengeTogetherNavHost(
                    appState = appState,
                    onShowSnackbar = { type, message ->
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(
                            message = message,
                            actionLabel = type.name,
                            duration = SnackbarDuration.Short
                        )
                    }
                )
            }
        }
    }
}