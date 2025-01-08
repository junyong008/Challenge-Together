package com.yjy.feature.signup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.constants.AuthConst.MIN_PASSWORD_LENGTH
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherBottomAppBar
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.ConditionIndicator
import com.yjy.common.designsystem.component.SingleLineTextField
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.signup.model.SignUpUiAction
import com.yjy.feature.signup.model.SignUpUiEvent
import com.yjy.feature.signup.model.SignUpUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun EmailPasswordRoute(
    onBackClick: () -> Unit,
    onContinue: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EmailPasswordScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onContinue = onContinue,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun EmailPasswordScreen(
    modifier: Modifier = Modifier,
    uiState: SignUpUiState = SignUpUiState(),
    uiEvent: Flow<SignUpUiEvent> = flowOf(),
    processAction: (SignUpUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onContinue: () -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val duplicatedEmailMessage = stringResource(id = R.string.feature_signup_email_already_registered)
    val checkNetworkMessage = stringResource(id = R.string.feature_signup_check_network_connection)
    val unknownErrorMessage = stringResource(id = R.string.feature_signup_error)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            SignUpUiEvent.EmailPasswordVerified -> onContinue()

            SignUpUiEvent.DuplicatedEmail ->
                onShowSnackbar(SnackbarType.ERROR, duplicatedEmailMessage)

            SignUpUiEvent.Failure.NetworkError ->
                onShowSnackbar(SnackbarType.ERROR, checkNetworkMessage)

            SignUpUiEvent.Failure.UnknownError ->
                onShowSnackbar(SnackbarType.ERROR, unknownErrorMessage)

            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                onNavigationClick = onBackClick,
            )
        },
        bottomBar = {
            ChallengeTogetherBottomAppBar(
                showBackButton = false,
                enableContinueButton = uiState.canTryContinueToNickname,
                isLoading = uiState.isValidatingEmail,
                onContinueClick = {
                    processAction(SignUpUiAction.OnEmailPasswordContinueClick(uiState.email))
                },
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
                titleRes = R.string.feature_signup_title,
                descriptionRes = R.string.feature_signup_description,
            )
            Spacer(modifier = Modifier.height(50.dp))
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                SignUpEmailTextField(
                    value = uiState.email,
                    onValueChange = { processAction(SignUpUiAction.OnEmailUpdated(it)) },
                    enabled = !uiState.isValidatingEmail,
                )
                ConditionIndicator(
                    text = stringResource(id = R.string.feature_signup_email_format_indicator),
                    isMatched = uiState.isValidEmailFormat,
                )
                Spacer(modifier = Modifier.height(24.dp))
                SignUpPasswordTextField(
                    value = uiState.password,
                    onValueChange = { processAction(SignUpUiAction.OnPasswordUpdated(it)) },
                    enabled = !uiState.isValidatingEmail,
                )
                ConditionIndicator(
                    text = stringResource(
                        id = R.string.feature_signup_password_length_indicator,
                        MIN_PASSWORD_LENGTH,
                    ),
                    isMatched = uiState.isPasswordLongEnough,
                )
                ConditionIndicator(
                    text = stringResource(id = R.string.feature_signup_password_number_indicator),
                    isMatched = uiState.isPasswordContainNumber,
                )
            }
        }
    }
}

@Composable
private fun SignUpEmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
) {
    SingleLineTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
        ),
        placeholderText = stringResource(id = R.string.feature_signup_input_email_place_holder),
    )
}

@Composable
private fun SignUpPasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
) {
    SingleLineTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
        ),
        placeholderText = stringResource(id = R.string.feature_signup_input_password_place_holder),
        isPassword = true,
    )
}

@DevicePreviews
@Composable
fun EmailPasswordScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            EmailPasswordScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
