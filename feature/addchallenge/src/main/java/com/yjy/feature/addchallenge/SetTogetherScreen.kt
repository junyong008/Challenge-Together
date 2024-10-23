package com.yjy.feature.addchallenge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_MAX_PARTICIPANTS
import com.yjy.common.core.constants.ChallengeConst.MIN_CHALLENGE_MAX_PARTICIPANTS
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.ThemePreviews
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherBottomAppBar
import com.yjy.common.designsystem.component.ChallengeTogetherButton
import com.yjy.common.designsystem.component.ChallengeTogetherDialog
import com.yjy.common.designsystem.component.ChallengeTogetherSwitch
import com.yjy.common.designsystem.component.NumberPicker
import com.yjy.common.designsystem.component.SingleLineTextField
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.feature.addchallenge.model.AddChallengeUiAction
import com.yjy.feature.addchallenge.model.AddChallengeUiEvent
import com.yjy.feature.addchallenge.model.AddChallengeUiState
import com.yjy.feature.addchallenge.navigation.AddChallengeStrings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun SetTogetherRoute(
    onBackClick: () -> Unit,
    onAddChallenge: (String) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddChallengeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SetTogetherScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onAddChallenge = onAddChallenge,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun SetTogetherScreen(
    modifier: Modifier = Modifier,
    uiState: AddChallengeUiState = AddChallengeUiState(),
    uiEvent: Flow<AddChallengeUiEvent> = flowOf(),
    processAction: (AddChallengeUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onAddChallenge: (String) -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val waitingRoomCreatedMessage = stringResource(id = AddChallengeStrings.feature_addchallenge_waiting_room_created)
    val unknownErrorMessage = stringResource(id = AddChallengeStrings.feature_addchallenge_unknown_error)
    val checkNetworkMessage = stringResource(id = AddChallengeStrings.feature_addchallenge_check_network_connection)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            is AddChallengeUiEvent.ChallengeAdded -> {
                onShowSnackbar(SnackbarType.SUCCESS, waitingRoomCreatedMessage)
                onAddChallenge(it.challengeId)
            }

            AddChallengeUiEvent.AddFailure.NetworkError ->
                onShowSnackbar(SnackbarType.ERROR, checkNetworkMessage)

            AddChallengeUiEvent.AddFailure.UnknownError ->
                onShowSnackbar(SnackbarType.ERROR, unknownErrorMessage)

            else -> Unit
        }
    }

    Scaffold(
        bottomBar = {
            ChallengeTogetherBottomAppBar(
                onBackClick = onBackClick,
                showContinueButton = false,
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier,
    ) { padding ->

        if (uiState.shouldShowAddConfirmDialog) {
            ChallengeTogetherDialog(
                title = stringResource(id = AddChallengeStrings.feature_addchallenge_dialog_create_room_title),
                description = stringResource(id = AddChallengeStrings.feature_addchallenge_dialog_create_room_description),
                positiveTextRes = AddChallengeStrings.feature_addchallenge_dialog_create,
                onClickPositive = {
                    processAction(
                        AddChallengeUiAction.OnConfirmCreateWaitingRoom(
                            category = uiState.category,
                            title = uiState.title,
                            description = uiState.description,
                            targetDays = uiState.targetDays,
                            maxParticipants = uiState.maxParticipants,
                            enableRoomPassword = uiState.enableRoomPassword,
                            roomPassword = uiState.roomPassword,
                        )
                    )
                },
                onClickNegative = { processAction(AddChallengeUiAction.OnCancelCreateWaitingRoom) },
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            TitleWithDescription(
                titleRes = AddChallengeStrings.feature_addchallenge_title_set_together,
                descriptionRes = AddChallengeStrings.feature_addchallenge_description_set_together,
            )
            Spacer(modifier = Modifier.height(50.dp))
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                MaxParticipantsCard(
                    maxParticipants = uiState.maxParticipants,
                    onMaxParticipantsUpdated = {
                        processAction(AddChallengeUiAction.OnMaxParticipantsUpdated(it))
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                RoomPasswordCard(
                    enableRoomPassword = uiState.enableRoomPassword,
                    onEnableRoomPasswordUpdated = {
                        processAction(AddChallengeUiAction.OnEnableRoomPasswordUpdated(it))
                    },
                    roomPassword = uiState.roomPassword,
                    onRoomPasswordUpdated = {
                        processAction(AddChallengeUiAction.OnRoomPasswordUpdated(it))
                    },
                )
                Spacer(modifier = Modifier.height(32.dp))
                ChallengeTogetherButton(
                    onClick = { processAction(AddChallengeUiAction.OnCreateWaitingRoomClick) },
                    enabled = !uiState.isAddingChallenge,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 55.dp),
                ) {
                    if (uiState.isAddingChallenge) {
                        CircularProgressIndicator(
                            color = CustomColorProvider.colorScheme.brand,
                            modifier = Modifier.size(24.dp),
                        )
                    } else {
                        Text(
                            text = stringResource(id = AddChallengeStrings.feature_addchallenge_create_waiting_room),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoomPasswordCard(
    enableRoomPassword: Boolean,
    onEnableRoomPasswordUpdated: (Boolean) -> Unit,
    roomPassword: String,
    onRoomPasswordUpdated: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(MaterialTheme.shapes.large)
            .background(CustomColorProvider.colorScheme.surface)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(id = AddChallengeStrings.feature_addchallenge_room_password),
                color = CustomColorProvider.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(8.dp))
            ChallengeTogetherSwitch(
                checked = enableRoomPassword,
                onCheckedChange = onEnableRoomPasswordUpdated,
            )
        }
        if (enableRoomPassword) {
            Spacer(modifier = Modifier.height(8.dp))
            SingleLineTextField(
                value = roomPassword,
                onValueChange = onRoomPasswordUpdated,
                backgroundColor = CustomColorProvider.colorScheme.background,
                trailingIconColor = CustomColorProvider.colorScheme.onBackgroundMuted,
                passwordIconColor = CustomColorProvider.colorScheme.onBackgroundMuted,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = ChallengeTogetherIcons.Lock),
                        contentDescription = stringResource(id = AddChallengeStrings.feature_addchallenge_room_password_icon),
                        tint = CustomColorProvider.colorScheme.onBackground,
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                placeholderText = stringResource(id = AddChallengeStrings.feature_addchallenge_room_password_placeholder),
                isPassword = true,
            )
        }
    }
}

@Composable
private fun MaxParticipantsCard(
    maxParticipants: Int,
    onMaxParticipantsUpdated: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .height(220.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(CustomColorProvider.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(id = AddChallengeStrings.feature_addchallenge_max_participants),
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .align(Alignment.Top)
                .weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            NumberPicker(
                selectedNumber = maxParticipants,
                onNumberChange = onMaxParticipantsUpdated,
                range = IntRange(MIN_CHALLENGE_MAX_PARTICIPANTS, MAX_CHALLENGE_MAX_PARTICIPANTS),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = AddChallengeStrings.feature_addchallenge_people),
                color = CustomColorProvider.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@ThemePreviews
@Composable
fun SetTogetherScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            SetTogetherScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
