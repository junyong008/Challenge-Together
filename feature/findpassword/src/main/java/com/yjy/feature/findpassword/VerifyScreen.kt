package com.yjy.feature.findpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.findpassword.FindPasswordViewModel.Companion.VERIFY_CODE_LENGTH
import com.yjy.feature.findpassword.model.FindPasswordUiAction
import com.yjy.feature.findpassword.model.FindPasswordUiEvent
import com.yjy.feature.findpassword.model.FindPasswordUiState
import com.yjy.feature.findpassword.navigation.FindPasswordStrings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun VerifyRoute(
    onBackClick: () -> Unit,
    onVerifySuccess: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FindPasswordViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    VerifyScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onVerifySuccess = onVerifySuccess,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun VerifyScreen(
    modifier: Modifier = Modifier,
    uiState: FindPasswordUiState = FindPasswordUiState(),
    uiEvent: Flow<FindPasswordUiEvent> = flowOf(),
    processAction: (FindPasswordUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onVerifySuccess: () -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val verifySuccessMessage = stringResource(id = FindPasswordStrings.feature_findpassword_verification_success)
    val unMatchCodeMessage = stringResource(id = FindPasswordStrings.feature_findpassword_code_mismatch)
    val timeOutMessage = stringResource(id = FindPasswordStrings.feature_findpassword_timeout)
    val checkNetworkMessage = stringResource(id = FindPasswordStrings.feature_findpassword_check_network_connection)
    val unknownErrorMessage = stringResource(id = FindPasswordStrings.feature_findpassword_unknown_error)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            FindPasswordUiEvent.VerifySuccess -> {
                onShowSnackbar(SnackbarType.SUCCESS, verifySuccessMessage)
                onVerifySuccess()
            }

            FindPasswordUiEvent.VerifyFailure.UnMatchedCode ->
                onShowSnackbar(SnackbarType.ERROR, unMatchCodeMessage)

            FindPasswordUiEvent.VerifyFailure.Timeout -> {
                onShowSnackbar(SnackbarType.ERROR, timeOutMessage)
                onBackClick()
            }

            FindPasswordUiEvent.Failure.NetworkError ->
                onShowSnackbar(SnackbarType.ERROR, checkNetworkMessage)

            FindPasswordUiEvent.Failure.UnknownError ->
                onShowSnackbar(SnackbarType.ERROR, unknownErrorMessage)

            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                onNavigationClick = onBackClick,
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = stringResource(id = FindPasswordStrings.feature_findpassword_enter_code),
                style = MaterialTheme.typography.titleMedium,
                color = CustomColorProvider.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(50.dp))
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.CenterHorizontally),
            ) {
                VerifyCodeTextField(
                    value = uiState.verifyCode,
                    onValueChange = { processAction(FindPasswordUiAction.OnVerifyCodeUpdated(it)) },
                    enabled = !uiState.isVerifyingCode,
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (uiState.isVerifyingCode) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.End),
                        color = CustomColorProvider.colorScheme.brand,
                    )
                } else {
                    Text(
                        text = stringResource(
                            id = FindPasswordStrings.feature_findpassword_minutes_seconds,
                            uiState.minutes,
                            uiState.seconds,
                        ),
                        color = CustomColorProvider.colorScheme.red,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.End),
                    )
                }
            }
        }
    }
}

@Composable
private fun VerifyCodeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                value.forEachIndexed { index, char ->
                    VerifyCodeTextFieldContainer(
                        text = char,
                        isFocused = index == value.lastIndex,
                        enabled = enabled,
                    )
                }
                repeat(VERIFY_CODE_LENGTH - value.length) {
                    VerifyCodeTextFieldContainer(
                        text = ' ',
                        isFocused = false,
                        enabled = enabled,
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
        ),
    )
}

@Composable
private fun VerifyCodeTextFieldContainer(
    text: Char,
    isFocused: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(
                width = 40.dp,
                height = 55.dp,
            )
            .background(
                color = if (enabled) {
                    CustomColorProvider.colorScheme.surface
                } else {
                    CustomColorProvider.colorScheme.disable
                },
                shape = MaterialTheme.shapes.medium,
            )
            .run {
                if (isFocused && enabled) {
                    border(
                        width = 2.dp,
                        color = CustomColorProvider.colorScheme.brand,
                        shape = MaterialTheme.shapes.medium,
                    )
                } else {
                    this
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text.toString(),
            color = CustomColorProvider.colorScheme.onSurface,
        )
    }
}

@DevicePreviews
@Composable
fun VerifyScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            VerifyScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
