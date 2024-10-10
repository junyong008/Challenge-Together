package com.yjy.feature.findpassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.core.common.ui.ObserveAsEvents
import com.yjy.core.designsystem.component.ChallengeTogetherBackground
import com.yjy.core.designsystem.component.ChallengeTogetherButton
import com.yjy.core.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.core.designsystem.component.SingleLineTextField
import com.yjy.core.designsystem.component.SnackbarType
import com.yjy.core.designsystem.component.TitleWithDescription
import com.yjy.core.designsystem.theme.ChallengeTogetherTheme
import com.yjy.core.designsystem.theme.CustomColorProvider
import com.yjy.core.ui.DevicePreviews
import com.yjy.feature.findpassword.model.FindPasswordUiAction
import com.yjy.feature.findpassword.model.FindPasswordUiEvent
import com.yjy.feature.findpassword.model.FindPasswordUiState
import com.yjy.feature.findpassword.navigation.FindPasswordStrings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun InputEmailRoute(
    onBackClick: () -> Unit,
    onVerifyCodeSent: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FindPasswordViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InputEmailScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onVerifyCodeSent = onVerifyCodeSent,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun InputEmailScreen(
    modifier: Modifier = Modifier,
    uiState: FindPasswordUiState = FindPasswordUiState(),
    uiEvent: Flow<FindPasswordUiEvent> = flowOf(),
    processAction: (FindPasswordUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onVerifyCodeSent: () -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val verifyCodeSentMessage = stringResource(id = FindPasswordStrings.feature_findpassword_code_sent)
    val invalidEmailMessage = stringResource(id = FindPasswordStrings.feature_findpassword_invalid_email)
    val unRegisteredEmailMessage = stringResource(id = FindPasswordStrings.feature_findpassword_email_not_registered)
    val checkNetworkMessage = stringResource(id = FindPasswordStrings.feature_findpassword_check_network_connection)
    val unknownErrorMessage = stringResource(id = FindPasswordStrings.feature_findpassword_unknown_error)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            FindPasswordUiEvent.VerifyCodeSent -> {
                onShowSnackbar(SnackbarType.SUCCESS, verifyCodeSentMessage)
                onVerifyCodeSent()
            }

            FindPasswordUiEvent.SendVerifyCodeFailure.UnregisteredEmail ->
                onShowSnackbar(SnackbarType.ERROR, unRegisteredEmailMessage)

            FindPasswordUiEvent.SendVerifyCodeFailure.InvalidEmail ->
                onShowSnackbar(SnackbarType.ERROR, invalidEmailMessage)

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
            TitleWithDescription(
                titleRes = FindPasswordStrings.feature_findpassword_title,
                descriptionRes = FindPasswordStrings.feature_findpassword_description,
            )
            Spacer(modifier = Modifier.height(50.dp))
            SingleLineTextField(
                value = uiState.email,
                onValueChange = { processAction(FindPasswordUiAction.OnEmailUpdated(it)) },
                enabled = !uiState.isSendingVerifyCode,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                placeholderText = stringResource(
                    id = FindPasswordStrings.feature_findpassword_input_email_place_holder,
                ),
            )
            if (!uiState.isValidEmailFormat) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = FindPasswordStrings.feature_findpassword_invalid_email_format),
                    color = CustomColorProvider.colorScheme.red,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.Start),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            ChallengeTogetherButton(
                onClick = {
                    processAction(
                        FindPasswordUiAction.OnSendVerifyCodeClick(email = uiState.email),
                    )
                },
                enabled = uiState.canTrySendVerifyCode && !uiState.isSendingVerifyCode,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
            ) {
                if (uiState.isSendingVerifyCode) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp),
                        color = CustomColorProvider.colorScheme.brand,
                    )
                } else {
                    Text(
                        text = stringResource(id = FindPasswordStrings.feature_findpassword_send_verify_code),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@DevicePreviews
@Composable
fun InputEmailScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            InputEmailScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
