package com.yjy.feature.signup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.constants.AuthConst.MAX_NICKNAME_LENGTH
import com.yjy.common.core.constants.AuthConst.MIN_NICKNAME_LENGTH
import com.yjy.common.core.constants.UrlConst.PRIVACY_POLICY
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherButton
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.ClickableText
import com.yjy.common.designsystem.component.ConditionIndicator
import com.yjy.common.designsystem.component.ErrorIndicator
import com.yjy.common.designsystem.component.SingleLineTextField
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.StableImage
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
internal fun NicknameRoute(
    kakaoId: String,
    googleId: String,
    naverId: String,
    guestId: String,
    onBackClick: () -> Unit,
    onSignUpSuccess: () -> Unit,
    onShowToast: (String) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NicknameScreen(
        modifier = modifier,
        kakaoId = kakaoId,
        googleId = googleId,
        naverId = naverId,
        guestId = guestId,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onSignUpSuccess = onSignUpSuccess,
        onShowToast = onShowToast,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun NicknameScreen(
    kakaoId: String = "",
    googleId: String = "",
    naverId: String = "",
    guestId: String = "",
    modifier: Modifier = Modifier,
    uiState: SignUpUiState = SignUpUiState(),
    uiEvent: Flow<SignUpUiEvent> = flowOf(),
    processAction: (SignUpUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {},
    onShowToast: (String) -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val signUpCompleteMessage = stringResource(id = R.string.feature_signup_complete)
    val duplicatedNicknameMessage = stringResource(id = R.string.feature_signup_nickname_already_taken)
    val checkNetworkMessage = stringResource(id = R.string.feature_signup_check_network_connection)
    val unknownErrorMessage = stringResource(id = R.string.feature_signup_error)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            SignUpUiEvent.SignUpSuccess -> {
                onShowToast(signUpCompleteMessage)
                onSignUpSuccess()
            }

            SignUpUiEvent.DuplicatedNickname ->
                onShowSnackbar(SnackbarType.ERROR, duplicatedNicknameMessage)

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
                titleRes = R.string.feature_signup_set_nickname_title,
                descriptionRes = R.string.feature_signup_set_nickname_description,
            )
            Spacer(modifier = Modifier.height(40.dp))
            StableImage(
                drawableResId = R.drawable.image_smile,
                descriptionResId = R.string.feature_signup_smile_image_description,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(115.dp),
            )
            Spacer(modifier = Modifier.height(40.dp))
            SingleLineTextField(
                value = uiState.nickname,
                onValueChange = { processAction(SignUpUiAction.OnNicknameUpdated(it)) },
                textStyle = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                contentAlignment = Alignment.Center,
                placeholderText = stringResource(id = R.string.feature_signup_nickname_place_holder),
                enabled = !uiState.isSigningUp,
            )
            ConditionIndicator(
                text = stringResource(
                    id = R.string.feature_signup_nickname_length_indicator,
                    MIN_NICKNAME_LENGTH,
                    MAX_NICKNAME_LENGTH,
                ),
                isMatched = uiState.isNicknameLengthValid,
            )
            if (uiState.isNicknameHasOnlyConsonantOrVowel) {
                ErrorIndicator(
                    text = stringResource(id = R.string.feature_signup_nickname_korean_constraint),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            ChallengeTogetherButton(
                onClick = {
                    processAction(
                        SignUpUiAction.OnStartClick(
                            nickname = uiState.nickname,
                            email = uiState.email,
                            password = uiState.password,
                            kakaoId = kakaoId,
                            googleId = googleId,
                            naverId = naverId,
                            guestId = guestId,
                        ),
                    )
                },
                enabled = uiState.canTryStart && !uiState.isSigningUp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isSigningUp) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp),
                        color = CustomColorProvider.colorScheme.brand,
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.feature_signup_start),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            PrivacyPolicyText(modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun PrivacyPolicyText(
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    val privacyAgreement = stringResource(id = R.string.feature_signup_privacy_policy_agreement)
    val privacyText = stringResource(id = R.string.feature_signup_privacy_policy)

    val annotatedText = buildAnnotatedString {
        append(privacyAgreement)

        val startIndex = privacyAgreement.indexOf(privacyText)
        val endIndex = startIndex + privacyText.length

        addStyle(
            style = SpanStyle(
                color = CustomColorProvider.colorScheme.brand,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
            ),
            start = startIndex,
            end = endIndex,
        )
    }

    ClickableText(
        text = annotatedText,
        textAlign = TextAlign.Center,
        textDecoration = TextDecoration.None,
        color = CustomColorProvider.colorScheme.onBackgroundMuted,
        onClick = { uriHandler.openUri(PRIVACY_POLICY) },
        modifier = modifier,
    )
}

@DevicePreviews
@Composable
fun NicknameScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            NicknameScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
