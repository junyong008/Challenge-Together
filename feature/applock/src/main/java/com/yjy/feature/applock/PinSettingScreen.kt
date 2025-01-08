package com.yjy.feature.applock

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
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
internal fun PinSettingRoute(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AppLockViewModel = hiltViewModel(),
) {
    PinSettingScreen(
        modifier = modifier,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun PinSettingScreen(
    modifier: Modifier = Modifier,
    uiEvent: Flow<AppLockUiEvent> = flowOf(),
    processAction: (AppLockUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current

    var pin by rememberSaveable { mutableStateOf("") }
    var firstPin by rememberSaveable { mutableStateOf<String?>(null) }
    var isConfirmStep by rememberSaveable { mutableStateOf(false) }

    val pinMismatchMessage = stringResource(id = R.string.feature_applock_password_mismatch)
    val pinSetSuccessMessage = stringResource(id = R.string.feature_applock_password_set_success)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            is AppLockUiEvent.OnPinMismatch -> {
                context.vibrate()
                onShowSnackbar(SnackbarType.ERROR, pinMismatchMessage)
            }

            is AppLockUiEvent.OnPinSetSuccess -> {
                onShowSnackbar(SnackbarType.SUCCESS, pinSetSuccessMessage)
                onBackClick()
            }

            else -> Unit
        }
    }

    PinInput(
        title = stringResource(
            id = if (isConfirmStep) {
                R.string.feature_applock_reenter_password
            } else {
                R.string.feature_applock_enter_password
            },
        ),
        pin = pin,
        onPinChanged = { pin = it },
        onClose = onBackClick,
        onPinComplete = { completedPin ->
            if (!isConfirmStep) {
                firstPin = completedPin
                isConfirmStep = true
                pin = ""
            } else {
                if (pin == firstPin) {
                    processAction(AppLockUiAction.OnSetPin(pin))
                } else {
                    processAction(AppLockUiAction.OnPinMismatch)
                    firstPin = null
                    isConfirmStep = false
                    pin = ""
                }
            }
        },
        modifier = modifier,
    )
}

@DevicePreviews
@Composable
fun PinSettingScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            PinSettingScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
