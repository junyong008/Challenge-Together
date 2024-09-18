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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yjy.core.designsystem.component.ChallengeTogetherBackground
import com.yjy.core.designsystem.component.ChallengeTogetherGradientBackground
import com.yjy.core.designsystem.component.ChallengeTogetherOutlinedButton
import com.yjy.core.designsystem.component.ChallengeTogetherTextField
import com.yjy.core.designsystem.component.StableImage
import com.yjy.core.designsystem.icon.ChallengeTogetherIcons
import com.yjy.core.designsystem.theme.ChallengeTogetherTheme
import com.yjy.core.designsystem.theme.googleBackground
import com.yjy.core.designsystem.theme.kakaoBackground
import com.yjy.core.designsystem.theme.naverBackground
import com.yjy.core.ui.DevicePreviews
import com.yjy.feature.login.navigation.LoginStrings

@Composable
internal fun LoginRoute(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = hiltViewModel(),
) {
    LoginScreen(
        modifier = modifier,
        onLoginClick = loginViewModel::login,
        onFindPasswordClick = { /* TODO */ },
        onSignUpClick = { /* TODO */ },
        onKakaoLoginClick = { /* TODO */ },
        onGoogleLoginClick = { /* TODO */ },
        onNaverLoginClick = { /* TODO */ },
    )
}

@Composable
internal fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onFindPasswordClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onKakaoLoginClick: () -> Unit = {},
    onGoogleLoginClick: () -> Unit = {},
    onNaverLoginClick: () -> Unit = {},
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 50.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        StableImage(
            drawableResId = R.drawable.feature_login_app_logo,
            description = stringResource(id = LoginStrings.feature_login_app_logo_content_description),
            modifier = Modifier.width(260.dp),
        )
        Spacer(modifier = Modifier.height(32.dp))
        EmailTextField(
            value = email,
            onValueChange = { email = it },
        )
        Spacer(modifier = Modifier.height(8.dp))
        PasswordTextField(
            value = password,
            onValueChange = { password = it },
        )
        Spacer(modifier = Modifier.height(16.dp))
        ChallengeTogetherOutlinedButton(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
        ) {
            Text(
                text = stringResource(id = LoginStrings.feature_login_button_text),
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        FindPasswordAndSignUp(
            onFindPasswordClick = onFindPasswordClick,
            onSignUpClick = onSignUpClick,
        )
        Spacer(modifier = Modifier.height(100.dp))
        SNSLoginDivider()
        Spacer(modifier = Modifier.height(16.dp))
        SNSLoginButtons(
            onKakaoLoginClick = onKakaoLoginClick,
            onGoogleLoginClick = onGoogleLoginClick,
            onNaverLoginClick = onNaverLoginClick,
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LoginTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(
                painter = painterResource(id = ChallengeTogetherIcons.Mail),
                contentDescription = stringResource(id = LoginStrings.feature_login_input_email_content_description),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
        ),
        placeholderText = stringResource(id = LoginStrings.feature_login_input_email_place_holder,),
        modifier = modifier,
    )
}

@Composable
private fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var shouldHidePassword by rememberSaveable { mutableStateOf(true) }

    LoginTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(
                painter = painterResource(id = ChallengeTogetherIcons.Lock),
                contentDescription = stringResource(id = LoginStrings.feature_login_input_password_content_description),
                tint = MaterialTheme.colorScheme.onBackground,
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
                        }
                    ),
                    contentDescription = if (shouldHidePassword) {
                        stringResource(id = LoginStrings.feature_login_input_password_show_content_description)
                    } else {
                        stringResource(id = LoginStrings.feature_login_input_password_hide_content_description)
                    },
                    tint = MaterialTheme.colorScheme.surfaceDim,
                    modifier = Modifier
                        .clip(RoundedCornerShape(32))
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
        modifier = modifier,
    )
}

@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
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
        borderColor = MaterialTheme.colorScheme.background,
        backgroundColor = MaterialTheme.colorScheme.background,
        modifier = modifier.height(50.dp),
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
        Text(
            text = stringResource(id = LoginStrings.feature_login_find_password),
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.End,
            modifier = Modifier.clickable { onFindPasswordClick() }
        )
        Text(
            text = "|",
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Text(
            text = stringResource(id = LoginStrings.feature_login_sign_up),
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Start,
            modifier = Modifier.clickable { onSignUpClick() }
        )
    }
}

@Composable
private fun SNSLoginDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outline)
        )
        Text(
            text = stringResource(id = LoginStrings.feature_login_social_login_text),
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outline)
        )
    }
}

@Composable
private fun SNSLoginButtons(
    onKakaoLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onNaverLoginClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(kakaoBackground)
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
                .background(googleBackground)
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
                .background(naverBackground)
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
            ChallengeTogetherGradientBackground {
                LoginScreen(
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}