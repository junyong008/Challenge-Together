package com.yjy.feature.editchallenge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_TARGET_DAYS
import com.yjy.common.core.constants.ChallengeConst.MIN_CHALLENGE_TARGET_DAYS
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherDialog
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.common.ui.TargetDay
import com.yjy.feature.editchallenge.component.ConfirmButton
import com.yjy.feature.editchallenge.model.EditChallengeUiAction
import com.yjy.feature.editchallenge.model.EditChallengeUiEvent
import com.yjy.feature.editchallenge.model.EditChallengeUiState
import com.yjy.model.challenge.core.TargetDays
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun EditTargetDayRoute(
    challengeId: String,
    targetDays: TargetDays,
    currentRecordDays: Int,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditChallengeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EditTargetDayScreen(
        modifier = modifier,
        challengeId = challengeId,
        targetDays = targetDays,
        currentRecordDays = currentRecordDays,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun EditTargetDayScreen(
    modifier: Modifier = Modifier,
    challengeId: String = "",
    targetDays: TargetDays = TargetDays.Infinite,
    currentRecordDays: Int = 0,
    uiState: EditChallengeUiState = EditChallengeUiState(),
    uiEvent: Flow<EditChallengeUiEvent> = flowOf(),
    processAction: (EditChallengeUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    var currentTargetDays by remember { mutableStateOf(targetDays) }
    var shouldShowConfirmDialog by remember { mutableStateOf(false) }

    val editSuccessMessage = stringResource(id = R.string.feature_editchallenge_edit_successful)
    val editFailedMessage = stringResource(id = R.string.feature_editchallenge_edit_failed)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            is EditChallengeUiEvent.EditSuccess -> {
                onShowSnackbar(SnackbarType.SUCCESS, editSuccessMessage)
                onBackClick()
            }

            is EditChallengeUiEvent.EditFailure -> onShowSnackbar(SnackbarType.ERROR, editFailedMessage)
        }
    }

    if (shouldShowConfirmDialog) {
        ChallengeTogetherDialog(
            title = stringResource(id = R.string.feature_editchallenge_record_exceeded_notice),
            description = stringResource(
                id = R.string.feature_editchallenge_record_exceeded_description,
                currentRecordDays - currentTargetDays.toDays(),
            ),
            positiveTextRes = R.string.feature_editchallenge_complete,
            onClickPositive = {
                shouldShowConfirmDialog = false
                processAction(
                    EditChallengeUiAction.OnEditTargetDays(
                        challengeId = challengeId,
                        targetDays = currentTargetDays,
                    ),
                )
            },
            onClickNegative = { shouldShowConfirmDialog = false },
        )
    }

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                onNavigationClick = onBackClick,
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
            ) {
                ConfirmButton(
                    onClick = {
                        if (currentTargetDays.toDays() <= currentRecordDays) {
                            shouldShowConfirmDialog = true
                            return@ConfirmButton
                        }

                        processAction(
                            EditChallengeUiAction.OnEditTargetDays(
                                challengeId = challengeId,
                                targetDays = currentTargetDays,
                            ),
                        )
                    },
                    enabled = !uiState.isEditing,
                )
            }
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
                titleRes = R.string.feature_editchallenge_edit_category_title,
                descriptionRes = R.string.feature_editchallenge_edit_category_description,
            )
            Spacer(modifier = Modifier.height(100.dp))
            TargetDay(
                targetDays = currentTargetDays,
                onTargetDaysUpdated = {
                    if (it is TargetDays.Fixed) {
                        val adjustedDays = it.days.coerceIn(
                            MIN_CHALLENGE_TARGET_DAYS,
                            MAX_CHALLENGE_TARGET_DAYS,
                        )
                        currentTargetDays = TargetDays.Fixed(adjustedDays)
                    } else {
                        currentTargetDays = it
                    }
                },
                enabled = !uiState.isEditing,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@DevicePreviews
@Composable
fun EditTargetDayScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            EditTargetDayScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
