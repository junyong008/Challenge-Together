package com.yjy.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.core.common.ui.ObserveAsEvents
import com.yjy.core.designsystem.component.ChallengeTogetherBackground
import com.yjy.core.designsystem.component.ChallengeTogetherButton
import com.yjy.core.designsystem.component.ChallengeTogetherTextField
import com.yjy.core.designsystem.component.ClickableText
import com.yjy.core.designsystem.component.LoadingWheel
import com.yjy.core.designsystem.component.SnackbarType
import com.yjy.core.designsystem.icon.ChallengeTogetherIcons
import com.yjy.core.designsystem.theme.ChallengeTogetherTheme
import com.yjy.core.designsystem.theme.CustomColorProvider
import com.yjy.core.ui.DevicePreviews
import com.yjy.feature.login.navigation.LoginStrings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun LoginRoute(
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun LoginScreen(
    modifier: Modifier = Modifier,
    uiState: LoginUiState = LoginUiState(),
    uiEvent: Flow<LoginUiEvent> = flowOf(),
    processAction: (LoginUiAction) -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val userNotFoundMessage = stringResource(id = LoginStrings.feature_login_user_not_found)
    val loginErrorMessage = stringResource(id = LoginStrings.feature_login_error)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            is LoginUiEvent.LoginSuccess -> {}
            is LoginUiEvent.LoginFailure.UserNotFound -> onShowSnackbar(SnackbarType.ERROR, userNotFoundMessage)
            is LoginUiEvent.LoginFailure.Error -> onShowSnackbar(SnackbarType.ERROR, loginErrorMessage)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 50.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Title(
            title = stringResource(id = LoginStrings.feature_login_title),
            titleDescription = stringResource(id = LoginStrings.feature_login_title_description),
            modifier = Modifier.align(Alignment.Start),
        )
        Spacer(modifier = Modifier.height(32.dp))

        Box {
            if (uiState.isLoading) {
                LoadingWheel(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (uiState.isLoading) 0f else 1f),
            ) {
                EmailTextField(
                    value = uiState.email,
                    onValueChange = { processAction(LoginUiAction.OnEmailUpdated(it)) },
                )
                if (!uiState.isValidEmailFormat) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(id = LoginStrings.feature_login_invalid_email_format),
                        color = CustomColorProvider.colorScheme.red,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.Start),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                PasswordTextField(
                    value = uiState.password,
                    onValueChange = { processAction(LoginUiAction.OnPasswordUpdated(it)) },
                )
                Spacer(modifier = Modifier.height(16.dp))
                ChallengeTogetherButton(
                    onClick = {
                        processAction(LoginUiAction.OnLoginClick(uiState.email, uiState.password))
                    },
                    enabled = uiState.canTryLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("loginButton"),
                ) {
                    Text(
                        text = stringResource(id = LoginStrings.feature_login_button_text),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FindPasswordAndSignUp(
                    onFindPasswordClick = { processAction(LoginUiAction.OnFindPasswordClick) },
                    onSignUpClick = { processAction(LoginUiAction.OnSignUpClick) },
                )
                Spacer(modifier = Modifier.height(100.dp))
                SNSLoginDivider()
                Spacer(modifier = Modifier.height(16.dp))
                SNSLoginButtons(
                    onKakaoLoginClick = { processAction(LoginUiAction.OnKakaoLoginClick) },
                    onGoogleLoginClick = { processAction(LoginUiAction.OnGoogleLoginClick) },
                    onNaverLoginClick = { processAction(LoginUiAction.OnNaverLoginClick) },
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
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
private fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    LoginTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(
                painter = painterResource(id = ChallengeTogetherIcons.Email),
                contentDescription = stringResource(id = LoginStrings.feature_login_input_email_content_description),
                tint = CustomColorProvider.colorScheme.onSurface,
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                Icon(
                    painter = painterResource(id = ChallengeTogetherIcons.Cancel),
                    contentDescription = stringResource(
                        id = LoginStrings.feature_login_input_email_clear_content_description,
                    ),
                    tint = CustomColorProvider.colorScheme.onSurfaceMuted,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onValueChange("") },
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
        ),
        placeholderText = stringResource(id = LoginStrings.feature_login_input_email_place_holder),
    )
}

@Composable
private fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    var shouldHidePassword by rememberSaveable { mutableStateOf(true) }

    LoginTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(
                painter = painterResource(id = ChallengeTogetherIcons.Lock),
                contentDescription = stringResource(id = LoginStrings.feature_login_input_password_content_description),
                tint = CustomColorProvider.colorScheme.onSurface,
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                Icon(
                    painter = painterResource(
                        id = if (shouldHidePassword) {
                            ChallengeTogetherIcons.VisibilityOff
                        } else {
                            ChallengeTogetherIcons.Visibility
                        },
                    ),
                    contentDescription = if (shouldHidePassword) {
                        stringResource(id = LoginStrings.feature_login_input_password_show_content_description)
                    } else {
                        stringResource(id = LoginStrings.feature_login_input_password_hide_content_description)
                    },
                    tint = CustomColorProvider.colorScheme.onSurfaceMuted,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            shouldHidePassword = !shouldHidePassword
                        },
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
        ),
        placeholderText = stringResource(id = LoginStrings.feature_login_input_password_place_holder),
        shouldHidePassword = shouldHidePassword,
    )
}

@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: @Composable () -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions,
    placeholderText: String,
    shouldHidePassword: Boolean = false,
) {
    ChallengeTogetherTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        maxLines = 1,
        shouldHidePassword = shouldHidePassword,
        placeholderText = placeholderText,
    )
}

@Composable
private fun FindPasswordAndSignUp(
    onFindPasswordClick: () -> Unit,
    onSignUpClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        ClickableText(
            text = stringResource(id = LoginStrings.feature_login_find_password),
            onClick = { onFindPasswordClick() },
            textAlign = TextAlign.End,
        )
        Text(
            text = "|",
            color = CustomColorProvider.colorScheme.onBackground,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        ClickableText(
            text = stringResource(id = LoginStrings.feature_login_sign_up),
            onClick = { onSignUpClick() },
        )
    }
}

@Composable
private fun SNSLoginDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(CustomColorProvider.colorScheme.onBackground),
        )
        Text(
            text = stringResource(id = LoginStrings.feature_login_social_login_text),
            color = CustomColorProvider.colorScheme.onBackground,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(CustomColorProvider.colorScheme.onBackground),
        )
    }
}

@Composable
private fun SNSLoginButtons(
    onKakaoLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onNaverLoginClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(CustomColorProvider.colorScheme.kakaoBackground)
                .clickable(
                    onClick = onKakaoLoginClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_kakao),
                contentDescription = stringResource(id = LoginStrings.feature_login_kakao_content_description),
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(CustomColorProvider.colorScheme.googleBackground)
                .clickable(
                    onClick = onGoogleLoginClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = stringResource(id = LoginStrings.feature_login_google_content_description),
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(CustomColorProvider.colorScheme.naverBackground)
                .clickable(
                    onClick = onNaverLoginClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_naver),
                contentDescription = stringResource(id = LoginStrings.feature_login_naver_content_description),
            )
        }
    }
}

@DevicePreviews
@Composable
fun LoginScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            LoginScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
