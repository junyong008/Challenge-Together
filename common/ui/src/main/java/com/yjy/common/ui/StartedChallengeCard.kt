package com.yjy.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yjy.common.core.extensions.clickableSingle
import com.yjy.common.core.util.formatTimeDuration
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.component.RoundedLinearProgressBar
import com.yjy.common.designsystem.extensions.getDisplayNameResId
import com.yjy.common.designsystem.extensions.getIconResId
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.model.challenge.ChallengeFactory
import com.yjy.model.challenge.StartedChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.TargetDays
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun StartedChallengeCard(
    challenge: StartedChallenge,
    onClick: (StartedChallenge) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (challenge.currentRecordInSeconds == null) return
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(CustomColorProvider.colorScheme.surface)
            .clickableSingle { onClick(challenge) }
            .padding(20.dp),
    ) {
        ChallengeBody(challenge)
        Spacer(modifier = Modifier.height(16.dp))
        if (challenge.isCompleted) {
            CompletedChallengeTimer(completedDateTime = getCompletedDateTime(challenge))
        } else {
            ChallengeTimer(seconds = challenge.currentRecordInSeconds!!)
        }
    }
}

private fun getCompletedDateTime(challenge: StartedChallenge): LocalDateTime {
    return when (val targetDays = challenge.targetDays) {
        is TargetDays.Fixed -> challenge.recentResetDateTime.plusDays(targetDays.days.toLong())
        TargetDays.Infinite -> error("Infinite targetDays is not supported in completed")
    }
}

@Composable
private fun ChallengeBody(
    challenge: StartedChallenge,
) {
    Row {
        Column(modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = challenge.title,
                color = CustomColorProvider.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = challenge.description,
                color = CustomColorProvider.colorScheme.onSurfaceMuted,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))
            challenge.targetDays.let { targetDays ->
                if (targetDays is TargetDays.Fixed) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ChallengeProgress(
                        currentSeconds = challenge.currentRecordInSeconds!!,
                        targetDays = targetDays.days,
                        isCompleted = challenge.isCompleted
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        CategoryIcon(challenge.category)
    }
}

@Composable
private fun ChallengeProgress(
    currentSeconds: Long,
    targetDays: Int,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val progress = if (isCompleted) {
        1f
    } else {
        val targetSeconds = targetDays * 24 * 60 * 60L
        (currentSeconds.toFloat() / targetSeconds).coerceIn(0f, 1f)
    }
    val progressPercent = (progress * 100).toInt()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        RoundedLinearProgressBar(
            progress = { progress },
            animated = false,
            modifier = Modifier
                .height(12.dp)
                .weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$progressPercent%",
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun CategoryIcon(
    category: Category,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(10.dp)
            .size(50.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(CustomColorProvider.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = category.getIconResId()),
            contentDescription = stringResource(id = category.getDisplayNameResId()),
            tint = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
    }
}

@Composable
private fun ChallengeTimer(
    seconds: Long,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(CustomColorProvider.colorScheme.background)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = ChallengeTogetherIcons.Timer),
            contentDescription = stringResource(
                id = R.string.common_ui_started_challenge_card_timer_description
            ),
            tint = CustomColorProvider.colorScheme.onBackground,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = formatTimeDuration(seconds),
            color = CustomColorProvider.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
private fun CompletedChallengeTimer(
    completedDateTime: LocalDateTime,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(CustomColorProvider.colorScheme.background)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = ChallengeTogetherIcons.Check),
            contentDescription = stringResource(
                id = R.string.common_ui_started_challenge_card_check_description
            ),
            tint = CustomColorProvider.colorScheme.onSurface,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = completedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@ComponentPreviews
@Composable
fun StartedChallengeCardPreview() {
    ChallengeTogetherTheme {
        StartedChallengeCard(
            challenge = ChallengeFactory.createStartedChallenge(
                id = "id",
                title = "The Title Of Challenge",
                description = "challenge description",
                category = Category.ALL,
                targetDays = TargetDays.Fixed(100),
                currentRecordInSeconds = (24 * 60 * 60) * 30,
                mode = Mode.CHALLENGE,
                recentResetDateTime = LocalDateTime.now(),
                isCompleted = false,
            ),
            onClick = {},
        )
    }
}
