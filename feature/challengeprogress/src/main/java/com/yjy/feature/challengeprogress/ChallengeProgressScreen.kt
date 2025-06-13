package com.yjy.feature.challengeprogress

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.extensions.clickableSingle
import com.yjy.common.designsystem.component.BaseBottomSheet
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.component.RoundedLinearProgressBar
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.common.ui.ErrorBody
import com.yjy.feature.challengeprogress.model.ChallengeProgressUiState
import com.yjy.feature.challengeprogress.model.RecoveryStepState
import com.yjy.feature.challengeprogress.util.getRecoverySteps
import com.yjy.model.challenge.RecoveryProgress
import com.yjy.model.challenge.core.Category
import java.text.NumberFormat
import java.util.Locale

@Composable
internal fun ChallengeProgressRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChallengeProgressViewModel = hiltViewModel(),
) {
    val progressState by viewModel.progressState.collectAsStateWithLifecycle()

    ChallengeProgressScreen(
        modifier = modifier,
        progressState = progressState,
        retryOnError = viewModel::retryOnError,
        onBackClick = onBackClick,
    )
}

@Composable
internal fun ChallengeProgressScreen(
    modifier: Modifier = Modifier,
    progressState: ChallengeProgressUiState = ChallengeProgressUiState.Loading,
    retryOnError: () -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    var animatedScore by remember { mutableIntStateOf(0) }
    var showProgressInfoBottomSheet by remember { mutableStateOf(false) }

    if (showProgressInfoBottomSheet) {
        ProgressInfoBottomSheet(onDismiss = { showProgressInfoBottomSheet = false })
    }

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                onNavigationClick = onBackClick,
                titleRes = R.string.feature_challengeprogress_title,
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier.consumeWindowInsets(WindowInsets.navigationBars),
    ) { padding ->

        when (progressState) {
            ChallengeProgressUiState.Error -> ErrorBody(onClickRetry = retryOnError)
            ChallengeProgressUiState.Loading -> {
                LoadingWheel(
                    modifier = Modifier
                        .padding(padding)
                        .background(CustomColorProvider.colorScheme.background),
                )
            }

            is ChallengeProgressUiState.Success -> {
                val score = progressState.recoveryProgress.score
                val category = progressState.recoveryProgress.category
                val isCompletedChallenge = progressState.recoveryProgress.isCompletedChallenge
                val hasCompletedCheckIn = progressState.recoveryProgress.hasCompletedCheckIn
                val hasCompletedEmotionRecord = progressState.recoveryProgress.hasCompletedEmotionRecord
                val hasCompletedCommunityEngage = progressState.recoveryProgress.hasCompletedCommunityEngage
                val recoverySteps = getRecoverySteps(category)
                var remainRecoveryScore = animatedScore

                LaunchedEffect(score) {
                    val animationSpec = tween<Float>(
                        durationMillis = 1500,
                        easing = FastOutSlowInEasing,
                    )

                    animate(
                        initialValue = 0f,
                        targetValue = score.toFloat(),
                        animationSpec = animationSpec,
                    ) { value, _ ->
                        animatedScore = value.toInt()
                    }
                }

                Column(modifier = Modifier.padding(padding)) {
                    Column(
                        modifier = Modifier
                            .background(CustomColorProvider.colorScheme.surface)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))
                        ProgressScore(score = animatedScore)
                        Spacer(modifier = Modifier.height(24.dp))
                        ProgressInfoButton(
                            onClick = { showProgressInfoBottomSheet = true },
                            modifier = Modifier.padding(horizontal = 32.dp),
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    Column(
                        modifier = modifier
                            .weight(1f)
                            .verticalScroll(scrollState),
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        if (!isCompletedChallenge) {
                            Text(
                                text = stringResource(id = R.string.feature_challengeprogress_daily_mission),
                                style = MaterialTheme.typography.titleSmall,
                                color = CustomColorProvider.colorScheme.onBackground,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Missions(
                                hasCompletedCheckIn = hasCompletedCheckIn,
                                hasCompletedEmotionRecord = hasCompletedEmotionRecord,
                                hasCompletedCommunityEngage = hasCompletedCommunityEngage,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                        Text(
                            text = stringResource(id = R.string.feature_challengeprogress_recovery_step),
                            style = MaterialTheme.typography.titleSmall,
                            color = CustomColorProvider.colorScheme.onBackground,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        for (recoveryStep in recoverySteps) {
                            Step(
                                title = recoveryStep.title,
                                description = recoveryStep.description,
                                currentScore = remainRecoveryScore.coerceIn(0, recoveryStep.requireScore),
                                maxScore = recoveryStep.requireScore,
                                isCompletedChallenge = isCompletedChallenge,
                                recoveryState = RecoveryStepState.fromScore(
                                    currentScore = remainRecoveryScore,
                                    requireScore = recoveryStep.requireScore,
                                ),
                            )
                            remainRecoveryScore -= recoveryStep.requireScore
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Missions(
    hasCompletedCheckIn: Boolean,
    hasCompletedEmotionRecord: Boolean,
    hasCompletedCommunityEngage: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Mission(
            icon = ChallengeTogetherIcons.CheckOnly,
            title = stringResource(id = R.string.feature_challengeprogress_check_in),
            isCompleted = hasCompletedCheckIn,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Mission(
            icon = ChallengeTogetherIcons.Edit,
            title = stringResource(id = R.string.feature_challengeprogress_emotion_record),
            isCompleted = hasCompletedEmotionRecord,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Mission(
            icon = ChallengeTogetherIcons.Share,
            title = stringResource(id = R.string.feature_challengeprogress_community_engage),
            isCompleted = hasCompletedCommunityEngage,
        )
    }
}

@Composable
private fun Mission(
    @DrawableRes icon: Int,
    title: String,
    isCompleted: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = title,
            tint = if (isCompleted) {
                CustomColorProvider.colorScheme.brandDim
            } else {
                CustomColorProvider.colorScheme.onSurfaceMuted
            },
            modifier = Modifier
                .size(36.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(CustomColorProvider.colorScheme.surface)
                .padding(8.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = if (isCompleted) {
                CustomColorProvider.colorScheme.brandDim
            } else {
                CustomColorProvider.colorScheme.onBackgroundMuted
            },
            style = if (isCompleted) {
                MaterialTheme.typography.bodySmall
            } else {
                MaterialTheme.typography.labelSmall
            },
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(
                    if (isCompleted) {
                        CustomColorProvider.colorScheme.brandDim
                    } else {
                        CustomColorProvider.colorScheme.divider
                    },
                ),
        )
    }
}

@Composable
private fun Step(
    title: String,
    description: String,
    currentScore: Int,
    maxScore: Int,
    isCompletedChallenge: Boolean,
    recoveryState: RecoveryStepState,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        StepHeader(title = title, recoveryState = recoveryState)
        Spacer(modifier = Modifier.height(8.dp))
        StepBody(
            description = description,
            currentScore = currentScore,
            maxScore = maxScore,
            isCompletedChallenge = isCompletedChallenge,
            recoveryState = recoveryState,
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun StepBody(
    description: String,
    currentScore: Int,
    maxScore: Int,
    isCompletedChallenge: Boolean,
    recoveryState: RecoveryStepState,
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.height(IntrinsicSize.Min),
    ) {
        RecoveryStepStateLine(
            recoveryState = recoveryState,
            modifier = Modifier.fillMaxHeight(),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
                textAlign = TextAlign.Start,
            )
            Spacer(modifier = Modifier.height(32.dp))
            RecoveryStepProgressBar(
                currentScore = currentScore,
                maxScore = maxScore,
                isDisabled = isCompletedChallenge && recoveryState == RecoveryStepState.IN_PROGRESS,
            )
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
private fun StepHeader(
    title: String,
    recoveryState: RecoveryStepState,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RecoveryStepStateIndicator(recoveryState = recoveryState)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = CustomColorProvider.colorScheme.onBackground,
            textAlign = TextAlign.Start,
        )
    }
}

@Composable
private fun RecoveryStepProgressBar(
    currentScore: Int,
    maxScore: Int,
    isDisabled: Boolean,
) {
    val progress = currentScore.toFloat() / maxScore.coerceAtLeast(1)

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoundedLinearProgressBar(
            progress = { progress },
            animated = true,
            modifier = Modifier
                .height(12.dp)
                .weight(1f),
            enabled = !isDisabled,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$currentScore/$maxScore",
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun RecoveryStepStateLine(
    recoveryState: RecoveryStepState,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val strokeWidth = with(density) { 2.dp.toPx() }

    val lineColor = when (recoveryState) {
        RecoveryStepState.IN_PROGRESS -> CustomColorProvider.colorScheme.brandBright
        RecoveryStepState.COMPLETED -> CustomColorProvider.colorScheme.brandBright
        RecoveryStepState.NOT_STARTED -> CustomColorProvider.colorScheme.divider
    }

    val stroke = Stroke(
        width = strokeWidth,
        cap = StrokeCap.Round,
        pathEffect = when (recoveryState) {
            RecoveryStepState.IN_PROGRESS -> PathEffect.dashPathEffect(floatArrayOf(12f, 16f))
            else -> null
        },
    )

    Canvas(
        modifier = modifier
            .width(32.dp),
    ) {
        val centerX = size.width / 2
        drawLine(
            color = lineColor,
            start = Offset(centerX, 0f),
            end = Offset(centerX, size.height),
            strokeWidth = stroke.width,
            cap = stroke.cap,
            pathEffect = stroke.pathEffect,
        )
    }
}

@Composable
private fun RecoveryStepStateIndicator(
    recoveryState: RecoveryStepState,
) {
    val size = 30.dp
    val shape = MaterialTheme.shapes.medium

    when (recoveryState) {
        RecoveryStepState.IN_PROGRESS -> {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(shape)
                    .background(CustomColorProvider.colorScheme.brandBright),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(shape)
                        .background(CustomColorProvider.colorScheme.surface),
                )
            }
        }

        RecoveryStepState.COMPLETED -> {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(shape)
                    .background(CustomColorProvider.colorScheme.brandDim),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(shape)
                        .background(CustomColorProvider.colorScheme.surface),
                )
            }
        }

        RecoveryStepState.NOT_STARTED -> {
            Box(
                modifier = Modifier
                    .size(size)
                    .border(
                        width = 1.dp,
                        color = CustomColorProvider.colorScheme.divider,
                        shape = shape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    ImageVector.vectorResource(id = ChallengeTogetherIcons.Lock),
                    contentDescription = stringResource(
                        id = R.string.feature_challengeprogress_not_opened,
                    ),
                    tint = CustomColorProvider.colorScheme.divider,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun ProgressScore(
    score: Int,
    modifier: Modifier = Modifier,
) {
    val numberFormat = remember {
        NumberFormat.getNumberInstance(Locale.getDefault()).apply {
            isGroupingUsed = true
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.feature_challengeprogress_score),
            style = MaterialTheme.typography.labelSmall,
            color = CustomColorProvider.colorScheme.onSurfaceMuted,
            textAlign = TextAlign.Center,
        )
        Text(
            text = numberFormat.format(score),
            style = MaterialTheme.typography.displayMedium,
            color = CustomColorProvider.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ProgressInfoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(CustomColorProvider.colorScheme.background)
            .clickableSingle { onClick() }
            .padding(16.dp),
    ) {
        Icon(
            ImageVector.vectorResource(id = ChallengeTogetherIcons.Info),
            contentDescription = stringResource(
                id = R.string.feature_challengeprogress_what_is_progress_score,
            ),
            tint = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.feature_challengeprogress_what_is_progress_score),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            ImageVector.vectorResource(id = ChallengeTogetherIcons.ArrowRight),
            contentDescription = stringResource(
                id = R.string.feature_challengeprogress_what_is_progress_score,
            ),
            tint = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
    }
}

@Composable
private fun ProgressInfoBottomSheet(
    onDismiss: () -> Unit,
) {
    val fullText = stringResource(R.string.feature_challengeprogress_progress_info)
    val boldWords = listOf(
        stringResource(R.string.feature_challengeprogress_progress_bold_1),
        stringResource(R.string.feature_challengeprogress_progress_bold_2),
    )

    val annotatedText = buildAnnotatedString {
        var currentIndex = 0
        while (currentIndex < fullText.length) {
            val nextMatch = boldWords
                .mapNotNull { word ->
                    val index = fullText.indexOf(word, currentIndex)
                    if (index != -1) index to word else null
                }
                .minByOrNull { it.first }

            if (nextMatch == null) {
                append(fullText.substring(currentIndex))
                break
            }

            val (matchIndex, matchWord) = nextMatch
            append(fullText.substring(currentIndex, matchIndex))
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(matchWord)
            }
            currentIndex = matchIndex + matchWord.length
        }
    }

    BaseBottomSheet(
        onDismiss = onDismiss,
        modifier = Modifier.padding(horizontal = 8.dp),
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                ImageVector.vectorResource(id = ChallengeTogetherIcons.Info),
                contentDescription = stringResource(
                    id = R.string.feature_challengeprogress_what_is_progress_score,
                ),
                tint = CustomColorProvider.colorScheme.brandDim,
                modifier = Modifier
                    .size(30.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(CustomColorProvider.colorScheme.background)
                    .padding(6.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.feature_challengeprogress_what_is_progress_score),
                color = CustomColorProvider.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = annotatedText,
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Start,
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@DevicePreviews
@Composable
fun ChallengeProgressScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            ChallengeProgressScreen(
                progressState = ChallengeProgressUiState.Success(
                    recoveryProgress = RecoveryProgress(
                        challengeId = 1,
                        category = Category.QUIT_SMOKING,
                        score = 1536,
                        isCompletedChallenge = false,
                        hasCompletedCheckIn = true,
                        hasCompletedEmotionRecord = false,
                        hasCompletedCommunityEngage = false,
                    ),
                ),
            )
        }
    }
}
