package com.yjy.navigation.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.CustomSnackbarHost
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.navigation.auth.navigation.AuthNavHost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun AuthScreen(
    navigateToService: () -> Unit,
    onShowToast: (String) -> Unit,
    navController: NavHostController = rememberNavController(),
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
        ) {
            AuthNavHost(
                navigateToService = navigateToService,
                navController = navController,
                onShowToast = onShowToast,
                onShowSnackbar = showSnackbar,
            )
            CustomSnackbarHost(
                snackbarHostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}
