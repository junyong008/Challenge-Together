package com.yjy.navigation.auth

import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.yjy.common.core.constants.UrlConst.PLAY_STORE
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.formatLocalDateTime
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherDialog
import com.yjy.common.designsystem.component.CustomSnackbarHost
import com.yjy.common.designsystem.component.MaintenanceDialog
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.ui.ManualTimeWarning
import com.yjy.model.common.Version
import com.yjy.navigation.auth.navigation.AuthNavHost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun AuthScreen(
    navigateToService: () -> Unit,
    onShowToast: (String) -> Unit,
    onFinishApp: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    snackbarScope: CoroutineScope = rememberCoroutineScope(),
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val isManualTime by viewModel.isManualTime.collectAsStateWithLifecycle()
    val remoteAppVersion by viewModel.remoteAppVersion.collectAsStateWithLifecycle()
    val maintenanceEndTime by viewModel.maintenanceEndTime.collectAsStateWithLifecycle()

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

    val currentVersion = remember {
        val packageInfo = if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
        packageInfo.versionName?.let { Version(it) }
    }

    if (remoteAppVersion != null && currentVersion != null && currentVersion < remoteAppVersion!!) {
        ChallengeTogetherDialog(
            title = stringResource(id = R.string.navigation_auth_update),
            description = stringResource(id = R.string.navigation_auth_update_description),
            positiveTextRes = R.string.navigation_auth_update,
            onClickPositive = { uriHandler.openUri(PLAY_STORE) },
            onClickNegative = onFinishApp,
        )
    }

    if (maintenanceEndTime != null) {
        MaintenanceDialog(
            expectedCompletionTime = formatLocalDateTime(maintenanceEndTime!!),
            onDismiss = onFinishApp,
        )
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
            AnimatedVisibility(
                visible = isManualTime,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
            ) {
                ManualTimeWarning()
            }
        }
    }
}
