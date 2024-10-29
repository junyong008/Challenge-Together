package com.yjy.feature.addchallenge

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.core.util.formatLocalDateTime
import com.yjy.common.designsystem.ThemePreviews
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherBottomAppBar
import com.yjy.common.designsystem.component.ChallengeTogetherButton
import com.yjy.common.designsystem.component.ChallengeTogetherDialog
import com.yjy.common.designsystem.component.ChallengeTogetherOutlinedButton
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.StableImage
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.extensions.getDisplayNameResId
import com.yjy.common.designsystem.extensions.getIconResId
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.feature.addchallenge.model.AddChallengeUiAction
import com.yjy.feature.addchallenge.model.AddChallengeUiEvent
import com.yjy.feature.addchallenge.model.AddChallengeUiState
import com.yjy.feature.addchallenge.navigation.AddChallengeStrings
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.TargetDays
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime

@Composable
internal fun ConfirmRoute(
    onBackClick: () -> Unit,
    onSetTogetherClick: () -> Unit,
    onAddChallenge: (String) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddChallengeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ConfirmScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onSetTogetherClick = onSetTogetherClick,
        onAddChallenge = onAddChallenge,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun ConfirmScreen(
    modifier: Modifier = Modifier,
    uiState: AddChallengeUiState = AddChallengeUiState(),
    uiEvent: Flow<AddChallengeUiEvent> = flowOf(),
    processAction: (AddChallengeUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onSetTogetherClick: () -> Unit = {},
    onAddChallenge: (String) -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val challengeAddedMessage = stringResource(id = AddChallengeStrings.feature_addchallenge_challenge_added)
    val unknownErrorMessage = stringResource(id = AddChallengeStrings.feature_addchallenge_unknown_error)
    val checkNetworkMessage = stringResource(id = AddChallengeStrings.feature_addchallenge_check_network_connection)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            is AddChallengeUiEvent.ChallengeAdded -> {
                onShowSnackbar(SnackbarType.SUCCESS, challengeAddedMessage)
                onAddChallenge(it.challengeId)
            }

            AddChallengeUiEvent.AddFailure.NetworkError ->
                onShowSnackbar(SnackbarType.ERROR, checkNetworkMessage)

            AddChallengeUiEvent.AddFailure.UnknownError ->
                onShowSnackbar(SnackbarType.ERROR, unknownErrorMessage)

            else -> Unit
        }
    }

    LaunchedEffect(Unit) {
        if (uiState.mode == null) onBackClick()
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
                title = stringResource(id = AddChallengeStrings.feature_addchallenge_dialog_start_title),
                description = stringResource(id = AddChallengeStrings.feature_addchallenge_dialog_start_description),
                positiveTextRes = AddChallengeStrings.feature_addchallenge_dialog_start,
                onClickPositive = {
                    processAction(
                        AddChallengeUiAction.OnConfirmStartChallenge(
                            mode = uiState.mode!!,
                            category = uiState.category,
                            title = uiState.title,
                            description = uiState.description,
                            startDateTime = uiState.startDateTime,
                            targetDays = uiState.targetDays,
                        )
                    )
                },
                onClickNegative = { processAction(AddChallengeUiAction.OnCancelStartChallenge) },
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
                titleRes = AddChallengeStrings.feature_addchallenge_title_confirm,
                descriptionRes = AddChallengeStrings.feature_addchallenge_description_confirm,
            )
            Spacer(modifier = Modifier.height(50.dp))
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TitleSection(
                    mode = uiState.mode!!,
                    title = uiState.title,
                    description = uiState.description,
                )

                Spacer(modifier = Modifier.height(8.dp))
                CategorySection(
                    category = uiState.category,
                    targetDays = if (uiState.mode == Mode.FREE) {
                        TargetDays.Infinite
                    } else {
                        uiState.targetDays
                    },
                )

                if (uiState.mode == Mode.FREE) {
                    Spacer(modifier = Modifier.height(8.dp))
                    DataSection(startDateTime = uiState.startDateTime)
                }

                Spacer(modifier = Modifier.height(32.dp))
                StartButton(
                    onClick = { processAction(AddChallengeUiAction.OnStartChallengeClick) },
                    enabled = !uiState.isAddingChallenge,
                )

                if (uiState.mode == Mode.CHALLENGE) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TogetherButton(
                        onClick = onSetTogetherClick,
                        enabled = !uiState.isAddingChallenge,
                    )
                }
            }
        }
    }
}

@Composable
private fun TogetherButton(
    onClick: () -> Unit,
    enabled: Boolean,
) {
    ChallengeTogetherOutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 55.dp),
    ) {
        Text(
            text = stringResource(id = AddChallengeStrings.feature_addchallenge_together),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun StartButton(
    onClick: () -> Unit,
    enabled: Boolean,
) {
    ChallengeTogetherButton(
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 55.dp),
    ) {
        if (!enabled) {
            CircularProgressIndicator(
                color = CustomColorProvider.colorScheme.brand,
                modifier = Modifier.size(24.dp),
            )
        } else {
            Text(
                text = stringResource(id = AddChallengeStrings.feature_addchallenge_start_immediately),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun DataSection(startDateTime: LocalDateTime) {
    BaseCard(
        titleResId = AddChallengeStrings.feature_addchallenge_start_date,
        content = {
            Text(
                text = formatLocalDateTime(startDateTime),
                style = MaterialTheme.typography.titleMedium,
                color = CustomColorProvider.colorScheme.onSurface,
                textAlign = TextAlign.End,
            )
        },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun CategorySection(category: Category, targetDays: TargetDays) {
    val targetDayText = when (targetDays) {
        is TargetDays.Fixed -> stringResource(id = R.string.feature_addchallenge_target_day_string, targetDays.days)
        TargetDays.Infinite -> stringResource(id = AddChallengeStrings.feature_addchallenge_unlimited)
    }

    Row(
        modifier = Modifier
            .heightIn(min = 120.dp)
            .height(IntrinsicSize.Min),
    ) {
        BaseCard(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight(),
            titleResId = AddChallengeStrings.feature_addchallenge_category_card_title,
            content = {
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(CustomColorProvider.colorScheme.background),
                ) {
                    Icon(
                        painter = painterResource(id = category.getIconResId()),
                        contentDescription = stringResource(id = category.getDisplayNameResId()),
                        tint = CustomColorProvider.colorScheme.onBackgroundMuted,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(8.dp),
                    )
                }
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        BaseCard(
            modifier = Modifier.weight(0.6f),
            titleResId = AddChallengeStrings.feature_addchallenge_target_day_card_title,
            content = {
                Text(
                    text = targetDayText,
                    style = MaterialTheme.typography.titleMedium,
                    color = CustomColorProvider.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                )
            }
        )
    }
}

@Composable
private fun BaseCard(
    @StringRes titleResId: Int,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(CustomColorProvider.colorScheme.surface)
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(id = titleResId),
            color = CustomColorProvider.colorScheme.onSurfaceMuted,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.align(Alignment.Start),
        )
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier.align(Alignment.End),
            contentAlignment = Alignment.BottomEnd,
        ) {
            content()
        }
    }
}

@Composable
private fun TitleSection(
    mode: Mode,
    title: String,
    description: String,
) {
    when (mode) {
        Mode.CHALLENGE -> {
            TitleCard(
                drawableResId = R.drawable.image_trophy,
                drawableDescriptionResId = AddChallengeStrings.feature_addchallenge_mode_challenge,
                title = title,
                description = description,
            )
        }
        Mode.FREE -> {
            TitleCard(
                imagePadding = 5.dp,
                drawableResId = R.drawable.image_calendar,
                drawableDescriptionResId = AddChallengeStrings.feature_addchallenge_mode_free,
                title = title,
                description = description,
            )
        }
    }
}

@Composable
private fun TitleCard(
    @DrawableRes drawableResId: Int,
    @StringRes drawableDescriptionResId: Int,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    imagePadding: Dp = 0.dp,
) {
    val shape = MaterialTheme.shapes.large
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape)
            .background(CustomColorProvider.colorScheme.surface)
            .padding(24.dp),
    ) {
        StableImage(
            drawableResId = drawableResId,
            descriptionResId = drawableDescriptionResId,
            modifier = Modifier
                .size(55.dp)
                .padding(imagePadding),
        )
        Column(
            modifier = modifier
                .weight(1f)
                .wrapContentHeight()
                .padding(start = 24.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = CustomColorProvider.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description.ifBlank { title },
                style = MaterialTheme.typography.labelSmall,
                color = CustomColorProvider.colorScheme.onSurfaceMuted,
            )
        }
    }
}

@ThemePreviews
@Composable
fun ConfirmScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            ConfirmScreen(
                modifier = Modifier.fillMaxSize(),
                uiState = AddChallengeUiState(
                    mode = Mode.CHALLENGE,
                    title = "Title",
                    description = "Description",
                ),
            )
        }
    }
}
