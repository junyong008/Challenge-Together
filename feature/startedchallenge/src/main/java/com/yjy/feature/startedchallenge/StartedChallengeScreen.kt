package com.yjy.feature.startedchallenge

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.constants.ChallengeConst
import com.yjy.common.core.constants.TimeConst.SECONDS_PER_DAY
import com.yjy.common.core.extensions.clickableSingle
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.core.util.formatLocalDateTime
import com.yjy.common.core.util.formatTimeDuration
import com.yjy.common.designsystem.component.BaseBottomSheet
import com.yjy.common.designsystem.component.Calendar
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherButton
import com.yjy.common.designsystem.component.ChallengeTogetherDialog
import com.yjy.common.designsystem.component.ChallengeTogetherOutlinedButton
import com.yjy.common.designsystem.component.ChallengeTogetherTextField
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.ClickableText
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.component.PERCENT_MULTIPLIER
import com.yjy.common.designsystem.component.RoundedGradientProgressBar
import com.yjy.common.designsystem.component.SelectionMode
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.StableImage
import com.yjy.common.designsystem.extensions.getDisplayNameResId
import com.yjy.common.designsystem.extensions.getIconResId
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.startedchallenge.model.ChallengeDetailUiState
import com.yjy.feature.startedchallenge.model.StartedChallengeUiAction
import com.yjy.feature.startedchallenge.model.StartedChallengeUiEvent
import com.yjy.feature.startedchallenge.model.StartedChallengeUiState
import com.yjy.feature.startedchallenge.model.challengeOrNull
import com.yjy.feature.startedchallenge.model.isLoading
import com.yjy.model.challenge.DetailedStartedChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.TargetDays
import com.yjy.platform.widget.WidgetManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import java.time.LocalDateTime
import com.yjy.common.designsystem.R as designSystemR

@Composable
internal fun StartedChallengeRoute(
    onBackClick: () -> Unit,
    onEditCategoryClick: (challengeId: Int, category: Category) -> Unit,
    onEditTitleClick: (challengeId: Int, title: String, description: String) -> Unit,
    onEditTargetDaysClick: (challengeId: Int, targetDays: String, currentDays: Int) -> Unit,
    onResetRecordClick: (challengeId: Int) -> Unit,
    onBoardClick: (challengeId: Int, isEditable: Boolean) -> Unit,
    onRankingClick: (challengeId: Int) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StartedChallengeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val challengeDetail by viewModel.challengeDetail.collectAsStateWithLifecycle()

    StartedChallengeScreen(
        modifier = modifier,
        challengeDetail = challengeDetail,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onEditCategoryClick = onEditCategoryClick,
        onEditTitleClick = onEditTitleClick,
        onEditTargetDaysClick = onEditTargetDaysClick,
        onResetRecordClick = onResetRecordClick,
        onBoardClick = onBoardClick,
        onRankingClick = onRankingClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun StartedChallengeScreen(
    modifier: Modifier = Modifier,
    challengeDetail: ChallengeDetailUiState = ChallengeDetailUiState.Loading,
    uiState: StartedChallengeUiState = StartedChallengeUiState(),
    uiEvent: Flow<StartedChallengeUiEvent> = flowOf(),
    processAction: (StartedChallengeUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onEditCategoryClick: (challengeId: Int, category: Category) -> Unit = { _, _ -> },
    onEditTitleClick: (challengeId: Int, title: String, description: String) -> Unit = { _, _, _ -> },
    onEditTargetDaysClick: (challengeId: Int, targetDays: String, currentDays: Int) -> Unit = { _, _, _ -> },
    onResetRecordClick: (challengeId: Int) -> Unit = {},
    onBoardClick: (challengeId: Int, isEditable: Boolean) -> Unit = { _, _ -> },
    onRankingClick: (challengeId: Int) -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    var shouldShowResetBottomSheet by rememberSaveable { mutableStateOf(false) }
    var shouldShowMenuBottomSheet by remember { mutableStateOf(false) }
    var shouldShowDeleteConfirmDialog by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val context = LocalContext.current
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

    val errorMessages = mapOf(
        StartedChallengeUiEvent.LoadFailure.NotFound to stringResource(
            id = R.string.feature_startedchallenge_not_found,
        ),
        StartedChallengeUiEvent.LoadFailure.NotStarted to stringResource(
            id = R.string.feature_startedchallenge_not_started,
        ),
        StartedChallengeUiEvent.LoadFailure.NotParticipant to stringResource(
            id = R.string.feature_startedchallenge_not_participating,
        ),
        StartedChallengeUiEvent.LoadFailure.Network to stringResource(
            id = R.string.feature_startedchallenge_check_network_connection,
        ),
        StartedChallengeUiEvent.LoadFailure.Unknown to stringResource(
            id = R.string.feature_startedchallenge_error,
        ),
    )
    val resetSuccessMessage = stringResource(id = R.string.feature_startedchallenge_reset_successful)
    val resetFailureMessage = stringResource(id = R.string.feature_startedchallenge_reset_failed)
    val deleteSuccessMessage = stringResource(id = R.string.feature_startedchallenge_delete_successful)
    val deleteFailureMessage = stringResource(id = R.string.feature_startedchallenge_delete_failed)

    ObserveAsEvents(flow = uiEvent) { event ->
        when (event) {
            is StartedChallengeUiEvent.LoadFailure -> {
                onShowSnackbar(
                    SnackbarType.ERROR,
                    errorMessages[event] ?: errorMessages[StartedChallengeUiEvent.LoadFailure.Unknown]!!,
                )
                onBackClick()
            }

            StartedChallengeUiEvent.ResetSuccess ->
                onShowSnackbar(SnackbarType.SUCCESS, resetSuccessMessage)

            StartedChallengeUiEvent.ResetFailure ->
                onShowSnackbar(SnackbarType.ERROR, resetFailureMessage)

            StartedChallengeUiEvent.DeleteSuccess -> {
                onShowSnackbar(SnackbarType.SUCCESS, deleteSuccessMessage)
                onBackClick()
            }

            StartedChallengeUiEvent.DeleteFailure ->
                onShowSnackbar(SnackbarType.ERROR, deleteFailureMessage)
        }
    }

    LaunchedEffect(challengeDetail) {
        WidgetManager.updateAllWidgets(context)
    }

    if (challengeDetail.isLoading() || uiState.isDeleting) {
        LoadingWheel(modifier = Modifier.background(CustomColorProvider.colorScheme.background))
    } else {
        val challenge = challengeDetail.challengeOrNull() ?: return

        if (shouldShowResetBottomSheet) {
            ResetBottomSheet(
                onResetClick = { memo ->
                    shouldShowResetBottomSheet = false
                    processAction(StartedChallengeUiAction.OnResetClick(challenge.id, memo))
                },
                onDismiss = { shouldShowResetBottomSheet = false },
            )
        }

        if (shouldShowMenuBottomSheet) {
            MenuBottomSheet(
                enableEdit = challenge.currentParticipantCounts == 1 && !challenge.isCompleted,
                onEditCategoryClick = {
                    shouldShowMenuBottomSheet = false
                    onEditCategoryClick(challenge.id, challenge.category)
                },
                onEditTitleClick = {
                    shouldShowMenuBottomSheet = false
                    onEditTitleClick(challenge.id, challenge.title, challenge.description)
                },
                onEditTargetDaysClick = {
                    shouldShowMenuBottomSheet = false
                    onEditTargetDaysClick(
                        challenge.id,
                        challenge.targetDays.toRouteString(),
                        (challenge.currentRecordInSeconds / SECONDS_PER_DAY).toInt(),
                    )
                },
                onDeleteChallengeClick = { shouldShowDeleteConfirmDialog = true },
                onDismiss = { shouldShowMenuBottomSheet = false },
            )
        }

        if (shouldShowDeleteConfirmDialog) {
            ChallengeTogetherDialog(
                title = stringResource(id = R.string.feature_startedchallenge_delete_prompt),
                description = stringResource(id = R.string.feature_startedchallenge_delete_warning),
                positiveTextRes = R.string.feature_startedchallenge_confirm_delete,
                positiveTextColor = CustomColorProvider.colorScheme.red,
                onClickPositive = {
                    shouldShowDeleteConfirmDialog = false
                    shouldShowMenuBottomSheet = false
                    processAction(StartedChallengeUiAction.OnDeleteChallengeClick(challenge.id))
                },
                onClickNegative = { shouldShowDeleteConfirmDialog = false },
            )
        }

        CompositionLocalProvider(
            LocalOverscrollConfiguration provides null,
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = modifier
                    .fillMaxSize()
                    .background(CustomColorProvider.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
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
                                CategoryIcon(category = challenge.category)
                                MenuButton(onClick = { shouldShowMenuBottomSheet = true })
                            }
                        },
                    )
                }

                item {
                    ChallengeBody(
                        challenge = challenge,
                        isResetting = uiState.isResetting,
                        onResetButtonClick = { shouldShowResetBottomSheet = true },
                        onResetRecordClick = { onResetRecordClick(challenge.id) },
                        onBoardClick = { challengeId, isEditable ->
                            onBoardClick(challengeId, isEditable)
                        },
                        onRankingClick = { onRankingClick(challenge.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ChallengeBody(
    challenge: DetailedStartedChallenge,
    isResetting: Boolean,
    onResetButtonClick: () -> Unit,
    onResetRecordClick: (challengeId: Int) -> Unit,
    onBoardClick: (challengeId: Int, isEditable: Boolean) -> Unit,
    onRankingClick: (challengeId: Int) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        TitleWithDescription(
            title = challenge.title,
            description = challenge.description,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.Start),
        )
        Spacer(modifier = Modifier.height(50.dp))
        ChallengeImage(mode = challenge.mode)
        Spacer(modifier = Modifier.height(40.dp))
        if (challenge.targetDays is TargetDays.Fixed) {
            TargetProgress(
                currentSeconds = challenge.currentRecordInSeconds,
                targetDays = challenge.targetDays.toDays(),
                modifier = Modifier.padding(horizontal = 50.dp),
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        CurrentRecord(
            currentRecordInSeconds = challenge.currentRecordInSeconds,
            modifier = Modifier.padding(horizontal = 50.dp),
        )
        if (challenge.currentRecordInSeconds < challenge.targetDays.toDays() * SECONDS_PER_DAY) {
            Spacer(modifier = Modifier.height(20.dp))
            ResetButton(
                onClick = onResetButtonClick,
                enabled = !isResetting,
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        ChallengeButtons(
            rank = challenge.rank,
            currentParticipantCounts = challenge.currentParticipantCounts,
            onResetRecordClick = { onResetRecordClick(challenge.id) },
            onBoardClick = {
                onBoardClick(
                    challenge.id,
                    challenge.currentParticipantCounts == 1,
                )
            },
            onRankingClick = { onRankingClick(challenge.id) },
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(32.dp))
        ChallengeInfos(
            mode = challenge.mode,
            startDateTime = challenge.startDateTime,
            recentResetDateTime = challenge.recentResetDateTime,
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun MenuBottomSheet(
    enableEdit: Boolean,
    onEditCategoryClick: () -> Unit,
    onEditTitleClick: () -> Unit,
    onEditTargetDaysClick: () -> Unit,
    onDeleteChallengeClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss = onDismiss) {
        Spacer(modifier = Modifier.height(16.dp))
        if (enableEdit) {
            ClickableText(
                text = stringResource(id = R.string.feature_startedchallenge_edit_category),
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.None,
                style = MaterialTheme.typography.labelMedium,
                color = CustomColorProvider.colorScheme.onSurface,
                onClick = onEditCategoryClick,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp),
            )
            ClickableText(
                text = stringResource(id = R.string.feature_startedchallenge_edit_title),
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.None,
                style = MaterialTheme.typography.labelMedium,
                color = CustomColorProvider.colorScheme.onSurface,
                onClick = onEditTitleClick,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp),
            )
            ClickableText(
                text = stringResource(id = R.string.feature_startedchallenge_edit_target_days),
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.None,
                style = MaterialTheme.typography.labelMedium,
                color = CustomColorProvider.colorScheme.onSurface,
                onClick = onEditTargetDaysClick,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp),
            )
        }
        ClickableText(
            text = stringResource(id = R.string.feature_startedchallenge_delete_challenge),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.red,
            onClick = onDeleteChallengeClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ResetBottomSheet(
    onResetClick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var recordText by rememberSaveable { mutableStateOf("") }

    BaseBottomSheet(
        onDismiss = onDismiss,
        disableDragToDismiss = true,
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(id = R.string.feature_startedchallenge_reset_timer_prompt),
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.feature_startedchallenge_emotion_record_prompt),
            color = CustomColorProvider.colorScheme.onSurfaceMuted,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))
        ChallengeTogetherTextField(
            value = recordText,
            onValueChange = {
                recordText = it.take(ChallengeConst.MAX_RESET_RECORD_LENGTH)
            },
            placeholderText = stringResource(
                id = R.string.feature_startedchallenge_record_input_placeholder,
            ),
            contentAlignment = Alignment.TopStart,
            backgroundColor = CustomColorProvider.colorScheme.background,
            modifier = Modifier.height(140.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        ChallengeTogetherButton(
            onClick = { onResetClick(recordText) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(id = R.string.feature_startedchallenge_reset_button),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ChallengeInfos(
    mode: Mode,
    startDateTime: LocalDateTime,
    recentResetDateTime: LocalDateTime,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        ModeInfo(mode = mode)
        FoldableItem(titleResId = R.string.feature_startedchallenge_calender) {
            Calendar(
                selectionMode = SelectionMode.DateRange(
                    startDate = recentResetDateTime.toLocalDate(),
                    endDate = LocalDate.now(),
                ),
                showAdjacentMonthsDays = true,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            )
        }
        FoldableItem(titleResId = R.string.feature_startedchallenge_quote) {
            Quote()
        }
        FoldableItem(titleResId = R.string.feature_startedchallenge_challenge_info) {
            ChallengeInfo(
                startDateTime = startDateTime,
                recentResetDateTime = recentResetDateTime,
            )
        }
    }
}

@Composable
private fun ChallengeInfo(
    startDateTime: LocalDateTime,
    recentResetDateTime: LocalDateTime,
) {
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .clip(MaterialTheme.shapes.large)
            .background(CustomColorProvider.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(
                    id = R.string.feature_startedchallenge_start_time,
                ),
                style = MaterialTheme.typography.labelSmall,
                color = CustomColorProvider.colorScheme.onSurface,
            )
            Text(
                text = formatLocalDateTime(startDateTime),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.labelSmall,
                color = CustomColorProvider.colorScheme.onSurfaceMuted,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(
                    id = R.string.feature_startedchallenge_last_reset_time,
                ),
                style = MaterialTheme.typography.labelSmall,
                color = CustomColorProvider.colorScheme.onSurface,
            )
            Text(
                text = formatLocalDateTime(recentResetDateTime),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.labelSmall,
                color = CustomColorProvider.colorScheme.onSurfaceMuted,
            )
        }
    }
}

private const val QUOTE_COUNT = 53
private const val QUOTE_ANIMATION_DURATION = 300

@Composable
private fun Quote() {
    val totalQuotes = QUOTE_COUNT
    val quotes = (1..totalQuotes).map { index ->
        stringResource(
            id = R.string::class.java.getDeclaredField(
                "feature_startedchallenge_quote_$index",
            ).getInt(null),
        )
    }

    var currentQuoteIndex by remember {
        mutableIntStateOf(quotes.indices.random())
    }

    var alpha by remember { mutableFloatStateOf(1f) }

    val alphaAnimation by animateFloatAsState(
        targetValue = alpha,
        animationSpec = tween(QUOTE_ANIMATION_DURATION),
        label = "Quote Alpha Animation",
        finishedListener = {
            if (it == 0f) {
                currentQuoteIndex = (quotes.indices - currentQuoteIndex).random()
                alpha = 1f
            }
        },
    )

    Column {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(CustomColorProvider.colorScheme.surface)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                ImageVector.vectorResource(id = ChallengeTogetherIcons.QuoteLeft),
                contentDescription = stringResource(id = R.string.feature_startedchallenge_quote),
                tint = CustomColorProvider.colorScheme.onSurfaceMuted,
                modifier = Modifier.align(Alignment.Start),
            )
            Text(
                text = quotes[currentQuoteIndex],
                style = MaterialTheme.typography.labelMedium,
                color = CustomColorProvider.colorScheme.onSurface,
                modifier = Modifier
                    .padding(horizontal = 30.dp, vertical = 8.dp)
                    .graphicsLayer(alpha = alphaAnimation),
                textAlign = TextAlign.Center,
            )
            Icon(
                ImageVector.vectorResource(id = ChallengeTogetherIcons.QuoteRight),
                contentDescription = stringResource(id = R.string.feature_startedchallenge_quote),
                tint = CustomColorProvider.colorScheme.onSurfaceMuted,
                modifier = Modifier.align(Alignment.End),
            )
        }
        IconButton(
            onClick = {
                // 버튼 클릭 시 페이드 아웃 시작
                alpha = 0f
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 4.dp),
        ) {
            Icon(
                ImageVector.vectorResource(id = ChallengeTogetherIcons.Refresh),
                contentDescription = stringResource(
                    id = R.string.feature_startedchallenge_refresh_quote,
                ),
                tint = CustomColorProvider.colorScheme.onBackgroundMuted,
            )
        }
    }
}

@Composable
private fun ModeInfo(mode: Mode) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp),
    ) {
        Text(
            text = stringResource(id = R.string.feature_startedchallenge_mode),
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(
                id = when (mode) {
                    Mode.CHALLENGE -> R.string.feature_startedchallenge_challenge_mode
                    Mode.FREE -> R.string.feature_startedchallenge_free_mode
                },
            ),
            style = MaterialTheme.typography.labelSmall,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            ImageVector.vectorResource(
                id = when (mode) {
                    Mode.CHALLENGE -> ChallengeTogetherIcons.Trophy
                    Mode.FREE -> ChallengeTogetherIcons.TrophyOff
                },
            ),
            contentDescription = stringResource(
                id = when (mode) {
                    Mode.CHALLENGE -> R.string.feature_startedchallenge_challenge_mode
                    Mode.FREE -> R.string.feature_startedchallenge_free_mode
                },
            ),
            tint = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
    }
}

@Composable
private fun FoldableItem(
    @StringRes titleResId: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        HorizontalDivider(
            thickness = 1.dp,
            color = CustomColorProvider.colorScheme.divider,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(id = titleResId),
                style = MaterialTheme.typography.labelMedium,
                color = CustomColorProvider.colorScheme.onBackground,
                modifier = Modifier.padding(16.dp),
            )
            IconButton(onClick = { isExpanded = !isExpanded }) {
                Icon(
                    ImageVector.vectorResource(
                        id = if (isExpanded) {
                            ChallengeTogetherIcons.ArrowUp
                        } else {
                            ChallengeTogetherIcons.ArrowDown
                        },
                    ),
                    contentDescription = stringResource(id = titleResId),
                    tint = CustomColorProvider.colorScheme.onBackground,
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            content()
        }
    }
}

@Composable
private fun ChallengeButtons(
    rank: Int,
    currentParticipantCounts: Int,
    onResetRecordClick: () -> Unit,
    onBoardClick: () -> Unit,
    onRankingClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        BaseButton(
            onClick = onResetRecordClick,
            titleResId = R.string.feature_startedchallenge_reset_history,
            description = stringResource(id = R.string.feature_startedchallenge_reset_history_description),
            imageResId = designSystemR.drawable.image_scroll,
        )
        Spacer(modifier = Modifier.height(8.dp))
        BaseButton(
            onClick = onBoardClick,
            titleResId = R.string.feature_startedchallenge_board,
            description = stringResource(id = R.string.feature_startedchallenge_board_description),
            imageResId = designSystemR.drawable.image_board,
        )
        if (currentParticipantCounts > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            BaseButton(
                onClick = onRankingClick,
                titleResId = R.string.feature_startedchallenge_ranking,
                description = stringResource(
                    id = R.string.feature_startedchallenge_ranking_description,
                    rank,
                ),
                imageResId = designSystemR.drawable.image_rank,
            )
        }
    }
}

@Composable
private fun BaseButton(
    onClick: () -> Unit,
    @StringRes titleResId: Int,
    description: String,
    @DrawableRes imageResId: Int,
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
                drawableResId = imageResId,
                descriptionResId = titleResId,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(id = titleResId),
                style = MaterialTheme.typography.bodyMedium,
                color = CustomColorProvider.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = CustomColorProvider.colorScheme.onSurfaceMuted,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            imageVector = ImageVector.vectorResource(id = ChallengeTogetherIcons.ArrowRight),
            contentDescription = description,
            tint = CustomColorProvider.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ResetButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    ChallengeTogetherOutlinedButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.extraLarge,
        borderColor = CustomColorProvider.colorScheme.onBackgroundMuted.copy(alpha = 0.7f),
        contentColor = CustomColorProvider.colorScheme.onBackgroundMuted.copy(alpha = 0.7f),
        content = {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.alpha(if (enabled) 1f else 0f),
                ) {
                    Icon(
                        ImageVector.vectorResource(id = ChallengeTogetherIcons.Refresh),
                        contentDescription = stringResource(
                            id = R.string.feature_startedchallenge_reset,
                        ),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.feature_startedchallenge_reset),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                    )
                }

                if (!enabled) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = CustomColorProvider.colorScheme.brand,
                    )
                }
            }
        },
    )
}

@Composable
private fun CurrentRecord(
    currentRecordInSeconds: Long,
    modifier: Modifier = Modifier,
) {
    val formattedRecord: String = formatTimeDuration(currentRecordInSeconds)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(CustomColorProvider.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = formattedRecord,
            color = CustomColorProvider.colorScheme.red,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.feature_startedchallenge_current_record),
            color = CustomColorProvider.colorScheme.onSurfaceMuted,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun TargetProgress(
    currentSeconds: Long,
    targetDays: Int,
    modifier: Modifier = Modifier,
) {
    val targetSeconds = targetDays * SECONDS_PER_DAY
    val progress = (currentSeconds.toFloat() / targetSeconds).coerceIn(0f, 1f)

    val leftDaysText = if (currentSeconds >= targetDays * SECONDS_PER_DAY) {
        stringResource(id = R.string.feature_startedchallenge_completed)
    } else {
        val currentDays = currentSeconds / SECONDS_PER_DAY
        val remainingDays = targetDays - currentDays
        stringResource(
            id = R.string.feature_startedchallenge_days_left,
            remainingDays,
        )
    }

    val progressPercent = (progress * PERCENT_MULTIPLIER).toInt()

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = stringResource(
                    id = R.string.feature_startedchallenge_goal_days,
                    targetDays,
                ),
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$progressPercent",
                textAlign = TextAlign.End,
                color = CustomColorProvider.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 2.dp),
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "%",
                textAlign = TextAlign.End,
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
        RoundedGradientProgressBar(
            progress = { progress },
            modifier = Modifier
                .height(15.dp)
                .fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = leftDaysText,
            textAlign = TextAlign.End,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.End),
        )
    }
}

@Composable
private fun ChallengeImage(mode: Mode) {
    when (mode) {
        Mode.CHALLENGE -> {
            StableImage(
                drawableResId = designSystemR.drawable.image_trophy,
                descriptionResId = R.string.feature_startedchallenge_mode_challenge,
                modifier = Modifier.size(165.dp),
            )
        }
        Mode.FREE -> {
            StableImage(
                drawableResId = designSystemR.drawable.image_calendar,
                descriptionResId = R.string.feature_startedchallenge_mode_free,
                modifier = Modifier
                    .size(165.dp)
                    .padding(10.dp),
            )
        }
    }
}

@Composable
private fun TitleWithDescription(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                color = CustomColorProvider.colorScheme.onBackground,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .weight(1f, fill = false),
            )
            Spacer(modifier = Modifier.width(4.dp))
            ClickableText(
                text = stringResource(
                    id = if (isExpanded) {
                        R.string.feature_startedchallenge_collapse
                    } else {
                        R.string.feature_startedchallenge_expand
                    },
                ),
                onClick = { isExpanded = !isExpanded },
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.labelMedium,
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
            )
        }
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

@Composable
private fun MenuButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            ImageVector.vectorResource(id = ChallengeTogetherIcons.MoreVertical),
            contentDescription = stringResource(
                id = R.string.feature_startedchallenge_menu_button,
            ),
            tint = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
    }
}

@DevicePreviews
@Composable
fun StartedChallengeScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            StartedChallengeScreen(
                modifier = Modifier.fillMaxSize(),
                challengeDetail = ChallengeDetailUiState.Success(
                    DetailedStartedChallenge(
                        id = 1,
                        title = "Challenge Title",
                        description = "Description",
                        category = Category.QUIT_DRUGS,
                        targetDays = TargetDays.Fixed(200),
                        currentParticipantCounts = 5,
                        recentResetDateTime = LocalDateTime.now(),
                        mode = Mode.CHALLENGE,
                        isCompleted = false,
                        rank = 1,
                        startDateTime = LocalDateTime.now(),
                        currentRecordInSeconds = 1000L,
                    ),
                ),
            )
        }
    }
}
