package com.yjy.feature.intro

import android.annotation.SuppressLint
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.extensions.clickableSingle
import com.yjy.common.core.login.GoogleLoginManager
import com.yjy.common.core.login.KakaoLoginManager
import com.yjy.common.core.login.NaverLoginManager
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.BaseBottomSheet
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherButton
import com.yjy.common.designsystem.component.ChallengeTogetherDialog
import com.yjy.common.designsystem.component.ChallengeTogetherOutlinedButton
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.StableImage
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.intro.model.IntroUiAction
import com.yjy.feature.intro.model.IntroUiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun IntroRoute(
    onStartWithEmailClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onSignUpWithKakao: (id: String) -> Unit,
    onSignUpWithGoogle: (id: String) -> Unit,
    onSignUpWithNaver: (id: String) -> Unit,
    onSignUpWithGuest: (id: String) -> Unit,
    onShowToast: (String) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: IntroViewModel = hiltViewModel(),
) {
    val isTryingLogin by viewModel.isTryingLogin.collectAsStateWithLifecycle()

    IntroScreen(
        modifier = modifier,
        isTryingLogin = isTryingLogin,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onLoginSuccess = onLoginSuccess,
        onStartWithEmailClick = onStartWithEmailClick,
        onSignUpWithKakao = onSignUpWithKakao,
        onSignUpWithGoogle = onSignUpWithGoogle,
        onSignUpWithNaver = onSignUpWithNaver,
        onSignUpWithGuest = onSignUpWithGuest,
        onShowToast = onShowToast,
        onShowSnackbar = onShowSnackbar,
    )
}

@SuppressLint("HardwareIds")
@Composable
internal fun IntroScreen(
    modifier: Modifier = Modifier,
    isTryingLogin: Boolean = false,
    uiEvent: Flow<IntroUiEvent> = flowOf(),
    processAction: (IntroUiAction) -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onStartWithEmailClick: () -> Unit = {},
    onSignUpWithKakao: (id: String) -> Unit = {},
    onSignUpWithGoogle: (id: String) -> Unit = {},
    onSignUpWithNaver: (id: String) -> Unit = {},
    onSignUpWithGuest: (id: String) -> Unit = {},
    onShowToast: (String) -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 3 })
    var shouldShowBottomSheet by rememberSaveable { mutableStateOf(false) }
    var shouldShowGuestWarning by rememberSaveable { mutableStateOf(false) }

    val loginSuccessMessage = stringResource(id = R.string.feature_intro_login_success)
    val unknownErrorMessage = stringResource(id = R.string.feature_intro_unknown_error)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            IntroUiEvent.LoginSuccess -> {
                onShowToast(loginSuccessMessage)
                onLoginSuccess()
            }

            IntroUiEvent.LoginFailure -> onShowSnackbar(SnackbarType.ERROR, unknownErrorMessage)
            is IntroUiEvent.NeedKakaoSignUp -> onSignUpWithKakao(it.id)
            is IntroUiEvent.NeedGoogleSignUp -> onSignUpWithGoogle(it.id)
            is IntroUiEvent.NeedNaverSignUp -> onSignUpWithNaver(it.id)
            is IntroUiEvent.NeedGuestSignUp -> onSignUpWithGuest(it.id)
        }
    }

    if (shouldShowBottomSheet) {
        IntroBottomSheet(
            onStartWithEmailClick = {
                shouldShowBottomSheet = false
                onStartWithEmailClick()
            },
            onStartWithGuestClick = { shouldShowGuestWarning = true },
            onStartWithKakaoClick = {
                KakaoLoginManager.login(
                    context = context,
                    onSuccess = { kakaoId ->
                        shouldShowBottomSheet = false
                        processAction(IntroUiAction.OnKakaoLoginClick(kakaoId = kakaoId))
                    },
                    onFailure = {
                        shouldShowBottomSheet = false
                        processAction(IntroUiAction.OnLoginFailure)
                    },
                )
            },
            onStartWithGoogleClick = {
                GoogleLoginManager.login(
                    context = context,
                    onSuccess = { googleId ->
                        shouldShowBottomSheet = false
                        processAction(IntroUiAction.OnGoogleLoginClick(googleId = googleId))
                    },
                    onFailure = {
                        shouldShowBottomSheet = false
                        processAction(IntroUiAction.OnLoginFailure)
                    },
                )
            },
            onStartWithNaverClick = {
                NaverLoginManager.login(
                    context = context,
                    onSuccess = { naverId ->
                        shouldShowBottomSheet = false
                        processAction(IntroUiAction.OnNaverLoginClick(naverId = naverId))
                    },
                    onFailure = {
                        shouldShowBottomSheet = false
                        processAction(IntroUiAction.OnLoginFailure)
                    },
                )
            },
            onDismiss = { shouldShowBottomSheet = false },
        )
    }

    if (shouldShowGuestWarning) {
        ChallengeTogetherDialog(
            title = stringResource(id = R.string.feature_intro_start_guest_warning),
            description = stringResource(id = R.string.feature_intro_start_guest_warning_detail),
            positiveTextRes = R.string.feature_intro_start,
            onClickPositive = {
                shouldShowGuestWarning = false
                shouldShowBottomSheet = false

                val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                processAction(IntroUiAction.OnGuestLoginClick(guestId = androidId))
            },
            onClickNegative = { shouldShowGuestWarning = false },
        )
    }

    Scaffold(
        bottomBar = {
            StartButton(
                onClick = { shouldShowBottomSheet = true },
                enabled = !isTryingLogin,
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(vertical = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                IntroPage(
                    image = when (page) {
                        0 -> R.drawable.image_mind
                        1 -> R.drawable.image_grade
                        else -> R.drawable.image_community
                    },
                    title = when (page) {
                        0 -> R.string.feature_intro_life_changing_challenges
                        1 -> R.string.feature_intro_challenge_together
                        else -> R.string.feature_intro_community
                    },
                    description = when (page) {
                        0 -> R.string.feature_intro_features
                        1 -> R.string.feature_intro_challenge_together_detail
                        else -> R.string.feature_intro_community_detail
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp),
                )
            }
            PageIndicator(
                pageCount = pagerState.pageCount,
                currentPage = pagerState.currentPage,
            )
        }
    }
}

@Composable
private fun IntroBottomSheet(
    onStartWithEmailClick: () -> Unit,
    onStartWithGuestClick: () -> Unit,
    onStartWithKakaoClick: () -> Unit,
    onStartWithGoogleClick: () -> Unit,
    onStartWithNaverClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(
        onDismiss = onDismiss,
        showCloseButton = false,
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.feature_intro_start_with),
            style = MaterialTheme.typography.bodyLarge,
            color = CustomColorProvider.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))
        SNSLoginButtons(
            onStartWithKakaoClick = onStartWithKakaoClick,
            onStartWithGoogleClick = onStartWithGoogleClick,
            onStartWithNaverClick = onStartWithNaverClick,
        )
        Spacer(modifier = Modifier.height(32.dp))
        SNSLoginDivider()
        Spacer(modifier = Modifier.height(16.dp))
        EmailLoginButton(onClick = onStartWithEmailClick)
        Spacer(modifier = Modifier.height(8.dp))
        GuestStartButton(onClick = onStartWithGuestClick)
    }
}

@Composable
private fun EmailLoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ChallengeTogetherOutlinedButton(
        modifier = modifier,
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        borderColor = CustomColorProvider.colorScheme.onSurfaceMuted,
        contentColor = CustomColorProvider.colorScheme.onSurface.copy(alpha = 0.5f),
        content = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = ChallengeTogetherIcons.Email),
                    contentDescription = stringResource(id = R.string.feature_intro_start_email),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(vertical = 8.dp),
                )
                Text(
                    text = stringResource(id = R.string.feature_intro_start_email),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
        },
    )
}

@Composable
private fun GuestStartButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ChallengeTogetherButton(
        modifier = modifier,
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        containerColor = CustomColorProvider.colorScheme.background,
        contentColor = CustomColorProvider.colorScheme.onBackgroundMuted,
        content = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = ChallengeTogetherIcons.Guest),
                    contentDescription = stringResource(id = R.string.feature_intro_start_guest),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(vertical = 8.dp),
                )
                Text(
                    text = stringResource(id = R.string.feature_intro_start_guest),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
        },
    )
}

@Composable
private fun SNSLoginDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = CustomColorProvider.colorScheme.onSurfaceMuted,
            thickness = 0.5.dp,
        )
        Text(
            text = stringResource(id = R.string.feature_intro_divider_text),
            color = CustomColorProvider.colorScheme.onSurfaceMuted,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp),
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = CustomColorProvider.colorScheme.onSurfaceMuted,
            thickness = 0.5.dp,
        )
    }
}

@Composable
private fun SNSLoginButtons(
    onStartWithKakaoClick: () -> Unit,
    onStartWithGoogleClick: () -> Unit,
    onStartWithNaverClick: () -> Unit,
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
                .clickableSingle(onClick = onStartWithKakaoClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = ChallengeTogetherIcons.Kakao),
                contentDescription = stringResource(id = R.string.feature_intro_kakao_content_description),
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(CustomColorProvider.colorScheme.googleBackground)
                .border(
                    width = 1.dp,
                    color = CustomColorProvider.colorScheme.divider,
                    shape = CircleShape,
                )
                .clickableSingle(onClick = onStartWithGoogleClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = ChallengeTogetherIcons.Google),
                contentDescription = stringResource(id = R.string.feature_intro_google_content_description),
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(CustomColorProvider.colorScheme.naverBackground)
                .clickableSingle(onClick = onStartWithNaverClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = ChallengeTogetherIcons.Naver),
                contentDescription = stringResource(id = R.string.feature_intro_naver_content_description),
            )
        }
    }
}

@Composable
private fun StartButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
    ) {
        ChallengeTogetherButton(
            onClick = onClick,
            enabled = enabled,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 55.dp),
        ) {
            if (!enabled) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = CustomColorProvider.colorScheme.brand,
                )
            } else {
                Text(
                    text = stringResource(id = R.string.feature_intro_start),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

private const val INDICATOR_COLOR_ANIMATION_DURATION = 300

@Composable
private fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { iteration ->
            val width by animateDpAsState(
                targetValue = if (currentPage == iteration) 50.dp else 30.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow,
                ),
                label = "width",
            )

            val color by animateColorAsState(
                targetValue = if (currentPage == iteration) {
                    CustomColorProvider.colorScheme.brand
                } else {
                    CustomColorProvider.colorScheme.divider
                },
                animationSpec = tween(INDICATOR_COLOR_ANIMATION_DURATION),
                label = "color",
            )

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(color)
                    .width(width)
                    .height(8.dp),
            )
        }
    }
}

@Composable
private fun IntroPage(
    @DrawableRes image: Int,
    @StringRes title: Int,
    @StringRes description: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        StableImage(
            drawableResId = image,
            descriptionResId = title,
            modifier = Modifier.size(200.dp),
        )
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = stringResource(id = title),
            style = MaterialTheme.typography.titleMedium,
            color = CustomColorProvider.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = description),
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
            textAlign = TextAlign.Center,
        )
    }
}

@DevicePreviews
@Composable
fun IntroScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            IntroScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
