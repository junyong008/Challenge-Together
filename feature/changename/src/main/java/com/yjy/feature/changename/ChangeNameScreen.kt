package com.yjy.feature.changename

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.constants.AuthConst.MAX_NICKNAME_LENGTH
import com.yjy.common.core.constants.AuthConst.MIN_NICKNAME_LENGTH
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.core.util.formatLargestTimeDuration
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherButton
import com.yjy.common.designsystem.component.ChallengeTogetherDialog
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.ConditionIndicator
import com.yjy.common.designsystem.component.ErrorIndicator
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.component.SingleLineTextField
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.StableImage
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.common.ui.ErrorBody
import com.yjy.feature.changename.model.ChangeNameUiAction
import com.yjy.feature.changename.model.ChangeNameUiEvent
import com.yjy.feature.changename.model.ChangeNameUiState
import com.yjy.feature.changename.model.RemainSecondForChangeUiState
import com.yjy.feature.changename.model.UserNameUiState
import com.yjy.feature.changename.model.getNameOrNull
import com.yjy.feature.changename.model.getValueOrNull
import com.yjy.feature.changename.model.isError
import com.yjy.feature.changename.model.isLoading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun ChangeNameRoute(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChangeNameViewModel = hiltViewModel(),
) {
    val remoteUserName by viewModel.remoteUserName.collectAsStateWithLifecycle()
    val remainSecondsForChange by viewModel.remainSecondsForChange.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ChangeNameScreen(
        modifier = modifier,
        remoteUserName = remoteUserName,
        remainSecondsForChange = remainSecondsForChange,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun ChangeNameScreen(
    modifier: Modifier = Modifier,
    remoteUserName: UserNameUiState = UserNameUiState.Success(""),
    remainSecondsForChange: RemainSecondForChangeUiState = RemainSecondForChangeUiState.Success(0),
    uiState: ChangeNameUiState = ChangeNameUiState(),
    uiEvent: Flow<ChangeNameUiEvent> = flowOf(),
    processAction: (ChangeNameUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    var shouldShowChangeConfirmDialog by remember { mutableStateOf(false) }

    val hasError = remoteUserName.isError() || remainSecondsForChange.isError()
    val isLoading = remoteUserName.isLoading() || remainSecondsForChange.isLoading()

    val changeSuccessMessage = stringResource(id = R.string.feature_changename_success)
    val duplicatedNicknameMessage = stringResource(id = R.string.feature_changename_nickname_already_taken)
    val checkNetworkMessage = stringResource(id = R.string.feature_changename_check_network_connection)
    val unknownErrorMessage = stringResource(id = R.string.feature_changename_error)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            ChangeNameUiEvent.ChangeSuccess -> {
                onShowSnackbar(SnackbarType.SUCCESS, changeSuccessMessage)
                onBackClick()
            }

            ChangeNameUiEvent.Failure.DuplicatedName ->
                onShowSnackbar(SnackbarType.ERROR, duplicatedNicknameMessage)

            ChangeNameUiEvent.Failure.NetworkError ->
                onShowSnackbar(SnackbarType.ERROR, checkNetworkMessage)

            ChangeNameUiEvent.Failure.UnknownError ->
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
        if (shouldShowChangeConfirmDialog) {
            ChallengeTogetherDialog(
                title = stringResource(id = R.string.feature_changename_title),
                description = stringResource(id = R.string.feature_changenam_confirm_info),
                positiveTextRes = R.string.feature_changename_action,
                onClickPositive = {
                    shouldShowChangeConfirmDialog = false
                    processAction(ChangeNameUiAction.OnChangeClick(uiState.name))
                },
                onClickNegative = { shouldShowChangeConfirmDialog = false },
            )
        }

        when {
            isLoading -> LoadingWheel()
            hasError -> ErrorBody(onClickRetry = { processAction(ChangeNameUiAction.OnRetryClick) })
            else -> {
                val placeHolderName = remoteUserName.getNameOrNull() ?: return@Scaffold
                val remainSeconds = remainSecondsForChange.getValueOrNull() ?: return@Scaffold

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 32.dp),
                ) {
                    TitleWithDescription(
                        titleRes = R.string.feature_changename_title,
                        descriptionRes = R.string.feature_changename_description,
                    )
                    Spacer(modifier = Modifier.height(35.dp))
                    StableImage(
                        drawableResId = R.drawable.image_memo,
                        descriptionResId = R.string.feature_changename_info_graphic,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(115.dp),
                    )
                    Spacer(modifier = Modifier.height(35.dp))
                    SingleLineTextField(
                        value = uiState.name,
                        onValueChange = { processAction(ChangeNameUiAction.OnNameUpdated(it)) },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        contentAlignment = Alignment.Center,
                        placeholderText = placeHolderName,
                        enabled = !uiState.isChangingName,
                    )
                    ConditionIndicator(
                        text = stringResource(
                            id = R.string.feature_changename_length_indicator,
                            MIN_NICKNAME_LENGTH,
                            MAX_NICKNAME_LENGTH,
                        ),
                        isMatched = uiState.isNameLengthValid,
                    )
                    if (uiState.name == placeHolderName) {
                        ErrorIndicator(
                            text = stringResource(id = R.string.feature_changename_duplicate_name),
                        )
                    }
                    if (uiState.isNameHasOnlyConsonantOrVowel) {
                        ErrorIndicator(
                            text = stringResource(id = R.string.feature_changename_korean_constraint),
                        )
                    }
                    if (remainSeconds > 0) {
                        ErrorIndicator(
                            text = stringResource(
                                id = R.string.feature_changename_next_change_notice,
                                formatLargestTimeDuration(remainSeconds),
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ChallengeTogetherButton(
                        onClick = { shouldShowChangeConfirmDialog = true },
                        enabled = uiState.isNameLengthValid &&
                            !uiState.isNameHasOnlyConsonantOrVowel &&
                            !uiState.isChangingName &&
                            uiState.name != placeHolderName &&
                            remainSeconds <= 0,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (uiState.isChangingName) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = CustomColorProvider.colorScheme.brand,
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.feature_changename_action),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}

@DevicePreviews
@Composable
fun ChangeNameScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            ChangeNameScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
