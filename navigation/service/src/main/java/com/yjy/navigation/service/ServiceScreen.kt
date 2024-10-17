package com.yjy.navigation.service

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.CustomSnackbarHost
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.navigation.service.component.ServiceBottomBar
import com.yjy.navigation.service.navigation.MainTab
import com.yjy.navigation.service.navigation.ServiceNavController
import com.yjy.navigation.service.navigation.ServiceNavHost
import com.yjy.navigation.service.navigation.rememberServiceNavController
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun ServiceScreen(
    navigateToAuth: () -> Unit,
    onShowToast: (String) -> Unit,
    viewModel: ServiceViewModel = hiltViewModel(),
    navigator: ServiceNavController = rememberServiceNavController(),
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

    val sessionExpiredMessage = stringResource(id = R.string.navigation_service_session_expired)
    ObserveAsEvents(flow = viewModel.uiEvent) {
        when (it) {
            ServiceUiEvent.SessionExpired -> {
                onShowToast(sessionExpiredMessage)
                navigateToAuth()
            }
        }
    }

    ChallengeTogetherBackground {
        Scaffold(
            bottomBar = {
                ServiceBottomBar(
                    visible = navigator.isOnMainTab(),
                    mainTabs = MainTab.entries.toImmutableList(),
                    currentTab = navigator.currentTab,
                    onTabSelected = { navigator.navigateToMainTab(it) },
                )
            },
            floatingActionButton = {
                if (navigator.currentTab == MainTab.HOME) {
                    FloatingActionButton(
                        onClick = { navigator.navigateToAddChallenge() },
                        containerColor = CustomColorProvider.colorScheme.brand,
                        contentColor = CustomColorProvider.colorScheme.onBrand,
                        shape = MaterialTheme.shapes.extraLarge,
                    ) {
                        Icon(
                            painter = painterResource(id = ChallengeTogetherIcons.Add),
                            contentDescription = stringResource(id = R.string.navigation_service_add_new_challenge),
                        )
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            containerColor = Color.Transparent,
            snackbarHost = { CustomSnackbarHost(snackbarHostState) },
        ) { padding ->
            Box(
                modifier = Modifier.padding(padding),
            ) {
                ServiceNavHost(
                    navController = navigator.navController,
                    navigateToAuth = navigateToAuth,
                    onShowToast = onShowToast,
                    onShowSnackbar = showSnackbar,
                )
            }
        }
    }
}
