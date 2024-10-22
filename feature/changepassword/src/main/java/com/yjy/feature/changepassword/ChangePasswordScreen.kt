package com.yjy.feature.changepassword

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.constants.AuthConst.MIN_PASSWORD_LENGTH
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherButton
import com.yjy.common.designsystem.component.ChallengeTogetherDialog
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.ConditionIndicator
import com.yjy.common.designsystem.component.SingleLineTextField
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.StableImage
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.changepassword.model.ChangePasswordUiAction
import com.yjy.feature.changepassword.model.ChangePasswordUiEvent
import com.yjy.feature.changepassword.model.ChangePasswordUiState
import com.yjy.feature.changepassword.navigation.ChangePasswordStrings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun ChangePasswordRoute(
    onBackClick: () -> Unit,
    onPasswordChanged: () -> Unit,
    onShowToast: (String) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChangePasswordViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ChangePasswordScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onPasswordChanged = onPasswordChanged,
        onShowToast = onShowToast,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun ChangePasswordScreen(
    modifier: Modifier = Modifier,
    uiState: ChangePasswordUiState = ChangePasswordUiState(),
    uiEvent: Flow<ChangePasswordUiEvent> = flowOf(),
    processAction: (ChangePasswordUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onPasswordChanged: () -> Unit = {},
    onShowToast: (String) -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val changeSuccessMessage = stringResource(id = ChangePasswordStrings.feature_changepassword_success)
    val checkNetworkMessage = stringResource(id = ChangePasswordStrings.feature_changepassword_check_network_connection)
    val unknownErrorMessage = stringResource(id = ChangePasswordStrings.feature_changepassword_error)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            ChangePasswordUiEvent.CancelChangePassword -> onBackClick()

            ChangePasswordUiEvent.ChangeSuccess -> {
                onShowToast(changeSuccessMessage)
                onPasswordChanged()
            }

            ChangePasswordUiEvent.Failure.NetworkError ->
                onShowSnackbar(SnackbarType.ERROR, checkNetworkMessage)

            ChangePasswordUiEvent.Failure.UnknownError ->
                onShowSnackbar(SnackbarType.ERROR, unknownErrorMessage)
        }
    }

    BackHandler { processAction(ChangePasswordUiAction.OnBackClick) }

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                onNavigationClick = { processAction(ChangePasswordUiAction.OnBackClick) },
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier,
    ) { padding ->

        if (uiState.shouldShowExitConfirmDialog) {
            ChallengeTogetherDialog(
                title = stringResource(id = ChangePasswordStrings.feature_changepassword_back),
                description = stringResource(id = ChangePasswordStrings.feature_changepassword_cancel_prompt),
                positiveTextRes = ChangePasswordStrings.feature_changepassword_back,
                onClickPositive = { processAction(ChangePasswordUiAction.OnConfirmExit) },
                onClickNegative = { processAction(ChangePasswordUiAction.OnCancelExit) },
            )
        }

        if (uiState.shouldShowChangeConfirmDialog) {
            ChallengeTogetherDialog(
                title = stringResource(id = ChangePasswordStrings.feature_changepassword_title),
                description = stringResource(id = ChangePasswordStrings.feature_changepassword_confirm_info),
                positiveTextRes = ChangePasswordStrings.feature_changepassword_change,
                onClickPositive = { processAction(ChangePasswordUiAction.OnConfirmChange(uiState.password)) },
                onClickNegative = { processAction(ChangePasswordUiAction.OnCancelChange) },
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
        ) {
            TitleWithDescription(
                titleRes = ChangePasswordStrings.feature_changepassword_title,
                descriptionRes = ChangePasswordStrings.feature_changepassword_description,
            )
            Spacer(modifier = Modifier.height(35.dp))
            StableImage(
                drawableResId = R.drawable.image_lock,
                descriptionResId = ChangePasswordStrings.feature_changepassword_lock_image_description,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(115.dp),
            )
            Spacer(modifier = Modifier.height(35.dp))
            SingleLineTextField(
                value = uiState.password,
                onValueChange = { processAction(ChangePasswordUiAction.OnPasswordUpdated(it)) },
                enabled = !uiState.isChangingPassword,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                placeholderText = stringResource(
                    id = ChangePasswordStrings.feature_changepassword_input_password_place_holder,
                ),
                isPassword = true,
            )
            ConditionIndicator(
                text = stringResource(
                    id = ChangePasswordStrings.feature_changepassword_password_length_indicator,
                    MIN_PASSWORD_LENGTH,
                ),
                isMatched = uiState.isPasswordLongEnough,
            )
            ConditionIndicator(
                text = stringResource(id = ChangePasswordStrings.feature_changepassword_password_number_indicator),
                isMatched = uiState.isPasswordContainNumber,
            )
            Spacer(modifier = Modifier.height(16.dp))
            ChallengeTogetherButton(
                onClick = {
                    processAction(ChangePasswordUiAction.OnChangeClick)
                },
                enabled = !uiState.isChangingPassword &&
                    uiState.isPasswordLongEnough &&
                    uiState.isPasswordContainNumber,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isChangingPassword) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp),
                        color = CustomColorProvider.colorScheme.brand,
                    )
                } else {
                    Text(
                        text = stringResource(id = ChangePasswordStrings.feature_changepassword_confirm),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@DevicePreviews
@Composable
fun ChangePasswordScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            ChangePasswordScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
