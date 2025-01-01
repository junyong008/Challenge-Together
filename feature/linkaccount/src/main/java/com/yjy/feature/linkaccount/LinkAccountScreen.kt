package com.yjy.feature.linkaccount

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.login.GoogleLoginManager
import com.yjy.common.core.login.KakaoLoginManager
import com.yjy.common.core.login.NaverLoginManager
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherButton
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.StableImage
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.linkaccount.model.LinkAccountUiAction
import com.yjy.feature.linkaccount.model.LinkAccountUiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun LinkAccountRoute(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LinkAccountViewModel = hiltViewModel(),
) {
    val isLinkingAccount by viewModel.isLinkingAccount.collectAsStateWithLifecycle()

    LinkAccountScreen(
        modifier = modifier,
        isLinkingAccount = isLinkingAccount,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun LinkAccountScreen(
    modifier: Modifier = Modifier,
    isLinkingAccount: Boolean = false,
    uiEvent: Flow<LinkAccountUiEvent> = flowOf(),
    processAction: (LinkAccountUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val linkSuccessMessage = stringResource(id = R.string.feature_linkaccount_success)
    val alreadyLinkedMessage = stringResource(id = R.string.feature_linkaccount_already_linked)
    val alreadyRegisteredMessage = stringResource(id = R.string.feature_linkaccount_already_exists)
    val checkNetworkMessage = stringResource(id = R.string.feature_linkaccount_check_network_connection)
    val linkFailedMessage = stringResource(id = R.string.feature_linkaccount_error)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            LinkAccountUiEvent.Success -> {
                onShowSnackbar(SnackbarType.SUCCESS, linkSuccessMessage)
                onBackClick()
            }

            LinkAccountUiEvent.Failure.AlreadyLinked -> {
                onShowSnackbar(SnackbarType.ERROR, alreadyLinkedMessage)
                onBackClick()
            }

            LinkAccountUiEvent.Failure.AlreadyRegistered -> {
                logouts(context)
                onShowSnackbar(SnackbarType.ERROR, alreadyRegisteredMessage)
            }

            LinkAccountUiEvent.Failure.NetworkError -> {
                logouts(context)
                onShowSnackbar(SnackbarType.ERROR, checkNetworkMessage)
            }

            LinkAccountUiEvent.Failure.UnknownError -> {
                logouts(context)
                onShowSnackbar(SnackbarType.ERROR, linkFailedMessage)
            }
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
        modifier = modifier.consumeWindowInsets(WindowInsets.navigationBars),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            TitleWithDescription(
                titleRes = R.string.feature_linkaccount_title,
                descriptionRes = R.string.feature_linkaccount_description,
            )
            Spacer(modifier = Modifier.height(70.dp))
            StableImage(
                drawableResId = R.drawable.image_link,
                descriptionResId = R.string.feature_linkaccount_info_graphic,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(115.dp),
            )
            Spacer(modifier = Modifier.height(70.dp))
            LinkButtons(
                onKakaoLinkClick = {
                    KakaoLoginManager.login(
                        context = context,
                        onSuccess = { processAction(LinkAccountUiAction.OnLinkWithKakao(it)) },
                        onFailure = { processAction(LinkAccountUiAction.OnLinkFailed) },
                    )
                },
                onGoogleLinkClick = {
                    GoogleLoginManager.login(
                        context = context,
                        onSuccess = { processAction(LinkAccountUiAction.OnLinkWithGoogle(it)) },
                        onFailure = { processAction(LinkAccountUiAction.OnLinkFailed) },
                    )
                },
                onNaverLinkClick = {
                    NaverLoginManager.login(
                        context = context,
                        onSuccess = { processAction(LinkAccountUiAction.OnLinkWithNaver(it)) },
                        onFailure = { processAction(LinkAccountUiAction.OnLinkFailed) },
                    )
                },
                isLinkingAccount = isLinkingAccount,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

private fun logouts(context: Context) {
    KakaoLoginManager.logout()
    NaverLoginManager.logout()
    GoogleLoginManager.logout(context)
}

@Composable
private fun LinkButtons(
    onKakaoLinkClick: () -> Unit,
    onGoogleLinkClick: () -> Unit,
    onNaverLinkClick: () -> Unit,
    isLinkingAccount: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Column(modifier = Modifier.alpha(if (isLinkingAccount) 0f else 1f)) {
            LinkButton(
                iconResId = ChallengeTogetherIcons.Kakao,
                textResId = R.string.feature_linkaccount_kakao,
                contentColor = Color.Black.copy(alpha = 0.85f),
                containerColor = CustomColorProvider.colorScheme.kakaoBackground,
                onClick = onKakaoLinkClick,
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinkButton(
                iconResId = ChallengeTogetherIcons.Google,
                textResId = R.string.feature_linkaccount_google,
                contentColor = Color.Black,
                containerColor = CustomColorProvider.colorScheme.googleBackground,
                onClick = onGoogleLinkClick,
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinkButton(
                iconResId = ChallengeTogetherIcons.Naver,
                textResId = R.string.feature_linkaccount_naver,
                contentColor = Color.White,
                containerColor = CustomColorProvider.colorScheme.naverBackground,
                onClick = onNaverLinkClick,
            )
        }

        if (isLinkingAccount) {
            CircularProgressIndicator(
                color = CustomColorProvider.colorScheme.brand,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun LinkButton(
    @DrawableRes iconResId: Int,
    @StringRes textResId: Int,
    contentColor: Color,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ChallengeTogetherButton(
        modifier = modifier,
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        containerColor = containerColor,
        contentColor = contentColor,
        content = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                StableImage(
                    drawableResId = iconResId,
                    descriptionResId = textResId,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(vertical = 10.dp),
                )
                Text(
                    text = stringResource(id = textResId),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                )
            }
        },
    )
}

@DevicePreviews
@Composable
fun LinkAccountScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            LinkAccountScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
