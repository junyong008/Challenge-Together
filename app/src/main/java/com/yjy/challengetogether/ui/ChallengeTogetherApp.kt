package com.yjy.challengetogether.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.yjy.challengetogether.component.MainBottomBar
import com.yjy.challengetogether.navigation.ChallengeTogetherNavHost
import com.yjy.challengetogether.navigation.MainTab
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.CustomSnackbarHost
import com.yjy.common.designsystem.component.SnackbarType
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun ChallengeTogetherApp(
    appState: ChallengeTogetherAppState = rememberChallengeTogetherAppState(),
    snackbarScope: CoroutineScope = rememberCoroutineScope(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val showSnackbar: (SnackbarType, String) -> Unit = { type, message ->
        snackbarScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = type.name,
                duration = SnackbarDuration.Short,
            )
        }
    }

    ChallengeTogetherBackground {
        Scaffold(
            bottomBar = {
                if (appState.isOnMainTab()) {
                    MainBottomBar(
                        mainTabs = MainTab.entries.toImmutableList(),
                        currentTab = appState.currentTab,
                        onTabSelected = { appState.navigateToMainTab(it) },
                    )
                }
            },
            containerColor = Color.Transparent,
            snackbarHost = { CustomSnackbarHost(snackbarHostState) },
        ) { padding ->
            Box(
                modifier = Modifier.padding(padding),
            ) {
                ChallengeTogetherNavHost(
                    appState = appState,
                    onShowSnackbar = showSnackbar,
                )
            }
        }
    }
}
