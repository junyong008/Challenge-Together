package com.yjy.feature.startedchallenge

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.constants.ChallengeConst
import com.yjy.common.core.constants.ChallengeConst.MAX_REASON_TO_START_COUNT
import com.yjy.common.core.constants.ChallengeConst.MAX_REASON_TO_START_LENGTH
import com.yjy.common.core.constants.TimeConst.SECONDS_PER_DAY
import com.yjy.common.core.extensions.clickableSingle
import com.yjy.common.core.util.NavigationAnimation.fadeIn
import com.yjy.common.core.util.NavigationAnimation.fadeOut
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.core.util.formatLocalDateTime
import com.yjy.common.core.util.formatTimeDuration
import com.yjy.common.designsystem.component.BaseBottomSheet
import com.yjy.common.designsystem.component.BulletText
import com.yjy.common.designsystem.component.Calendar
import com.yjy.common.designsystem.component.CalendarDialog
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
import com.yjy.common.designsystem.component.TextInputDialog
import com.yjy.common.designsystem.extensions.getDisplayNameResId
import com.yjy.common.designsystem.extensions.getIconResId
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.startedchallenge.model.ChallengeDetailUiState
import com.yjy.feature.startedchallenge.model.StartReasonsUiState
import com.yjy.feature.startedchallenge.model.StartedChallengeUiAction
import com.yjy.feature.startedchallenge.model.StartedChallengeUiEvent
import com.yjy.feature.startedchallenge.model.StartedChallengeUiState
import com.yjy.feature.startedchallenge.model.challengeOrNull
import com.yjy.feature.startedchallenge.model.isLoading
import com.yjy.feature.startedchallenge.model.reasonsOrEmpty
import com.yjy.model.challenge.DetailedStartedChallenge
import com.yjy.model.challenge.StartReason
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.TargetDays
import com.yjy.platform.widget.WidgetManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import java.time.LocalDateTime
import com.yjy.common.designsystem.R as designSystemR

@Composable
internal fun StartedChallengeRoute(
    onBackClick: () -> Unit,
    onResetClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditCategoryClick: (challengeId: Int, category: Category) -> Unit,
    onEditTitleClick: (challengeId: Int, title: String, description: String) -> Unit,
    onEditTargetDaysClick: (challengeId: Int, targetDays: String, currentDays: Int) -> Unit,
    onResetRecordClick: (challengeId: Int) -> Unit,
    onBoardClick: (challengeId: Int, isEditable: Boolean) -> Unit,
    onRankingClick: (challengeId: Int) -> Unit,
    onProgressClick: (challengeId: Int) -> Unit,
    onRewardClick: (challengeId: Int) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StartedChallengeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val startReasonsState by viewModel.startReasonsState.collectAsStateWithLifecycle()
    val challengeDetail by viewModel.challengeDetail.collectAsStateWithLifecycle()

    StartedChallengeScreen(
        modifier = modifier,
        challengeDetail = challengeDetail,
        startReasonsState = startReasonsState,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onResetClick = onResetClick,
        onDeleteClick = onDeleteClick,
        onEditCategoryClick = onEditCategoryClick,
        onEditTitleClick = onEditTitleClick,
        onEditTargetDaysClick = onEditTargetDaysClick,
        onResetRecordClick = onResetRecordClick,
        onBoardClick = onBoardClick,
        onRankingClick = onRankingClick,
        onProgressClick = onProgressClick,
        onRewardClick = onRewardClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun StartedChallengeScreen(
    modifier: Modifier = Modifier,
    challengeDetail: ChallengeDetailUiState = ChallengeDetailUiState.Loading,
    startReasonsState: StartReasonsUiState = StartReasonsUiState.Loading,
    uiState: StartedChallengeUiState = StartedChallengeUiState(),
    uiEvent: Flow<StartedChallengeUiEvent> = flowOf(),
    processAction: (StartedChallengeUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onResetClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onEditCategoryClick: (challengeId: Int, category: Category) -> Unit = { _, _ -> },
    onEditTitleClick: (challengeId: Int, title: String, description: String) -> Unit = { _, _, _ -> },
    onEditTargetDaysClick: (challengeId: Int, targetDays: String, currentDays: Int) -> Unit = { _, _, _ -> },
    onResetRecordClick: (challengeId: Int) -> Unit = {},
    onBoardClick: (challengeId: Int, isEditable: Boolean) -> Unit = { _, _ -> },
    onRankingClick: (challengeId: Int) -> Unit = {},
    onProgressClick: (challengeId: Int) -> Unit = {},
    onRewardClick: (challengeId: Int) -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    var shouldShowResetBottomSheet by rememberSaveable { mutableStateOf(false) }
    var shouldShowMenuBottomSheet by remember { mutableStateOf(false) }
    var shouldShowRemindDialog by remember { mutableStateOf(false) }
    var shouldShowAddReasonDialog by remember { mutableStateOf(false) }
    var shouldShowDeleteConfirmDialog by remember { mutableStateOf(false) }
    var shouldShowCalendarDialog by rememberSaveable { mutableStateOf(false) }
    var shouldShowContinueDialog by rememberSaveable { mutableStateOf(false) }
    var selectedReasonToDelete by remember { mutableStateOf<StartReason?>(null) }

    var inputReasonToStart by rememberSaveable { mutableStateOf("") }
    var resetDateTime by rememberSaveable { mutableStateOf(LocalDateTime.now()) }

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
    val continueSuccessMessage = stringResource(id = R.string.feature_startedchallenge_continue_successful)
    val continueFailureMessage = stringResource(id = R.string.feature_startedchallenge_continue_failed)
    val editReasonFailureMessage = stringResource(id = R.string.feature_startedchallenge_edit_reason_failed)

    ObserveAsEvents(flow = uiEvent) { event ->
        when (event) {
            is StartedChallengeUiEvent.LoadFailure -> {
                onShowSnackbar(
                    SnackbarType.ERROR,
                    errorMessages[event] ?: errorMessages[StartedChallengeUiEvent.LoadFailure.Unknown]!!,
                )
                onBackClick()
            }

            StartedChallengeUiEvent.ResetSuccess -> {
                onResetClick()
                onShowSnackbar(SnackbarType.SUCCESS, resetSuccessMessage)
            }

            StartedChallengeUiEvent.ResetFailure ->
                onShowSnackbar(SnackbarType.ERROR, resetFailureMessage)

            StartedChallengeUiEvent.DeleteSuccess -> {
                onDeleteClick()
                onShowSnackbar(SnackbarType.SUCCESS, deleteSuccessMessage)
                onBackClick()
            }

            StartedChallengeUiEvent.DeleteFailure ->
                onShowSnackbar(SnackbarType.ERROR, deleteFailureMessage)

            StartedChallengeUiEvent.ContinueSuccess ->
                onShowSnackbar(SnackbarType.SUCCESS, continueSuccessMessage)

            StartedChallengeUiEvent.ContinueFailure ->
                onShowSnackbar(SnackbarType.ERROR, continueFailureMessage)

            StartedChallengeUiEvent.EditReasonToStartFailure -> {
                shouldShowRemindDialog = false
                onShowSnackbar(SnackbarType.ERROR, editReasonFailureMessage)
            }
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
                resetDateTime = resetDateTime,
                onResetClick = { memo ->
                    shouldShowResetBottomSheet = false
                    processAction(
                        StartedChallengeUiAction.OnResetClick(
                            challengeId = challenge.id,
                            resetDateTime = resetDateTime,
                            memo = memo,
                        ),
                    )
                },
                onEditTimeClick = {
                    shouldShowCalendarDialog = true
                },
                onDismiss = { shouldShowResetBottomSheet = false },
            )
        }

        if (shouldShowCalendarDialog) {
            CalendarDialog(
                initialDateTime = resetDateTime,
                outRangeText = stringResource(id = R.string.feature_startedchallenge_invalid_reset_date),
                onDismissRequest = { shouldShowCalendarDialog = false },
                onConfirmEdit = {
                    resetDateTime = it
                    shouldShowCalendarDialog = false
                },
                minDateTime = challenge.recentResetDateTime,
                maxDateTime = LocalDateTime.now(),
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

        if (shouldShowAddReasonDialog) {
            TextInputDialog(
                value = inputReasonToStart,
                title = stringResource(id = R.string.feature_startedchallenge_reason_to_start),
                description = stringResource(id = R.string.feature_startedchallenge_remind_description),
                maxTextLength = MAX_REASON_TO_START_LENGTH,
                placeholder = stringResource(id = R.string.feature_startedchallenge_reason_placeholder),
                onValueChange = { inputReasonToStart = it.take(MAX_REASON_TO_START_LENGTH) },
                enableConfirmButton = inputReasonToStart.isNotBlank(),
                onConfirm = { reason ->
                    shouldShowAddReasonDialog = false
                    processAction(StartedChallengeUiAction.OnAddReasonToStart(challenge.id, reason))
                },
                onClickNegative = { shouldShowAddReasonDialog = false },
            )
        }

        if (selectedReasonToDelete != null) {
            ChallengeTogetherDialog(
                title = stringResource(id = R.string.feature_startedchallenge_delete_reason_title),
                description = stringResource(
                    id = R.string.feature_startedchallenge_delete_reason_description,
                    selectedReasonToDelete!!.value,
                ),
                positiveTextRes = R.string.feature_startedchallenge_confirm_delete,
                positiveTextColor = CustomColorProvider.colorScheme.red,
                onClickPositive = {
                    processAction(
                        StartedChallengeUiAction.OnDeleteReasonToStart(reasonId = selectedReasonToDelete!!.id),
                    )
                    selectedReasonToDelete = null
                },
                onClickNegative = { selectedReasonToDelete = null },
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

        if (shouldShowContinueDialog) {
            ChallengeTogetherDialog(
                title = stringResource(id = R.string.feature_startedchallenge_continue_title),
                description = stringResource(id = R.string.feature_startedchallenge_continue_description),
                positiveTextRes = R.string.feature_startedchallenge_continue,
                onClickPositive = {
                    shouldShowContinueDialog = false
                    processAction(StartedChallengeUiAction.OnContinueChallengeClick(challenge.id))
                },
                onClickNegative = { shouldShowContinueDialog = false },
            )
        }

        AnimatedRemindDialog(
            showDialog = shouldShowRemindDialog,
            reasons = startReasonsState.reasonsOrEmpty(),
            isLoading = startReasonsState.isLoading() || uiState.isEditingReasonToStart,
            onAddReasonClick = {
                inputReasonToStart = ""
                shouldShowAddReasonDialog = true
            },
            onDeleteReasonClick = { reason ->
                selectedReasonToDelete = reason
            },
            onDismiss = { shouldShowRemindDialog = false },
        )

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
                        isContinuing = uiState.isContinuing,
                        shouldShowRemindDialog = shouldShowRemindDialog,
                        onResetButtonClick = {
                            resetDateTime = LocalDateTime.now()
                            shouldShowResetBottomSheet = true
                        },
                        onRemindButtonClick = {
                            shouldShowRemindDialog = true
                            context.vibrateClickFeedback()
                        },
                        onContinueButtonClick = {
                            shouldShowContinueDialog = true
                        },
                        onResetRecordClick = { onResetRecordClick(challenge.id) },
                        onBoardClick = { challengeId, isEditable ->
                            onBoardClick(challengeId, isEditable)
                        },
                        onRankingClick = { onRankingClick(challenge.id) },
                        onProgressClick = { onProgressClick(challenge.id) },
                        onRewardClick = { onRewardClick(challenge.id) },
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
    isContinuing: Boolean,
    shouldShowRemindDialog: Boolean,
    onResetButtonClick: () -> Unit,
    onRemindButtonClick: () -> Unit,
    onContinueButtonClick: () -> Unit,
    onResetRecordClick: (challengeId: Int) -> Unit,
    onBoardClick: (challengeId: Int, isEditable: Boolean) -> Unit,
    onRankingClick: (challengeId: Int) -> Unit,
    onProgressClick: (challengeId: Int) -> Unit,
    onRewardClick: (challengeId: Int) -> Unit,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                ResetButton(
                    onClick = onResetButtonClick,
                    enabled = !isResetting,
                )
                Spacer(modifier = Modifier.width(8.dp))
                RemindButton(
                    onClick = onRemindButtonClick,
                    shouldShowRemindDialog = shouldShowRemindDialog,
                )
            }
        } else if (challenge.currentParticipantCounts <= 1) {
            Spacer(modifier = Modifier.height(20.dp))
            ContinueButton(
                onClick = onContinueButtonClick,
                enabled = !isContinuing,
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        ChallengeButtons(
            rank = challenge.rank,
            scoreRank = challenge.scoreRank,
            currentParticipantCounts = challenge.currentParticipantCounts,
            onResetRecordClick = { onResetRecordClick(challenge.id) },
            onBoardClick = {
                onBoardClick(
                    challenge.id,
                    challenge.currentParticipantCounts == 1,
                )
            },
            onRankingClick = { onRankingClick(challenge.id) },
            onProgressClick = { onProgressClick(challenge.id) },
            onRewardClick = { onRewardClick(challenge.id) },
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
    resetDateTime: LocalDateTime,
    onResetClick: (String) -> Unit,
    onEditTimeClick: () -> Unit,
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
        Spacer(modifier = Modifier.height(28.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clip(MaterialTheme.shapes.medium)
                .background(CustomColorProvider.colorScheme.background),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.feature_startedchallenge_reset_time),
                style = MaterialTheme.typography.bodySmall,
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
            )
            VerticalDivider(
                thickness = 1.dp,
                color = CustomColorProvider.colorScheme.divider,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
            )
            Text(
                text = formatLocalDateTime(resetDateTime),
                style = MaterialTheme.typography.bodySmall,
                color = CustomColorProvider.colorScheme.onBackground,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
            )
            Icon(
                imageVector = ImageVector.vectorResource(ChallengeTogetherIcons.EditCalendar),
                contentDescription = stringResource(R.string.feature_startedchallenge_edit_reset_time),
                tint = CustomColorProvider.colorScheme.onSurface,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(CustomColorProvider.colorScheme.surface)
                    .clickable(onClick = onEditTimeClick)
                    .padding(8.dp)
                    .size(20.dp),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
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

private const val ANIMATED_REMIND_DIALOG_CLOSE_DELAY = 200
private const val QUOTE_ANIMATION_DURATION = 300

@Composable
fun AnimatedRemindDialog(
    showDialog: Boolean,
    reasons: List<StartReason>,
    isLoading: Boolean,
    onAddReasonClick: () -> Unit,
    onDeleteReasonClick: (reason: StartReason) -> Unit,
    onDismiss: () -> Unit,
) {
    var showAnimatedDialog by remember { mutableStateOf(false) }
    var animateIn by remember { mutableStateOf(false) }

    LaunchedEffect(showDialog) {
        if (!showDialog) {
            animateIn = false
            delay(ANIMATED_REMIND_DIALOG_CLOSE_DELAY.toLong())
            showAnimatedDialog = false
        } else {
            showAnimatedDialog = true
        }
    }

    LaunchedEffect(showAnimatedDialog) {
        if (showAnimatedDialog) { animateIn = true }
    }

    if (!showAnimatedDialog) return

    val quotesResIds = remember {
        listOf(
            R.string.feature_startedchallenge_quote_1,
            R.string.feature_startedchallenge_quote_2,
            R.string.feature_startedchallenge_quote_3,
            R.string.feature_startedchallenge_quote_4,
            R.string.feature_startedchallenge_quote_5,
            R.string.feature_startedchallenge_quote_6,
            R.string.feature_startedchallenge_quote_7,
            R.string.feature_startedchallenge_quote_8,
            R.string.feature_startedchallenge_quote_9,
            R.string.feature_startedchallenge_quote_10,
            R.string.feature_startedchallenge_quote_11,
            R.string.feature_startedchallenge_quote_12,
            R.string.feature_startedchallenge_quote_13,
            R.string.feature_startedchallenge_quote_14,
            R.string.feature_startedchallenge_quote_15,
            R.string.feature_startedchallenge_quote_16,
            R.string.feature_startedchallenge_quote_17,
            R.string.feature_startedchallenge_quote_18,
            R.string.feature_startedchallenge_quote_19,
            R.string.feature_startedchallenge_quote_20,
            R.string.feature_startedchallenge_quote_21,
            R.string.feature_startedchallenge_quote_22,
            R.string.feature_startedchallenge_quote_23,
            R.string.feature_startedchallenge_quote_24,
            R.string.feature_startedchallenge_quote_25,
            R.string.feature_startedchallenge_quote_26,
            R.string.feature_startedchallenge_quote_27,
            R.string.feature_startedchallenge_quote_28,
            R.string.feature_startedchallenge_quote_29,
            R.string.feature_startedchallenge_quote_30,
            R.string.feature_startedchallenge_quote_31,
            R.string.feature_startedchallenge_quote_32,
            R.string.feature_startedchallenge_quote_33,
            R.string.feature_startedchallenge_quote_34,
            R.string.feature_startedchallenge_quote_35,
            R.string.feature_startedchallenge_quote_36,
            R.string.feature_startedchallenge_quote_37,
            R.string.feature_startedchallenge_quote_38,
            R.string.feature_startedchallenge_quote_39,
            R.string.feature_startedchallenge_quote_40,
            R.string.feature_startedchallenge_quote_41,
            R.string.feature_startedchallenge_quote_42,
            R.string.feature_startedchallenge_quote_43,
            R.string.feature_startedchallenge_quote_44,
            R.string.feature_startedchallenge_quote_45,
            R.string.feature_startedchallenge_quote_46,
            R.string.feature_startedchallenge_quote_47,
            R.string.feature_startedchallenge_quote_48,
            R.string.feature_startedchallenge_quote_49,
            R.string.feature_startedchallenge_quote_50,
            R.string.feature_startedchallenge_quote_51,
            R.string.feature_startedchallenge_quote_52,
            R.string.feature_startedchallenge_quote_53,
        )
    }
    val quotes = quotesResIds.map { resId -> stringResource(id = resId) }
    var currentQuoteIndex by remember {
        mutableIntStateOf(quotes.indices.random())
    }

    var quoteAlpha by remember { mutableFloatStateOf(1f) }
    val alphaAnimation by animateFloatAsState(
        targetValue = if (quoteAlpha.isFinite()) quoteAlpha else 1f,
        animationSpec = tween(QUOTE_ANIMATION_DURATION),
        label = "Quote Alpha Animation",
        finishedListener = {
            if (it == 0f) {
                currentQuoteIndex = (quotes.indices - currentQuoteIndex).random()
                quoteAlpha = 1f
            }
        },
    )

    Dialog(onDismissRequest = onDismiss) {
        AnimatedVisibility(
            visible = animateIn,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight / 4 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            ) + scaleIn(
                initialScale = 0.6f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            ) + fadeIn(
                animationSpec = tween(durationMillis = 300),
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight / 4 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            ) + scaleOut(
                targetScale = 0.6f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            ) + fadeOut(
                animationSpec = tween(durationMillis = ANIMATED_REMIND_DIALOG_CLOSE_DELAY),
            ),
        ) {
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .background(CustomColorProvider.colorScheme.surface)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                StableImage(
                    drawableResId = R.drawable.image_heart,
                    descriptionResId = R.string.feature_startedchallenge_remind,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(90.dp),
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(id = R.string.feature_startedchallenge_remind),
                    color = CustomColorProvider.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.feature_startedchallenge_remind_description),
                    color = CustomColorProvider.colorScheme.onSurfaceMuted,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(28.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                        .background(CustomColorProvider.colorScheme.background),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.feature_startedchallenge_reason_to_start),
                        style = MaterialTheme.typography.bodyMedium,
                        color = CustomColorProvider.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        textAlign = TextAlign.Start,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(modifier = Modifier.alpha(if (isLoading) 0f else 1f)) {
                            reasons.forEach { reason ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .clickable { onDeleteReasonClick(reason) }
                                        .padding(horizontal = 4.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    BulletText(
                                        text = reason.value,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = CustomColorProvider.colorScheme.onBackground,
                                        bulletColor = CustomColorProvider.colorScheme.onBackgroundMuted,
                                    )
                                }
                            }

                            if (reasons.size < MAX_REASON_TO_START_COUNT) {
                                if (reasons.isNotEmpty()) {
                                    HorizontalDivider(
                                        color = CustomColorProvider.colorScheme.divider,
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(
                                            start = 16.dp,
                                            end = 16.dp,
                                            top = 16.dp,
                                            bottom = 4.dp,
                                        ),
                                    )
                                }
                                AddReasonToStartButton(onClick = onAddReasonClick)
                                Spacer(modifier = Modifier.height(4.dp))
                            } else {
                                Spacer(modifier = Modifier.height(14.dp))
                            }
                        }

                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 3.dp,
                                color = CustomColorProvider.colorScheme.brand,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                        .background(CustomColorProvider.colorScheme.background)
                        .clickable { quoteAlpha = 0f }
                        .padding(16.dp)
                        .animateContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_startedchallenge_today_quote),
                        style = MaterialTheme.typography.bodyMedium,
                        color = CustomColorProvider.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Icon(
                        ImageVector.vectorResource(id = ChallengeTogetherIcons.QuoteLeft),
                        contentDescription = stringResource(id = R.string.feature_startedchallenge_quote),
                        tint = CustomColorProvider.colorScheme.onBackgroundMuted,
                        modifier = Modifier.align(Alignment.Start),
                    )
                    Text(
                        text = quotes[currentQuoteIndex],
                        style = MaterialTheme.typography.labelMedium,
                        color = CustomColorProvider.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(horizontal = 30.dp, vertical = 8.dp)
                            .graphicsLayer(alpha = alphaAnimation),
                        textAlign = TextAlign.Center,
                    )
                    Icon(
                        ImageVector.vectorResource(id = ChallengeTogetherIcons.QuoteRight),
                        contentDescription = stringResource(id = R.string.feature_startedchallenge_quote),
                        tint = CustomColorProvider.colorScheme.onBackgroundMuted,
                        modifier = Modifier.align(Alignment.End),
                    )
                }
            }
        }
    }
}

@Composable
fun AddReasonToStartButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(ChallengeTogetherIcons.AddCircle),
            contentDescription = stringResource(R.string.feature_startedchallenge_emotion_record_prompt),
            tint = CustomColorProvider.colorScheme.onBackgroundMuted,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.feature_startedchallenge_reason_to_start),
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
            style = MaterialTheme.typography.bodySmall,
        )
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
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
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
    scoreRank: Int,
    currentParticipantCounts: Int,
    onResetRecordClick: () -> Unit,
    onBoardClick: () -> Unit,
    onRankingClick: () -> Unit,
    onProgressClick: () -> Unit,
    onRewardClick: () -> Unit,
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
            onClick = onProgressClick,
            titleResId = R.string.feature_startedchallenge_progress,
            description = stringResource(id = R.string.feature_startedchallenge_progress_description),
            imageResId = designSystemR.drawable.image_compass,
        )
        Spacer(modifier = Modifier.height(8.dp))
        BaseButton(
            onClick = onRewardClick,
            titleResId = R.string.feature_startedchallenge_reward,
            description = stringResource(id = R.string.feature_startedchallenge_reward_description),
            imageResId = designSystemR.drawable.image_money,
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
                    scoreRank,
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
                        style = MaterialTheme.typography.bodyMedium,
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
private fun RemindButton(
    shouldShowRemindDialog: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = CustomColorProvider.colorScheme.onBackgroundMuted.copy(alpha = 0.7f),
                shape = CircleShape,
            )
            .clickable(onClick = onClick)
            .padding(15.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = ChallengeTogetherIcons.LikeOn),
            contentDescription = stringResource(id = R.string.feature_startedchallenge_remind),
            tint = if (shouldShowRemindDialog) {
                CustomColorProvider.colorScheme.red
            } else {
                CustomColorProvider.colorScheme.onBackgroundMuted.copy(alpha = 0.7f)
            },
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun ContinueButton(
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
                        ImageVector.vectorResource(id = ChallengeTogetherIcons.Play),
                        contentDescription = stringResource(
                            id = R.string.feature_startedchallenge_continue,
                        ),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.feature_startedchallenge_continue),
                        style = MaterialTheme.typography.bodyMedium,
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
                useSingleClick = false,
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

private fun Context.vibrateClickFeedback() {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(10, 255))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(10)
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
                        scoreRank = 2,
                        startDateTime = LocalDateTime.now(),
                        currentRecordInSeconds = 1000L,
                    ),
                ),
            )
        }
    }
}
