package com.yjy.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.yjy.common.core.extensions.clickableSingle
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.extensions.getDisplayNameResId
import com.yjy.common.designsystem.extensions.getIconResId
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.model.challenge.SimpleWaitingChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.TargetDays

@Composable
fun WaitingChallengeCard(
    challenge: SimpleWaitingChallenge,
    onClick: (SimpleWaitingChallenge) -> Unit,
    modifier: Modifier = Modifier,
    searchKeyword: String = "",
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(CustomColorProvider.colorScheme.surface)
            .clickableSingle { onClick(challenge) }
            .padding(16.dp),
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        ChallengeHeader(
            challengeId = challenge.id,
            isPrivate = challenge.isPrivate,
            searchKeyword = searchKeyword,
        )
        ChallengeBody(
            challenge = challenge,
            searchKeyword = searchKeyword,
        )
        ChallengeFooter(challenge)
    }
}

@Composable
private fun ChallengeHeader(
    challengeId: Int,
    isPrivate: Boolean,
    searchKeyword: String,
) {
    Row {
        val numberText = stringResource(id = R.string.common_ui_waiting_challenge_number, challengeId)
        val numberAnnotatedString = buildAnnotatedString {
            val startIndex = numberText.indexOf(searchKeyword, ignoreCase = true)
            if (searchKeyword.isNotEmpty() && startIndex != -1) {
                append(numberText.substring(0, startIndex))
                withStyle(
                    style = SpanStyle(
                        background = CustomColorProvider.colorScheme.brandBright,
                        color = CustomColorProvider.colorScheme.onBrandBright,
                    ),
                ) {
                    append(numberText.substring(startIndex, startIndex + searchKeyword.length))
                }
                append(numberText.substring(startIndex + searchKeyword.length))
            } else {
                append(numberText)
            }
        }

        Text(
            text = numberAnnotatedString,
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall,
        )
        if (isPrivate) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.common_ui_waiting_challenge_private),
                color = CustomColorProvider.colorScheme.onSurface,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun ChallengeBody(
    challenge: SimpleWaitingChallenge,
    searchKeyword: String,
) {
    val titleAnnotatedString = buildAnnotatedString {
        val startIndex = challenge.title.indexOf(searchKeyword, ignoreCase = true)
        if (searchKeyword.isNotEmpty() && startIndex != -1) {
            append(challenge.title.substring(0, startIndex))
            withStyle(
                style = SpanStyle(
                    background = CustomColorProvider.colorScheme.brandBright,
                    color = CustomColorProvider.colorScheme.onBrandBright,
                ),
            ) {
                append(challenge.title.substring(startIndex, startIndex + searchKeyword.length))
            }
            append(challenge.title.substring(startIndex + searchKeyword.length))
        } else {
            append(challenge.title)
        }
    }

    val descriptionAnnotatedString = buildAnnotatedString {
        val startIndex = challenge.description.indexOf(searchKeyword, ignoreCase = true)
        if (searchKeyword.isNotEmpty() && startIndex != -1) {
            append(challenge.description.substring(0, startIndex))
            withStyle(
                style = SpanStyle(
                    background = CustomColorProvider.colorScheme.brandBright,
                    color = CustomColorProvider.colorScheme.onBrandBright,
                ),
            ) {
                append(challenge.description.substring(startIndex, startIndex + searchKeyword.length))
            }
            append(challenge.description.substring(startIndex + searchKeyword.length))
        } else {
            append(challenge.description)
        }
    }

    Row {
        Column(modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = titleAnnotatedString,
                color = CustomColorProvider.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = descriptionAnnotatedString,
                color = CustomColorProvider.colorScheme.onSurfaceMuted,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        CategoryIcon(challenge.category)
    }
}

@Composable
private fun CategoryIcon(
    category: Category,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(vertical = 26.dp, horizontal = 10.dp)
            .size(50.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(CustomColorProvider.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            ImageVector.vectorResource(id = category.getIconResId()),
            contentDescription = stringResource(id = category.getDisplayNameResId()),
            tint = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
    }
}

@Composable
private fun ChallengeFooter(
    challenge: SimpleWaitingChallenge,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
    ) {
        ParticipantsSection(
            currentCount = challenge.currentParticipantCounts,
            maxCount = challenge.maxParticipantCounts,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(12.dp))
        TargetDaysText(targetDays = challenge.targetDays)
    }
}

@Composable
private fun ParticipantsSection(
    currentCount: Int,
    maxCount: Int,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val participantTextStyle = MaterialTheme.typography.bodySmall

    // 텍스트 크기는 사용자 환경에 따라 변하므로 따로 측정
    val countTextWidth = remember(currentCount, maxCount) {
        textMeasurer.measure(
            text = "$currentCount / $maxCount",
            style = participantTextStyle,
        ).size.width
    }

    // maxWidth 를 얻기 위해 사용
    BoxWithConstraints(modifier = modifier) {
        val participantIconSize = 40.dp
        val iconOverlap = 20.dp
        val effectiveIconWidth = participantIconSize - iconOverlap
        val ellipsisIconSize = 20.dp
        val spacingWidth = 8.dp

        val maxDisplayCount = with(density) {
            val availableWidth = maxWidth - countTextWidth.toDp() - ellipsisIconSize - spacingWidth
            (((availableWidth - participantIconSize) / effectiveIconWidth) + 1)
                .toInt()
                .coerceAtLeast(1)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ParticipantIcons(
                currentCount = currentCount,
                maxDisplayCount = maxDisplayCount,
                iconSize = participantIconSize,
                iconOverlap = iconOverlap,
            )
            Spacer(modifier = Modifier.width(spacingWidth))
            Text(
                text = "$currentCount / $maxCount",
                color = CustomColorProvider.colorScheme.onSurfaceMuted,
                style = participantTextStyle,
            )
        }
    }
}

@Composable
private fun ParticipantIcons(
    currentCount: Int,
    maxDisplayCount: Int,
    iconSize: Dp,
    iconOverlap: Dp,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy((-iconOverlap)),
        ) {
            val displayCount = minOf(currentCount, maxDisplayCount)
            repeat(displayCount) { index ->
                ParticipantIcon(
                    zIndex = displayCount - index.toFloat(),
                    modifier = Modifier.size(iconSize),
                )
            }
        }
        if (currentCount > maxDisplayCount) {
            Icon(
                ImageVector.vectorResource(id = ChallengeTogetherIcons.MoreHorizontal),
                contentDescription = null,
                tint = CustomColorProvider.colorScheme.onSurfaceMuted,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.Bottom),
            )
        }
    }
}

@Composable
private fun TargetDaysText(targetDays: TargetDays) {
    Text(
        text = (targetDays as? TargetDays.Fixed)?.let { fixed ->
            stringResource(
                id = R.string.common_ui_waiting_challenge_goal_days,
                fixed.days.toString(),
            )
        } ?: stringResource(
            id = R.string.common_ui_waiting_challenge_unlimited,
        ),
        color = CustomColorProvider.colorScheme.onBackground,
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.End,
        modifier = Modifier.padding(4.dp),
    )
}

@Composable
private fun ParticipantIcon(
    modifier: Modifier = Modifier,
    zIndex: Float,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(CustomColorProvider.colorScheme.background)
            .border(
                width = 2.dp,
                color = CustomColorProvider.colorScheme.surface,
                shape = CircleShape,
            )
            .zIndex(zIndex),
    ) {
        Icon(
            ImageVector.vectorResource(id = ChallengeTogetherIcons.Person),
            contentDescription = stringResource(id = R.string.common_ui_waiting_challenge_icon_of_person),
            tint = CustomColorProvider.colorScheme.onBackgroundMuted,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@ComponentPreviews
@Composable
fun WaitingChallengeCardPreview() {
    ChallengeTogetherTheme {
        WaitingChallengeCard(
            challenge = SimpleWaitingChallenge(
                id = 2000,
                title = "The Title Of Challenge",
                description = "challenge description",
                category = Category.ALL,
                targetDays = TargetDays.Infinite,
                currentParticipantCounts = 9,
                maxParticipantCounts = 10,
                isPrivate = true,
            ),
            modifier = Modifier.width(390.dp),
            onClick = {},
        )
    }
}
