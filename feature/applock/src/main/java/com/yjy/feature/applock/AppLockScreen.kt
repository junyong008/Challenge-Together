package com.yjy.feature.applock

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.extensions.clickableSingle
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherSwitch
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.applock.model.AppLockUiAction
import com.yjy.platform.widget.WidgetManager

@Composable
internal fun AppLockRoute(
    onBackClick: () -> Unit,
    onSetPinClick: () -> Unit,
    onChangePinClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AppLockViewModel = hiltViewModel(),
) {
    val isPinSet by viewModel.isPinSet.collectAsStateWithLifecycle()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsStateWithLifecycle()
    val shouldHideWidgetContents by viewModel.shouldHideWidgetContents.collectAsStateWithLifecycle()
    val shouldHideNotificationContents by viewModel.shouldHideNotificationContents.collectAsStateWithLifecycle()

    AppLockScreen(
        modifier = modifier,
        isPinSet = isPinSet,
        isBiometricEnabled = isBiometricEnabled,
        shouldHideWidgetContents = shouldHideWidgetContents,
        shouldHideNotificationContents = shouldHideNotificationContents,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onSetPinClick = onSetPinClick,
        onChangePinClick = onChangePinClick,
    )
}

@Composable
internal fun AppLockScreen(
    modifier: Modifier = Modifier,
    isPinSet: Boolean = false,
    isBiometricEnabled: Boolean = false,
    shouldHideWidgetContents: Boolean = false,
    shouldHideNotificationContents: Boolean = false,
    processAction: (AppLockUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onSetPinClick: () -> Unit = {},
    onChangePinClick: () -> Unit = {},
) {
    val context = LocalContext.current

    LaunchedEffect(shouldHideWidgetContents) {
        WidgetManager.updateAllWidgets(context)
    }

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                onNavigationClick = onBackClick,
                titleRes = R.string.feature_applock_title,
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier.consumeWindowInsets(WindowInsets.navigationBars),
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SettingItem(
                iconResId = ChallengeTogetherIcons.Lock,
                titleResId = R.string.feature_applock_password,
                checked = isPinSet,
                onCheckedChange = {
                    if (isPinSet) {
                        processAction(AppLockUiAction.OnRemovePin)
                    } else {
                        onSetPinClick()
                    }
                },
            )
            if (isPinSet) {
                SettingItem(
                    iconResId = ChallengeTogetherIcons.FingerPrint,
                    titleResId = R.string.feature_applock_biometric_authentication,
                    checked = isBiometricEnabled,
                    enabled = context.canAuthenticateWithBiometric(),
                    onCheckedChange = {
                        processAction(AppLockUiAction.OnToggleBiometric(isBiometricEnabled))
                    },
                )
                ChangePasswordItem(onClick = onChangePinClick)
                HorizontalDivider(
                    thickness = 1.dp,
                    color = CustomColorProvider.colorScheme.divider,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
                SettingItem(
                    iconResId = ChallengeTogetherIcons.Hide,
                    titleResId = R.string.feature_applock_hide_widget_content,
                    checked = shouldHideWidgetContents,
                    onCheckedChange = {
                        processAction(AppLockUiAction.OnToggleHideWidget(shouldHideWidgetContents))
                    },
                )
                SettingItem(
                    iconResId = ChallengeTogetherIcons.NotificationOff,
                    titleResId = R.string.feature_applock_hide_notification_content,
                    checked = shouldHideNotificationContents,
                    onCheckedChange = {
                        processAction(AppLockUiAction.OnToggleHideNotification(shouldHideNotificationContents))
                    },
                )
            }
        }
    }
}

private fun Context.canAuthenticateWithBiometric(): Boolean {
    val biometricManager = BiometricManager.from(this)
    return biometricManager.canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
}

@Composable
private fun ChangePasswordItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(CustomColorProvider.colorScheme.surface)
            .clickableSingle { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = R.string.feature_applock_change_password),
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = ImageVector.vectorResource(id = ChallengeTogetherIcons.ArrowRight),
            contentDescription = stringResource(id = R.string.feature_applock_change_password),
            tint = CustomColorProvider.colorScheme.onSurface,
        )
    }
}

private const val ITEM_SWITCH_SCALE = 0.8f

@Composable
private fun SettingItem(
    @DrawableRes iconResId: Int,
    @StringRes titleResId: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(CustomColorProvider.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = iconResId),
            contentDescription = stringResource(id = titleResId),
            tint = CustomColorProvider.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(id = titleResId),
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(16.dp))
        ChallengeTogetherSwitch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            modifier = Modifier
                .scale(ITEM_SWITCH_SCALE)
                .align(Alignment.CenterVertically),
        )
    }
}

@DevicePreviews
@Composable
fun AppLockScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            AppLockScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
