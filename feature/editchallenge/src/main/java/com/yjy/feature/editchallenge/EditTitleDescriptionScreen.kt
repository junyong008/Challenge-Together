package com.yjy.feature.editchallenge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_DESCRIPTION_LENGTH
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_TITLE_LENGTH
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherTextField
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.SingleLineTextField
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.editchallenge.component.ConfirmButton
import com.yjy.feature.editchallenge.model.EditChallengeUiAction
import com.yjy.feature.editchallenge.model.EditChallengeUiEvent
import com.yjy.feature.editchallenge.model.EditChallengeUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun EditTitleDescriptionRoute(
    challengeId: String,
    title: String,
    description: String,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditChallengeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EditTitleDescriptionScreen(
        modifier = modifier,
        challengeId = challengeId,
        title = title,
        description = description,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun EditTitleDescriptionScreen(
    modifier: Modifier = Modifier,
    challengeId: String = "",
    title: String = "",
    description: String = "",
    uiState: EditChallengeUiState = EditChallengeUiState(),
    uiEvent: Flow<EditChallengeUiEvent> = flowOf(),
    processAction: (EditChallengeUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    var currentTitle by rememberSaveable { mutableStateOf(title) }
    var currentDescription by rememberSaveable { mutableStateOf(description) }

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
                        processAction(
                            EditChallengeUiAction.OnEditTitle(
                                challengeId = challengeId,
                                title = currentTitle,
                                description = currentDescription.ifBlank { currentTitle },
                            )
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
                titleRes = R.string.feature_editchallenge_edit_title,
                descriptionRes = R.string.feature_editchallenge_edit_description,
            )
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = stringResource(id = R.string.feature_editchallenge_title),
                color = CustomColorProvider.colorScheme.onBackground,
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            SingleLineTextField(
                value = currentTitle,
                onValueChange = {
                    if (it.length <= MAX_CHALLENGE_TITLE_LENGTH) currentTitle = it
                },
                enabled = !uiState.isEditing,
                placeholderText = stringResource(id = R.string.feature_editchallenge_title_placeholder),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.feature_editchallenge_description_optional),
                color = CustomColorProvider.colorScheme.onBackground,
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            ChallengeTogetherTextField(
                value = currentDescription,
                onValueChange = {
                    if (it.length <= MAX_CHALLENGE_DESCRIPTION_LENGTH) currentDescription = it
                },
                placeholderText = currentTitle.ifBlank {
                    stringResource(id = R.string.feature_editchallenge_description_placeholder)
                },
                enabled = !uiState.isEditing,
                contentAlignment = Alignment.TopStart,
                modifier = Modifier.height(180.dp),
            )
        }
    }
}

@DevicePreviews
@Composable
fun EditTitleDescriptionScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            EditTitleDescriptionScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
