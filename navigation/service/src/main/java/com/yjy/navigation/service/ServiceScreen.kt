package com.yjy.navigation.service

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.yjy.common.core.constants.UrlConst.PLAY_STORE
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.core.util.formatLocalDateTime
import com.yjy.common.designsystem.component.BanDialog
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherDialog
import com.yjy.common.designsystem.component.CustomSnackbarHost
import com.yjy.common.designsystem.component.MaintenanceDialog
import com.yjy.common.designsystem.component.PremiumDialog
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.extensions.getDisplayNameResId
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.ManualTimeWarning
import com.yjy.model.common.Version
import com.yjy.navigation.service.component.NetworkTopBar
import com.yjy.navigation.service.component.ServiceBottomBar
import com.yjy.navigation.service.navigation.MainTab
import com.yjy.navigation.service.navigation.ServiceNavController
import com.yjy.navigation.service.navigation.ServiceNavHost
import com.yjy.navigation.service.navigation.rememberServiceNavController
import com.yjy.navigation.service.util.AdType
import com.yjy.platform.widget.WidgetManager
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("HardwareIds")
@Composable
internal fun ServiceScreen(
    navigateToAuth: () -> Unit,
    onShowToast: (String) -> Unit,
    onShowAd: (AdType) -> Unit,
    onFinishApp: () -> Unit,
    viewModel: ServiceViewModel = hiltViewModel(),
    navigator: ServiceNavController = rememberServiceNavController(),
    snackbarScope: CoroutineScope = rememberCoroutineScope(),
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val banStatus by viewModel.banStatus.collectAsStateWithLifecycle()
    val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()
    val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val isManualTime by viewModel.isManualTime.collectAsStateWithLifecycle()
    val remoteAppVersion by viewModel.remoteAppVersion.collectAsStateWithLifecycle()
    val maintenanceEndTime by viewModel.maintenanceEndTime.collectAsStateWithLifecycle()
    val isSessionExpired by viewModel.isSessionExpired.collectAsStateWithLifecycle()
    val sessionExpiredMessage = stringResource(id = R.string.navigation_service_session_expired)

    var shouldShowPremiumDialog by rememberSaveable { mutableStateOf(false) }

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

    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            if (viewModel.isPinSet.first()) {
                navigator.navigateToAppLockPinValidation()
            }
        }
    }

    LaunchedEffect(isOffline) {
        if (!isOffline) {
            val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            viewModel.checkBan(identifier = androidId)
        }
    }

    LaunchedEffect(Unit) {
        shouldShowPremiumDialog = viewModel.shouldShowPremiumDialog()
    }

    if (banStatus != null) {
        BanDialog(
            banReason = stringResource(id = banStatus!!.reason.getDisplayNameResId()),
            banEndTime = formatLocalDateTime(banStatus!!.endAt),
            onDismiss = onFinishApp,
        )
    }

    if (remoteAppVersion != null && currentVersion != null && currentVersion < remoteAppVersion!!) {
        ChallengeTogetherDialog(
            title = stringResource(id = R.string.navigation_service_update),
            description = stringResource(id = R.string.navigation_service_update_description),
            positiveTextRes = R.string.navigation_service_update,
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

    if (shouldShowPremiumDialog) {
        PremiumDialog(
            title = stringResource(id = R.string.navigation_service_premium_dialog_title),
            description = stringResource(id = R.string.navigation_service_premium_dialog_description),
            onExploreClick = {
                shouldShowPremiumDialog = false
                viewModel.markPremiumDialogShown()
                navigator.navigateToPremium()
            },
            onDismiss = {
                shouldShowPremiumDialog = false
                viewModel.markPremiumDialogShown()
            },
        )
    }

    ObserveAsEvents(flow = viewModel.sessionExpiredEvent) {
        onShowToast(sessionExpiredMessage)
    }

    LaunchedEffect(isLoggedIn, isSessionExpired) {
        if (!isLoggedIn || isSessionExpired) {
            WidgetManager.updateAllWidgets(context)
            navigateToAuth()
        }
    }

    ChallengeTogetherBackground {
        NotificationPermissionEffect()
        Scaffold(
            topBar = {
                Box(modifier = Modifier.statusBarsPadding()) {
                    AnimatedVisibility(
                        visible = isOffline,
                        enter = expandVertically(),
                        exit = shrinkVertically(),
                    ) {
                        NetworkTopBar()
                    }
                }
            },
            bottomBar = {
                Box(modifier = Modifier.navigationBarsPadding()) {
                    AnimatedVisibility(
                        visible = navigator.isOnMainTab(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        ServiceBottomBar(
                            mainTabs = MainTab.entries.toImmutableList(),
                            currentTab = navigator.currentTab,
                            onTabSelected = { navigator.navigateToMainTab(it) },
                        )
                    }
                }
            },
            floatingActionButton = {
                when (navigator.currentTab) {
                    MainTab.HOME -> {
                        FloatingActionButton(
                            onClick = { navigator.navigateToAddChallenge() },
                            containerColor = CustomColorProvider.colorScheme.brand,
                            contentColor = CustomColorProvider.colorScheme.onBrand,
                            shape = CircleShape,
                        ) {
                            Icon(
                                ImageVector.vectorResource(id = ChallengeTogetherIcons.Add),
                                contentDescription = stringResource(id = R.string.navigation_service_add_new_challenge),
                            )
                        }
                    }

                    MainTab.COMMUNITY -> {
                        FloatingActionButton(
                            onClick = { navigator.navigateToAddCommunityPost() },
                            containerColor = CustomColorProvider.colorScheme.brand,
                            contentColor = CustomColorProvider.colorScheme.onBrand,
                            shape = CircleShape,
                        ) {
                            Icon(
                                ImageVector.vectorResource(id = ChallengeTogetherIcons.Edit),
                                contentDescription = stringResource(
                                    id = R.string.navigation_service_add_new_community_post,
                                ),
                            )
                        }
                    }

                    else -> Unit
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            containerColor = Color.Transparent,
            snackbarHost = { CustomSnackbarHost(snackbarHostState) },
            modifier = Modifier.animateContentSize(),
        ) { padding ->
            Box(
                modifier = Modifier.padding(padding),
            ) {
                ServiceNavHost(
                    navController = navigator.navController,
                    onShowToast = onShowToast,
                    onShowAd = { adType ->
                        if (!isPremium) { onShowAd(adType) }
                    },
                    onShowSnackbar = showSnackbar,
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
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun NotificationPermissionEffect() {
    if (LocalInspectionMode.current) return
    if (VERSION.SDK_INT < VERSION_CODES.TIRAMISU) return

    val notificationsPermissionState = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS,
    )

    LaunchedEffect(notificationsPermissionState) {
        val status = notificationsPermissionState.status
        if (status is PermissionStatus.Denied && !status.shouldShowRationale) {
            notificationsPermissionState.launchPermissionRequest()
        }
    }
}
