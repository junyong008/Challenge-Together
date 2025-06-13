package com.yjy.feature.challengeranking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.core.util.formatTimeDuration
import com.yjy.common.designsystem.component.BulletText
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherDialog
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.CircleMedal
import com.yjy.common.designsystem.component.ClickableText
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.StableImage
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.common.ui.ErrorBody
import com.yjy.common.ui.preview.ChallengeRankPreviewParameterProvider
import com.yjy.feature.challengeranking.model.ChallengeRankingUiAction
import com.yjy.feature.challengeranking.model.ChallengeRankingUiEvent
import com.yjy.feature.challengeranking.model.ChallengeRankingUiState
import com.yjy.feature.challengeranking.model.challengeRanksOrNull
import com.yjy.model.challenge.ChallengeRank
import com.yjy.model.common.Tier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun ChallengeRankingRoute(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChallengeRankingViewModel = hiltViewModel(),
) {
    val challengeRanks by viewModel.challengeRanks.collectAsStateWithLifecycle()

    ChallengeRankingScreen(
        modifier = modifier,
        challengeRanksUiState = challengeRanks,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun ChallengeRankingScreen(
    modifier: Modifier = Modifier,
    challengeRanksUiState: ChallengeRankingUiState = ChallengeRankingUiState.Loading,
    uiEvent: Flow<ChallengeRankingUiEvent> = flowOf(),
    processAction: (ChallengeRankingUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val forceRemoveSuccessMessage = stringResource(id = R.string.feature_challengeranking_force_remove_success)
    val forceRemoveFailedMessage = stringResource(id = R.string.feature_challengeranking_force_remove_failed)
    val forceRemoveUserReconnectedMessage = stringResource(id = R.string.feature_challengeranking_user_reconnected)
    val networkErrorMessage = stringResource(id = R.string.feature_challengeranking_check_network_connection)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            ChallengeRankingUiEvent.ForceRemoveSuccess ->
                onShowSnackbar(SnackbarType.SUCCESS, forceRemoveSuccessMessage)

            ChallengeRankingUiEvent.ForceRemoveFailed.UserReConnected ->
                onShowSnackbar(SnackbarType.ERROR, forceRemoveUserReconnectedMessage)

            ChallengeRankingUiEvent.ForceRemoveFailed.NetworkError ->
                onShowSnackbar(SnackbarType.ERROR, networkErrorMessage)

            ChallengeRankingUiEvent.ForceRemoveFailed.UnknownError ->
                onShowSnackbar(SnackbarType.ERROR, forceRemoveFailedMessage)
        }
    }

    var isScoreMode by rememberSaveable { mutableStateOf(false) }
    var selectedMemberIdToRemove by rememberSaveable { mutableStateOf<Int?>(null) }

    if (selectedMemberIdToRemove != null) {
        ChallengeTogetherDialog(
            title = stringResource(id = R.string.feature_challengeranking_force_remove_title),
            description = stringResource(id = R.string.feature_challengeranking_force_remove_description),
            positiveTextRes = R.string.feature_challengeranking_remove,
            positiveTextColor = CustomColorProvider.colorScheme.red,
            onClickPositive = {
                processAction(ChallengeRankingUiAction.OnForceRemoveClick(selectedMemberIdToRemove!!))
                selectedMemberIdToRemove = null
            },
            onClickNegative = { selectedMemberIdToRemove = null },
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.background(CustomColorProvider.colorScheme.surface),
            ) {
                ChallengeTogetherTopAppBar(
                    onNavigationClick = onBackClick,
                    titleRes = R.string.feature_challengeranking_title,
                    backgroundColor = CustomColorProvider.colorScheme.surface,
                    rightContent = {
                        challengeRanksUiState.challengeRanksOrNull()?.let {
                            ModeChangeButton(
                                onClick = { isScoreMode = !isScoreMode },
                                modifier = Modifier.padding(end = 4.dp),
                                isScoreMode = isScoreMode,
                            )
                        }
                    },
                )
                challengeRanksUiState.challengeRanksOrNull()?.findMyRank()?.let { myRank ->
                    RankingHeader(
                        isScoreMode = isScoreMode,
                        myChallengeRank = myRank,
                    )
                }
            }
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier.consumeWindowInsets(WindowInsets.navigationBars),
    ) { padding ->
        when (challengeRanksUiState) {
            ChallengeRankingUiState.Error -> {
                ErrorBody(onClickRetry = { processAction(ChallengeRankingUiAction.RetryOnError) })
            }

            ChallengeRankingUiState.Loading -> {
                LoadingWheel(
                    modifier = Modifier
                        .padding(padding)
                        .background(CustomColorProvider.colorScheme.background),
                )
            }

            is ChallengeRankingUiState.Success -> {
                RankingBody(
                    isScoreMode = isScoreMode,
                    challengeRanks = challengeRanksUiState.challengeRanks,
                    onForceRemoveClick = { selectedMemberIdToRemove = it },
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

private fun List<ChallengeRank>.findMyRank(): ChallengeRank? = firstOrNull { it.isMine }

@Composable
private fun ModeChangeButton(
    onClick: () -> Unit,
    isScoreMode: Boolean,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            ImageVector.vectorResource(id = ChallengeTogetherIcons.Rotation),
            contentDescription = null,
            tint = if (isScoreMode) {
                CustomColorProvider.colorScheme.onBackground
            } else {
                CustomColorProvider.colorScheme.onBackgroundMuted
            },
        )
    }
}

@Composable
private fun RankingBody(
    isScoreMode: Boolean,
    challengeRanks: List<ChallengeRank>,
    onForceRemoveClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(isScoreMode) {
        listState.scrollToItem(0)
    }

    LazyColumn(
        state = listState,
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        items(
            items = if (isScoreMode) {
                challengeRanks.sortedByDescending { it.recoveryScore }
            } else {
                challengeRanks.sortedByDescending { it.currentRecordInSeconds }
            },
            key = { it.memberId },
        ) { challengeRank ->
            RankItem(
                isScoreMode = isScoreMode,
                challengeRank = challengeRank,
                onForceRemoveClick = { onForceRemoveClick(challengeRank.memberId) },
            )
            HorizontalDivider(
                color = CustomColorProvider.colorScheme.divider,
                thickness = 1.dp,
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            TipDescriptions()
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun TipDescriptions() {
    Column {
        Text(
            text = stringResource(id = R.string.feature_challengeranking_tip_title),
            style = MaterialTheme.typography.bodyMedium,
            color = CustomColorProvider.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(8.dp))
        BulletText(
            text = stringResource(id = R.string.feature_challengeranking_tip_long_absence),
            modifier = Modifier.padding(start = 4.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        BulletText(
            text = stringResource(id = R.string.feature_challengeranking_tip_alone),
            modifier = Modifier.padding(start = 4.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        BulletText(
            text = stringResource(id = R.string.feature_challengeranking_tip_progress_score),
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun RankItem(
    isScoreMode: Boolean,
    challengeRank: ChallengeRank,
    onForceRemoveClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 16.dp),
    ) {
        RankingProfile(tier = challengeRank.user.tier)
        Spacer(modifier = Modifier.width(6.dp))
        Column(modifier = Modifier.weight(1f)) {
            RankNickName(
                challengeRank = challengeRank,
                onForceRemoveClick = onForceRemoveClick,
            )
            Text(
                text = when {
                    isScoreMode -> stringResource(
                        id = R.string.feature_challengeranking_score_format,
                        challengeRank.recoveryScore,
                    )

                    challengeRank.isComplete -> stringResource(id = R.string.feature_challengeranking_success)
                    else -> formatTimeDuration(challengeRank.currentRecordInSeconds)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = when {
                    isScoreMode -> CustomColorProvider.colorScheme.brandDim
                    challengeRank.isComplete -> CustomColorProvider.colorScheme.onBackground
                    else -> CustomColorProvider.colorScheme.red
                },
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        RankNumber(
            rank = if (isScoreMode) {
                challengeRank.scoreRank
            } else {
                challengeRank.rank
            },
        )
    }
}

@Composable
private fun RankNickName(
    challengeRank: ChallengeRank,
    onForceRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val paddingValues = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    if (challengeRank.isInActive) {
        ClickableText(
            text = challengeRank.user.name,
            style = MaterialTheme.typography.labelSmall,
            color = CustomColorProvider.colorScheme.red,
            onClick = onForceRemoveClick,
            contentPadding = paddingValues,
        )
    } else {
        Text(
            text = challengeRank.user.name,
            style = if (challengeRank.isMine) {
                MaterialTheme.typography.bodySmall
            } else {
                MaterialTheme.typography.labelSmall
            },
            color = CustomColorProvider.colorScheme.onBackground,
            modifier = modifier.padding(paddingValues),
        )
    }
}

@Composable
private fun RankingHeader(
    isScoreMode: Boolean,
    myChallengeRank: ChallengeRank,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
    ) {
        RankingProfile(
            tier = myChallengeRank.user.tier,
            shape = CircleShape,
            modifier = Modifier.size(70.dp),
            padding = 14.dp,
            backgroundColor = CustomColorProvider.colorScheme.background,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = myChallengeRank.user.name,
                style = MaterialTheme.typography.bodyMedium,
                color = CustomColorProvider.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when {
                    isScoreMode -> stringResource(
                        id = R.string.feature_challengeranking_score_format,
                        myChallengeRank.recoveryScore,
                    )

                    myChallengeRank.isComplete -> stringResource(id = R.string.feature_challengeranking_success)
                    else -> formatTimeDuration(myChallengeRank.currentRecordInSeconds)
                },
                style = MaterialTheme.typography.titleSmall,
                color = when {
                    isScoreMode -> CustomColorProvider.colorScheme.brandDim
                    myChallengeRank.isComplete -> CustomColorProvider.colorScheme.onBackground
                    else -> CustomColorProvider.colorScheme.red
                },
            )
        }
        RankNumber(
            rank = if (isScoreMode) {
                myChallengeRank.scoreRank
            } else {
                myChallengeRank.rank
            },
            size = 40.dp,
            textStyle = MaterialTheme.typography.titleLarge,
            textColor = CustomColorProvider.colorScheme.onSurface,
        )
    }
}

@Composable
private fun RankNumber(
    rank: Int,
    size: Dp = 32.dp,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    textColor: Color = CustomColorProvider.colorScheme.onBackground,
) {
    when (rank) {
        in RankNumberConstant.MEDAL_RANKS -> {
            RankNumberConstant.MEDALS[rank]?.let { (drawable, description) ->
                StableImage(
                    drawableResId = drawable,
                    descriptionResId = description,
                    modifier = modifier.size(size),
                )
            }
        }
        else -> {
            Text(
                text = rank.toString(),
                style = textStyle,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(min = size),
            )
        }
    }
}

@Composable
private fun RankingProfile(
    tier: Tier,
    shape: Shape = MaterialTheme.shapes.large,
    modifier: Modifier = Modifier,
    padding: Dp = 8.dp,
    backgroundColor: Color = CustomColorProvider.colorScheme.surface,
) {
    Box(modifier = modifier.width(46.dp)) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(backgroundColor)
                .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            CircleMedal(tier = tier)
        }
    }
}

private object RankNumberConstant {
    const val FIRST_PLACE = 1
    const val SECOND_PLACE = 2
    const val THIRD_PLACE = 3
    val MEDAL_RANKS = FIRST_PLACE..THIRD_PLACE

    val MEDALS = mapOf(
        FIRST_PLACE to Pair(
            R.drawable.image_medal_1,
            R.string.feature_challengeranking_first_place_description,
        ),
        SECOND_PLACE to Pair(
            R.drawable.image_medal_2,
            R.string.feature_challengeranking_second_place_description,
        ),
        THIRD_PLACE to Pair(
            R.drawable.image_medal_3,
            R.string.feature_challengeranking_third_place_description,
        ),
    )
}

@DevicePreviews
@Composable
fun ChallengeRankingScreenPreview(
    @PreviewParameter(ChallengeRankPreviewParameterProvider::class)
    challengeRanks: List<ChallengeRank>,
) {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            ChallengeRankingScreen(
                challengeRanksUiState = ChallengeRankingUiState.Success(challengeRanks),
            )
        }
    }
}
