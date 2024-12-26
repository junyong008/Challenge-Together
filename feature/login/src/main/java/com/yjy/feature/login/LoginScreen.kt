package com.yjy.feature.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherButton
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.ClickableText
import com.yjy.common.designsystem.component.SingleLineTextField
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.login.model.LoginUiAction
import com.yjy.feature.login.model.LoginUiEvent
import com.yjy.feature.login.model.LoginUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun LoginRoute(
    onBackClick: () -> Unit,
    onShowToast: (String) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    onFindPasswordClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onLoginSuccess = onLoginSuccess,
        onSignUpClick = onSignUpClick,
        onFindPasswordClick = onFindPasswordClick,
        onShowToast = onShowToast,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun LoginScreen(
    modifier: Modifier = Modifier,
    uiState: LoginUiState = LoginUiState(),
    uiEvent: Flow<LoginUiEvent> = flowOf(),
    processAction: (LoginUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onFindPasswordClick: () -> Unit = {},
    onShowToast: (String) -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val loginSuccessMessage = stringResource(id = R.string.feature_login_login_success)
    val userNotFoundMessage = stringResource(id = R.string.feature_login_user_not_found)
    val checkNetworkMessage = stringResource(id = R.string.feature_login_check_network_connection)
    val unknownErrorMessage = stringResource(id = R.string.feature_login_unknown_error)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            LoginUiEvent.LoginSuccess -> {
                onShowToast(loginSuccessMessage)
                onLoginSuccess()
            }

            LoginUiEvent.LoginFailure.UserNotFound ->
                onShowSnackbar(SnackbarType.ERROR, userNotFoundMessage)

            LoginUiEvent.LoginFailure.NetworkError ->
                onShowSnackbar(SnackbarType.ERROR, checkNetworkMessage)

            LoginUiEvent.LoginFailure.UnknownError ->
                onShowSnackbar(SnackbarType.ERROR, unknownErrorMessage)
        }
    }

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                onNavigationClick = onBackClick,
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(CustomColorProvider.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Title(
                title = stringResource(id = R.string.feature_login_title),
                titleDescription = stringResource(id = R.string.feature_login_title_description),
                modifier = Modifier.align(Alignment.Start),
            )
            Spacer(modifier = Modifier.height(32.dp))
            LoginEmailTextField(
                value = uiState.email,
                onValueChange = { processAction(LoginUiAction.OnEmailUpdated(it)) },
                enabled = !uiState.isLoading,
            )
            if (!uiState.isValidEmailFormat) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.feature_login_invalid_email_format),
                    color = CustomColorProvider.colorScheme.red,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.Start),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LoginPasswordTextField(
                value = uiState.password,
                onValueChange = { processAction(LoginUiAction.OnPasswordUpdated(it)) },
                enabled = !uiState.isLoading,
            )
            Spacer(modifier = Modifier.height(16.dp))
            ChallengeTogetherButton(
                onClick = {
                    processAction(LoginUiAction.OnEmailLoginClick(uiState.email, uiState.password))
                },
                enabled = uiState.canTryLogin && !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("loginButton"),
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = CustomColorProvider.colorScheme.brand,
                        modifier = Modifier.size(24.dp),
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.feature_login_button_text),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            FindPasswordAndSignUp(
                onFindPasswordClick = onFindPasswordClick,
                onSignUpClick = onSignUpClick,
                enabled = !uiState.isLoading,
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun Title(
    title: String,
    titleDescription: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = titleDescription,
            style = MaterialTheme.typography.labelSmall,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = CustomColorProvider.colorScheme.onBackground,
        )
    }
}

@Composable
private fun LoginEmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
) {
    SingleLineTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(
                ImageVector.vectorResource(id = ChallengeTogetherIcons.Email),
                contentDescription = stringResource(id = R.string.feature_login_input_email_content_description),
                tint = CustomColorProvider.colorScheme.onSurface,
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
        ),
        placeholderText = stringResource(id = R.string.feature_login_input_email_place_holder),
        enabled = enabled,
    )
}

@Composable
private fun LoginPasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
) {
    SingleLineTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(
                ImageVector.vectorResource(id = ChallengeTogetherIcons.Lock),
                contentDescription = stringResource(id = R.string.feature_login_input_password_content_description),
                tint = CustomColorProvider.colorScheme.onSurface,
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
        ),
        placeholderText = stringResource(id = R.string.feature_login_input_password_place_holder),
        enabled = enabled,
        isPassword = true,
    )
}

@Composable
private fun FindPasswordAndSignUp(
    onFindPasswordClick: () -> Unit,
    onSignUpClick: () -> Unit,
    enabled: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        ClickableText(
            text = stringResource(id = R.string.feature_login_find_password),
            onClick = { onFindPasswordClick() },
            textAlign = TextAlign.End,
            enabled = enabled,
            modifier = Modifier.weight(1f, fill = false),
        )
        Text(
            text = "|",
            color = CustomColorProvider.colorScheme.onBackground,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        ClickableText(
            text = stringResource(id = R.string.feature_login_sign_up),
            onClick = { onSignUpClick() },
            enabled = enabled,
            modifier = Modifier.weight(1f, fill = false),
        )
    }
}

@DevicePreviews
@Composable
fun LoginScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            LoginScreen(
                uiState = LoginUiState(isLoading = false),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
