package com.yjy.platform.widget.configures.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
fun ChallengeTallWidgetPreview(
    challenge: SimpleStartedChallenge?,
    modifier: Modifier = Modifier,
    backgroundAlpha: Float = 1f,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.large)
            .background(CustomColorProvider.colorScheme.surface.copy(alpha = backgroundAlpha)),
        contentAlignment = Alignment.Center,
    ) {
        if (challenge == null) {
            Text(
                text = stringResource(R.string.platform_widget_no_challenge),
                style = MaterialTheme.typography.bodyLarge,
                color = CustomColorProvider.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                CircularProgressBar(
                    percentage = challenge.calculateProgressPercentage(),
                    icon = painterResource(challenge.category.getIconResId()),
                    iconColor = CustomColorProvider.colorScheme.onBackgroundMuted,
                    progressColor = CustomColorProvider.colorScheme.brand,
                    backgroundColor = CustomColorProvider.colorScheme.background.copy(alpha = backgroundAlpha),
                    size = 50,
                    thickness = 3f,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = CustomColorProvider.colorScheme.onSurfaceMuted,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
                Text(
                    text = formatPreviewSimpleTimeDuration(
                        seconds = challenge.currentRecordInSeconds,
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = CustomColorProvider.colorScheme.onSurface,
                )
            }
        }
    }
}
