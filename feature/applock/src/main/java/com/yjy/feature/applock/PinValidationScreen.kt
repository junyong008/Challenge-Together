package com.yjy.feature.applock

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.applock.components.PinInput
import com.yjy.feature.applock.components.vibrate
import com.yjy.feature.applock.model.AppLockUiAction
import com.yjy.feature.applock.model.AppLockUiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun PinValidationRoute(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AppLockViewModel = hiltViewModel(),
) {
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsStateWithLifecycle()

    PinValidationScreen(
        modifier = modifier,
        isBiometricEnabled = isBiometricEnabled,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun PinValidationScreen(
    modifier: Modifier = Modifier,
    isBiometricEnabled: Boolean = false,
    uiEvent: Flow<AppLockUiEvent> = flowOf(),
    processAction: (AppLockUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    BiometricAuth(
        isBiometricEnabled = isBiometricEnabled,
        onSuccess = onBackClick,
    )

    var pin by rememberSaveable { mutableStateOf("") }
    val pinMismatchMessage = stringResource(id = R.string.feature_applock_password_mismatch)
    val onFinish: () -> Unit = { activity?.finish() }

    BackHandler(onBack = onFinish)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            AppLockUiEvent.OnPinValidationSuccess -> onBackClick()
            AppLockUiEvent.OnPinValidationFailure -> {
                pin = ""
                context.vibrate()
                onShowSnackbar(SnackbarType.ERROR, pinMismatchMessage)
            }

            else -> Unit
        }
    }

    PinInput(
        title = stringResource(id = R.string.feature_applock_enter_password),
        pin = pin,
        onPinChanged = { pin = it },
        onClose = onFinish,
        onPinComplete = { completedPin ->
            processAction(AppLockUiAction.OnValidatePin(completedPin))
        },
        modifier = modifier,
    )
}

@Composable
fun BiometricAuth(
    isBiometricEnabled: Boolean,
    onSuccess: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val fragmentActivity = activity as? FragmentActivity

    val biometricTitle = stringResource(R.string.feature_applock_biometric_authentication)
    val biometricCancel = stringResource(R.string.feature_applock_biometric_cancel)

    val biometricPrompt = remember(fragmentActivity) {
        fragmentActivity?.let { fragActivity ->
            BiometricPrompt(
                fragActivity,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        onSuccess()
                    }
                },
            )
        }
    }

    val promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(biometricTitle)
            .setNegativeButtonText(biometricCancel)
            .build()
    }

    LaunchedEffect(isBiometricEnabled) {
        if (isBiometricEnabled) {
            biometricPrompt?.authenticate(promptInfo)
        }
    }
}

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@DevicePreviews
@Composable
fun PinValidationScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            PinValidationScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
