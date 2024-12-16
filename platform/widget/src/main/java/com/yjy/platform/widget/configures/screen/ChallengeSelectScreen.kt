package com.yjy.platform.widget.configures.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.extensions.getDisplayNameResId
import com.yjy.common.designsystem.extensions.getIconResId
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.EmptyBody
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.platform.widget.R
import com.yjy.platform.widget.components.CircularProgressBar
import com.yjy.platform.widget.util.calculateProgressPercentage

@Composable
fun ChallengeSelectScreen(
    challenges: List<SimpleStartedChallenge>,
    selectedChallenge: SimpleStartedChallenge?,
    onChallengeSelect: (SimpleStartedChallenge) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (challenges.isEmpty()) {
        EmptyBody(
            title = stringResource(id = R.string.platform_widget_no_challenges),
            description = stringResource(id = R.string.platform_widget_no_challenges_description),
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.padding(horizontal = 16.dp),
        ) {
            items(
                items = challenges,
                key = { it.id },
            ) { challenge ->
                ChallengeItem(
                    challenge = challenge,
                    isSelected = challenge.id == selectedChallenge?.id,
                    onClick = { onChallengeSelect(challenge) },
                )
            }
        }
    }
}

@Composable
private fun ChallengeItem(
    challenge: SimpleStartedChallenge,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.medium

    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape)
            .background(CustomColorProvider.colorScheme.surface)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = CustomColorProvider.colorScheme.brand,
                        shape = shape,
                    )
                } else {
                    Modifier
                },
            )
            .clickable(
                role = Role.RadioButton,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(CustomColorProvider.colorScheme.background),
        ) {
            Icon(
                ImageVector.vectorResource(id = challenge.category.getIconResId()),
                contentDescription = stringResource(id = challenge.category.getDisplayNameResId()),
                tint = CustomColorProvider.colorScheme.onBackgroundMuted,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp),
            )
            CircularProgressBar(
                percentage = challenge.calculateProgressPercentage(),
                icon = painterResource(challenge.category.getIconResId()),
                iconColor = CustomColorProvider.colorScheme.onBackgroundMuted,
                progressColor = CustomColorProvider.colorScheme.brand,
                backgroundColor = CustomColorProvider.colorScheme.background,
                size = 50,
                thickness = 3f,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = challenge.title,
                color = CustomColorProvider.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = challenge.description,
                color = CustomColorProvider.colorScheme.onSurfaceMuted,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        RadioButton(
            selected = isSelected,
            onClick = null,
            modifier = Modifier.padding(end = 16.dp),
            colors = RadioButtonDefaults.colors(
                selectedColor = CustomColorProvider.colorScheme.brand,
                unselectedColor = CustomColorProvider.colorScheme.onSurfaceMuted,
            ),
        )
    }
}
