package com.yjy.feature.waitingchallenge

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.constants.ChallengeConst.MAX_ROOM_PASSWORD_LENGTH
import com.yjy.common.core.constants.DeepLinkConfig
import com.yjy.common.core.constants.DeepLinkType
import com.yjy.common.core.extensions.clickableSingle
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.BaseBottomSheet
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherButton
import com.yjy.common.designsystem.component.ChallengeTogetherDialog
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.CircleMedal
import com.yjy.common.designsystem.component.ClickableText
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.component.PasswordDialog
import com.yjy.common.designsystem.component.SelectableText
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.StableImage
import com.yjy.common.designsystem.extensions.getDisplayNameResId
import com.yjy.common.designsystem.extensions.getIconResId
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.waitingchallenge.model.ChallengeDetailUiState
import com.yjy.feature.waitingchallenge.model.WaitingChallengeUiAction
import com.yjy.feature.waitingchallenge.model.WaitingChallengeUiEvent
import com.yjy.feature.waitingchallenge.model.WaitingChallengeUiState
import com.yjy.feature.waitingchallenge.model.challengeOrNull
import com.yjy.feature.waitingchallenge.model.isLoading
import com.yjy.model.challenge.DetailedWaitingChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.TargetDays
import com.yjy.model.common.Tier
import com.yjy.model.common.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun WaitingChallengeRoute(
    onBackClick: () -> Unit,
    onChallengeStart: (challengeId: Int) -> Unit,
    onBoardClick: (challengeId: Int, isEditable: Boolean) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WaitingChallengeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val challengeDetail by viewModel.challengeDetail.collectAsStateWithLifecycle()

    WaitingChallengeScreen(
        modifier = modifier,
        challengeDetail = challengeDetail,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onChallengeStart = onChallengeStart,
        onBoardClick = onBoardClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun WaitingChallengeScreen(
    modifier: Modifier = Modifier,
    challengeDetail: ChallengeDetailUiState = ChallengeDetailUiState.Loading,
    uiState: WaitingChallengeUiState = WaitingChallengeUiState(),
    uiEvent: Flow<WaitingChallengeUiEvent> = flowOf(),
    processAction: (WaitingChallengeUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onChallengeStart: (challengeId: Int) -> Unit = {},
    onBoardClick: (challengeId: Int, isEditable: Boolean) -> Unit = { _, _ -> },
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val passwordCopiedMessage = stringResource(id = R.string.feature_waitingchallenge_password_copied)
    val deleteSuccessMessage = stringResource(id = R.string.feature_waitingchallenge_delete_successful)
    val startSuccessMessage = stringResource(id = R.string.feature_waitingchallenge_start_successful)
    val challengeFullMessage = stringResource(id = R.string.feature_waitingchallenge_participant_full)
    val commonErrorMessage = stringResource(id = R.string.feature_waitingchallenge_error)
    val errorMessages = mapOf(
        WaitingChallengeUiEvent.LoadFailure.NotFound to stringResource(
            id = R.string.feature_waitingchallenge_not_found,
        ),
        WaitingChallengeUiEvent.LoadFailure.AlreadyStarted to stringResource(
            id = R.string.feature_waitingchallenge_already_started,
        ),
        WaitingChallengeUiEvent.LoadFailure.PasswordIncorrect to stringResource(
            id = R.string.feature_waitingchallenge_password_incorrect,
        ),
        WaitingChallengeUiEvent.LoadFailure.Network to stringResource(
            id = R.string.feature_waitingchallenge_check_network_connection,
        ),
        WaitingChallengeUiEvent.LoadFailure.Unknown to stringResource(
            id = R.string.feature_waitingchallenge_error,
        ),
    )

    ObserveAsEvents(flow = uiEvent, useMainImmediate = false) { event ->
        when (event) {
            is WaitingChallengeUiEvent.LoadFailure -> {
                onShowSnackbar(
                    SnackbarType.ERROR,
                    errorMessages[event] ?: errorMessages[WaitingChallengeUiEvent.LoadFailure.Unknown]!!,
                )
                onBackClick()
            }

            WaitingChallengeUiEvent.DeleteSuccess -> {
                onShowSnackbar(SnackbarType.SUCCESS, deleteSuccessMessage)
                onBackClick()
            }

            is WaitingChallengeUiEvent.StartSuccess -> {
                onShowSnackbar(SnackbarType.SUCCESS, startSuccessMessage)
                onChallengeStart(event.challengeId)
            }

            WaitingChallengeUiEvent.PasswordInputCanceled -> onBackClick()
            WaitingChallengeUiEvent.PasswordCopied -> onShowSnackbar(SnackbarType.MESSAGE, passwordCopiedMessage)
            WaitingChallengeUiEvent.DeleteFailure -> onShowSnackbar(SnackbarType.ERROR, commonErrorMessage)
            WaitingChallengeUiEvent.StartFailure -> onShowSnackbar(SnackbarType.ERROR, commonErrorMessage)
            WaitingChallengeUiEvent.JoinFailure.Full -> onShowSnackbar(SnackbarType.ERROR, challengeFullMessage)
            WaitingChallengeUiEvent.JoinFailure.Unknown -> onShowSnackbar(SnackbarType.ERROR, commonErrorMessage)
            WaitingChallengeUiEvent.LeaveFailure.Unknown -> onShowSnackbar(SnackbarType.ERROR, commonErrorMessage)
        }
    }

    var inputPassword by rememberSaveable { mutableStateOf("") }
    var shouldShowMenuBottomSheet by rememberSaveable { mutableStateOf(false) }
    var shouldShowDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }
    var shouldShowStartConfirmDialog by rememberSaveable { mutableStateOf(false) }

    if (uiState.shouldShowPasswordDialog) {
        PasswordDialog(
            value = inputPassword,
            onValueChange = { inputPassword = it.take(MAX_ROOM_PASSWORD_LENGTH) },
            onConfirm = { password ->
                processAction(WaitingChallengeUiAction.OnEnterPassword(password))
            },
            onClickNegative = {
                processAction(WaitingChallengeUiAction.OnDismissPasswordDialog)
            },
        )
    }

    if (challengeDetail.isLoading() || uiState.isDeleting) {
        LoadingWheel(modifier = modifier.background(CustomColorProvider.colorScheme.background))
    } else {
        val challenge = challengeDetail.challengeOrNull() ?: return

        if (shouldShowMenuBottomSheet) {
            val shareMessage = stringResource(
                id = R.string.feature_waitingchallenge_menu_share_message,
                challenge.title,
                challenge.participants.size,
            )

            MenuBottomSheet(
                isAuthor = challenge.isAuthor,
                onShareClick = {
                    val shareLink = "${DeepLinkConfig.ONE_LINK_URL}?" +
                        "${DeepLinkType.TYPE_PARAM}=${DeepLinkType.WAITING}&" +
                        "${DeepLinkType.ID_PARAM}=${challenge.id}"

                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "$shareMessage\n\n$shareLink")
                    }

                    context.startActivity(Intent.createChooser(shareIntent, null))
                },
                onRefreshClick = {
                    shouldShowMenuBottomSheet = false
                    processAction(WaitingChallengeUiAction.OnRefreshClick)
                },
                onDeleteClick = {
                    shouldShowDeleteConfirmDialog = true
                },
                onDismiss = { shouldShowMenuBottomSheet = false },
            )
        }

        if (shouldShowDeleteConfirmDialog) {
            ChallengeTogetherDialog(
                title = stringResource(id = R.string.feature_waitingchallenge_delete),
                description = stringResource(id = R.string.feature_waitingchallenge_delete_prompt),
                positiveTextRes = R.string.feature_waitingchallenge_delete,
                positiveTextColor = CustomColorProvider.colorScheme.red,
                onClickPositive = {
                    shouldShowDeleteConfirmDialog = false
                    shouldShowMenuBottomSheet = false
                    processAction(WaitingChallengeUiAction.OnDeleteClick(challenge.id))
                },
                onClickNegative = { shouldShowDeleteConfirmDialog = false },
            )
        }

        if (shouldShowStartConfirmDialog) {
            ChallengeTogetherDialog(
                title = stringResource(id = R.string.feature_waitingchallenge_start),
                description = stringResource(id = R.string.feature_waitingchallenge_start_prompt),
                positiveTextRes = R.string.feature_waitingchallenge_start,
                onClickPositive = {
                    shouldShowStartConfirmDialog = false
                    processAction(WaitingChallengeUiAction.OnStartClick(challenge.id))
                },
                onClickNegative = { shouldShowStartConfirmDialog = false },
            )
        }

        Box(
            modifier = modifier.fillMaxSize(),
        ) {
            ChallengeBody(
                challenge = challenge,
                onBackClick = onBackClick,
                onMenuClick = { shouldShowMenuBottomSheet = true },
                onBoardClick = onBoardClick,
                processAction = processAction,
            )
            if (challenge.isAuthor ||
                challenge.isParticipated ||
                challenge.participants.size < challenge.maxParticipantCounts
            ) {
                GradientBackground(modifier = Modifier.align(Alignment.BottomCenter))
                ActionButton(
                    isLoading = uiState.isActionProcessing,
                    challenge = challenge,
                    onStartClick = { shouldShowStartConfirmDialog = true },
                    onJoinClick = { processAction(WaitingChallengeUiAction.OnJoinClick(challenge.id)) },
                    onLeaveClick = { processAction(WaitingChallengeUiAction.OnLeaveClick(challenge.id)) },
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    isLoading: Boolean,
    challenge: DetailedWaitingChallenge,
    onStartClick: () -> Unit,
    onJoinClick: () -> Unit,
    onLeaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ChallengeTogetherButton(
        onClick = {
            when {
                challenge.isAuthor -> onStartClick()
                challenge.isParticipated -> onLeaveClick()
                else -> onJoinClick()
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = if (challenge.isParticipated && !challenge.isAuthor) {
            CustomColorProvider.colorScheme.red
        } else {
            CustomColorProvider.colorScheme.brandDim
        },
        contentColor = CustomColorProvider.colorScheme.onBrandDim,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = CustomColorProvider.colorScheme.brand,
                modifier = Modifier.size(24.dp),
            )
        } else {
            Text(
                text = stringResource(
                    id = when {
                        challenge.isAuthor -> R.string.feature_waitingchallenge_start
                        challenge.isParticipated -> R.string.feature_waitingchallenge_leave
                        else -> R.string.feature_waitingchallenge_join
                    },
                ),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun GradientBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        CustomColorProvider.colorScheme.background,
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY,
                ),
            ),
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChallengeBody(
    challenge: DetailedWaitingChallenge,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onBoardClick: (challengeId: Int, isEditable: Boolean) -> Unit,
    processAction: (WaitingChallengeUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current
    val density = LocalDensity.current
    val lazyListState = rememberLazyListState()
    val isSticky by remember {
        derivedStateOf {
            val firstItemHeight = with(density) { 16.dp.toPx() }
            lazyListState.firstVisibleItemIndex > 0 ||
                (
                    lazyListState.firstVisibleItemIndex == 0 &&
                        lazyListState.firstVisibleItemScrollOffset > firstItemHeight
                    )
        }
    }

    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null,
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = modifier
                .fillMaxSize()
                .background(CustomColorProvider.colorScheme.background),
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            stickyHeader {
                ChallengeTogetherTopAppBar(
                    onNavigationClick = onBackClick,
                    shouldShowDivider = isSticky,
                    rightContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            InfoText(
                                challengeId = challenge.id,
                                isPrivate = challenge.password.isNotEmpty(),
                                onPrivateClick = {
                                    clipboardManager.setText(AnnotatedString(challenge.password))
                                    processAction(WaitingChallengeUiAction.OnPasswordCopy)
                                },
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            CategoryIcon(category = challenge.category)
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    },
                )
            }

            item {
                ChallengeContent(
                    challenge = challenge,
                    onMenuClick = onMenuClick,
                    onBoardClick = onBoardClick,
                )
            }
        }
    }
}

@Composable
private fun ChallengeContent(
    challenge: DetailedWaitingChallenge,
    onMenuClick: () -> Unit,
    onBoardClick: (challengeId: Int, isEditable: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = challenge.title,
            style = MaterialTheme.typography.displaySmall,
            color = CustomColorProvider.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        AuthorSection(
            author = challenge.author,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            thickness = 1.dp,
            color = CustomColorProvider.colorScheme.divider,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Row {
            SelectableText(
                text = challenge.description,
                style = MaterialTheme.typography.labelMedium,
                color = CustomColorProvider.colorScheme.onBackground,
                modifier = Modifier
                    .heightIn(min = 200.dp)
                    .padding(16.dp)
                    .weight(1f),
            )
            MenuButton(
                onClick = onMenuClick,
                modifier = Modifier.padding(top = 4.dp, end = 4.dp),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        TargetDaySection(
            targetDays = challenge.targetDays,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        BoardButton(
            onClick = { onBoardClick(challenge.id, false) },
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(32.dp))
        ParticipationSection(
            participants = challenge.participants,
            maxParticipantCounts = challenge.maxParticipantCounts,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

@Composable
private fun MenuBottomSheet(
    isAuthor: Boolean,
    onShareClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss = onDismiss) {
        Spacer(modifier = Modifier.height(16.dp))
        ClickableText(
            text = stringResource(id = R.string.feature_waitingchallenge_menu_share),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.onSurface,
            onClick = onShareClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
        )
        ClickableText(
            text = stringResource(id = R.string.feature_waitingchallenge_menu_refresh),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.onSurface,
            onClick = onRefreshClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
        )
        if (isAuthor) {
            ClickableText(
                text = stringResource(id = R.string.feature_waitingchallenge_menu_delete),
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.None,
                style = MaterialTheme.typography.labelMedium,
                color = CustomColorProvider.colorScheme.red,
                onClick = onDeleteClick,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ParticipationSection(
    participants: List<User>,
    maxParticipantCounts: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(
                id = R.string.feature_waitingchallenge_participants_count,
                participants.size,
                maxParticipantCounts,
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = CustomColorProvider.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            participants.forEach { participant ->
                ParticipantItem(user = participant)
            }
        }
        Spacer(modifier = Modifier.height(150.dp))
    }
}

@Composable
private fun ParticipantItem(
    user: User,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UserProfile(tier = user.tier)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = user.name,
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.onBackground,
        )
    }
}

@Composable
private fun BoardButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(CustomColorProvider.colorScheme.surface)
            .clickableSingle(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(CustomColorProvider.colorScheme.background)
                .padding(10.dp),
            contentAlignment = Alignment.Center,
        ) {
            StableImage(
                drawableResId = com.yjy.common.designsystem.R.drawable.image_board,
                descriptionResId = R.string.feature_waitingchallenge_board,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(id = R.string.feature_waitingchallenge_board),
                style = MaterialTheme.typography.bodyMedium,
                color = CustomColorProvider.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.feature_waitingchallenge_board_description),
                style = MaterialTheme.typography.labelSmall,
                color = CustomColorProvider.colorScheme.onSurfaceMuted,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            imageVector = ImageVector.vectorResource(id = ChallengeTogetherIcons.ArrowRight),
            contentDescription = stringResource(R.string.feature_waitingchallenge_board_description),
            tint = CustomColorProvider.colorScheme.onSurface,
        )
    }
}

@Composable
private fun TargetDaySection(
    targetDays: TargetDays,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = CustomColorProvider.colorScheme.onBackgroundMuted.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.large,
            )
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(id = R.string.feature_waitingchallenge_target_day),
            style = MaterialTheme.typography.labelSmall,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Text(
            text = when (targetDays) {
                is TargetDays.Fixed -> stringResource(
                    id = R.string.feature_waitingchallenge_target_day_goal_days,
                    targetDays.days,
                )

                TargetDays.Infinite ->
                    stringResource(id = R.string.feature_waitingchallenge_target_day_unlimited)
            },
            style = MaterialTheme.typography.titleLarge,
            color = CustomColorProvider.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.End),
        )
    }
}

@Composable
private fun MenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            ImageVector.vectorResource(id = ChallengeTogetherIcons.MoreVertical),
            contentDescription = stringResource(id = R.string.feature_waitingchallenge_menu),
            tint = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
    }
}

@Composable
private fun AuthorSection(
    author: User,
    modifier: Modifier = Modifier,
) {
    val authorName = author.name
    val authorText = buildAnnotatedString {
        val fullText = stringResource(R.string.feature_waitingchallenge_created_by)
        val formattedText = fullText.format(authorName)
        val startQuoteIndex = formattedText.indexOf("\"")
        val endQuoteIndex = formattedText.lastIndexOf("\"") + 1

        append(formattedText)
        addStyle(
            style = SpanStyle(fontWeight = FontWeight.Bold),
            start = startQuoteIndex,
            end = endQuoteIndex,
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        UserProfile(tier = author.tier)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = authorText,
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
    }
}

@Composable
private fun UserProfile(
    tier: Tier,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.width(36.dp)) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(CustomColorProvider.colorScheme.surface)
                .padding(6.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircleMedal(tier = tier)
        }
    }
}

@Composable
private fun InfoText(
    challengeId: Int,
    isPrivate: Boolean,
    onPrivateClick: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isPrivate) {
            ClickableText(
                text = stringResource(id = R.string.feature_waitingchallenge_private),
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
                style = MaterialTheme.typography.labelSmall,
                onClick = onPrivateClick,
            )
        }
        Text(
            text = stringResource(R.string.feature_waitingchallenge_number, challengeId),
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun CategoryIcon(category: Category) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(CustomColorProvider.colorScheme.surface)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            ImageVector.vectorResource(id = category.getIconResId()),
            contentDescription = stringResource(id = category.getDisplayNameResId()),
            tint = CustomColorProvider.colorScheme.onSurfaceMuted,
            modifier = Modifier.size(20.dp),
        )
    }
}

@DevicePreviews
@Composable
fun WaitingChallengeScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            WaitingChallengeScreen(
                modifier = Modifier.fillMaxSize(),
                challengeDetail = ChallengeDetailUiState.Success(
                    DetailedWaitingChallenge(
                        id = 1,
                        title = "Challenge Title",
                        description = "Description",
                        category = Category.QUIT_DRUGS,
                        targetDays = TargetDays.Fixed(200),
                        password = "1234",
                        author = User(name = "author", tier = Tier.GOLD),
                        participants = listOf(
                            User(name = "participant1", tier = Tier.GOLD),
                            User(name = "participant2", tier = Tier.SILVER),
                            User(name = "participant3", tier = Tier.BRONZE),
                        ),
                        maxParticipantCounts = 5,
                        isAuthor = false,
                        isParticipated = true,
                    ),
                ),
            )
        }
    }
}
