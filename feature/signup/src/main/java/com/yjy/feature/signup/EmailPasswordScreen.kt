package com.yjy.feature.signup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.core.common.constants.AuthConst.MIN_PASSWORD_LENGTH
import com.yjy.core.common.ui.ObserveAsEvents
import com.yjy.core.designsystem.component.ChallengeTogetherBackground
import com.yjy.core.designsystem.component.ChallengeTogetherBottomAppBar
import com.yjy.core.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.core.designsystem.component.PasswordTextField
import com.yjy.core.designsystem.component.SingleLineTextField
import com.yjy.core.designsystem.component.SnackbarType
import com.yjy.core.designsystem.component.TitleWithDescription
import com.yjy.core.designsystem.icon.ChallengeTogetherIcons
import com.yjy.core.designsystem.theme.ChallengeTogetherTheme
import com.yjy.core.designsystem.theme.CustomColorProvider
import com.yjy.core.ui.DevicePreviews
import com.yjy.feature.signup.model.SignUpUiAction
import com.yjy.feature.signup.model.SignUpUiEvent
import com.yjy.feature.signup.model.SignUpUiState
import com.yjy.feature.signup.navigation.SignUpStrings
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
    val duplicatedEmailMessage = stringResource(id = SignUpStrings.feature_signup_email_already_registered)
    val checkNetworkMessage = stringResource(id = SignUpStrings.feature_signup_check_network_connection)
    val unknownErrorMessage = stringResource(id = SignUpStrings.feature_signup_error)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            is SignUpUiEvent.EmailPasswordVerified -> onContinue()

            is SignUpUiEvent.EmailPasswordVerifyFailure.DuplicatedEmail ->
                onShowSnackbar(SnackbarType.ERROR, duplicatedEmailMessage)

            is SignUpUiEvent.EmailPasswordVerifyFailure.NetworkError ->
                onShowSnackbar(SnackbarType.ERROR, checkNetworkMessage)

            is SignUpUiEvent.EmailPasswordVerifyFailure.UnknownError ->
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
                titleRes = SignUpStrings.feature_signup_title,
                descriptionRes = SignUpStrings.feature_signup_description,
            )
            Spacer(modifier = Modifier.height(50.dp))
            SignUpEmailTextField(
                value = uiState.email,
                onValueChange = { processAction(SignUpUiAction.OnEmailUpdated(it)) },
                enabled = !uiState.isValidatingEmail,
            )
            ConditionIndicator(
                text = stringResource(id = SignUpStrings.feature_signup_email_format_indicator),
                isMatched = uiState.isValidEmailFormat,
            )
            Spacer(modifier = Modifier.height(24.dp))
            SignUpPasswordTextField(
                value = uiState.password,
                onValueChange = { processAction(SignUpUiAction.OnPasswordUpdated(it)) },
                enabled = !uiState.isValidatingEmail,
            )
            ConditionIndicator(
                text = stringResource(id = SignUpStrings.feature_signup_password_length_indicator, MIN_PASSWORD_LENGTH),
                isMatched = uiState.isPasswordLongEnough,
            )
            ConditionIndicator(
                text = stringResource(id = SignUpStrings.feature_signup_password_number_indicator),
                isMatched = uiState.isPasswordContainNumber,
            )
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
        placeholderText = stringResource(id = SignUpStrings.feature_signup_input_email_place_holder),
    )
}

@Composable
private fun SignUpPasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
) {
    PasswordTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
        ),
        placeholderText = stringResource(id = SignUpStrings.feature_signup_input_password_place_holder),
    )
}

@Composable
private fun ConditionIndicator(
    text: String,
    isMatched: Boolean,
    modifier: Modifier = Modifier,
    unMatchColor: Color = CustomColorProvider.colorScheme.disable,
    matchColor: Color = CustomColorProvider.colorScheme.brand,
) {
    val icon = if (isMatched) ChallengeTogetherIcons.Check else ChallengeTogetherIcons.UnCheck
    val color = if (isMatched) matchColor else unMatchColor

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(top = 8.dp),
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
        )
    }
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