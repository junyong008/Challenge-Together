package com.yjy.platform.widget.configures.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.platform.widget.R
import com.yjy.platform.widget.util.formatPreviewDaysOnly

@Composable
fun ChallengeSimpleWidgetPreview(
    challenge: SimpleStartedChallenge?,
    shouldHideContent: Boolean,
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
        when {
            shouldHideContent -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = ChallengeTogetherIcons.Hide),
                        contentDescription = stringResource(id = R.string.platform_widget_hidden_due_to_app_lock),
                        tint = CustomColorProvider.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.platform_widget_hidden_due_to_app_lock),
                        style = MaterialTheme.typography.labelSmall,
                        color = CustomColorProvider.colorScheme.onSurfaceMuted,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            challenge == null -> {
                Text(
                    text = stringResource(R.string.platform_widget_no_challenge),
                    style = MaterialTheme.typography.bodyLarge,
                    color = CustomColorProvider.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(
                        text = challenge.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CustomColorProvider.colorScheme.onSurface,
                    )
                    Text(
                        text = challenge.description,
                        style = MaterialTheme.typography.labelSmall,
                        color = CustomColorProvider.colorScheme.onSurfaceMuted,
                        modifier = Modifier.padding(vertical = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = formatPreviewDaysOnly(
                            seconds = challenge.currentRecordInSeconds,
                        ),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.displayMedium,
                        color = CustomColorProvider.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.End),
                    )
                }
            }
        }
    }
}
