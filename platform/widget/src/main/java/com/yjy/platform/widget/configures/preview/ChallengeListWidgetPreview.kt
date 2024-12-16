package com.yjy.platform.widget.configures.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.extensions.getIconResId
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.platform.widget.R
import com.yjy.platform.widget.components.CircularProgressBar
import com.yjy.platform.widget.util.calculateProgressPercentage
import com.yjy.platform.widget.util.formatPreviewSimpleTimeDuration

@Composable
fun ChallengeListWidgetPreview(
    challenges: List<SimpleStartedChallenge>,
    modifier: Modifier = Modifier,
    backgroundAlpha: Float = 1f,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.large)
            .background(CustomColorProvider.colorScheme.surface.copy(alpha = backgroundAlpha))
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (challenges.isEmpty()) {
            Text(
                text = stringResource(R.string.platform_widget_no_challenges),
                style = MaterialTheme.typography.bodyLarge,
                color = CustomColorProvider.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    items = challenges,
                    key = { it.id },
                ) { challenge ->
                    ChallengeItem(
                        challenge = challenge,
                        backgroundAlpha = backgroundAlpha,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChallengeItem(
    challenge: SimpleStartedChallenge,
    backgroundAlpha: Float = 1f,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressBar(
            percentage = challenge.calculateProgressPercentage(),
            icon = painterResource(id = challenge.category.getIconResId()),
            iconColor = CustomColorProvider.colorScheme.onBackgroundMuted,
            progressColor = CustomColorProvider.colorScheme.brand,
            backgroundColor = CustomColorProvider.colorScheme.background.copy(alpha = backgroundAlpha),
            size = 32,
            thickness = 2.5f,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = challenge.title,
            style = MaterialTheme.typography.bodySmall,
            color = CustomColorProvider.colorScheme.onSurface,
            modifier = Modifier
                .padding(bottom = 2.dp)
                .weight(1f),
        )
        Text(
            text = formatPreviewSimpleTimeDuration(seconds = challenge.currentRecordInSeconds),
            style = MaterialTheme.typography.bodySmall,
            color = CustomColorProvider.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(110.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(CustomColorProvider.colorScheme.background)
                .padding(vertical = 8.dp, horizontal = 12.dp),
        )
    }
}
