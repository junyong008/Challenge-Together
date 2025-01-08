package com.yjy.platform.widget.configures.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.platform.widget.R

@Composable
fun HiddenScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = ChallengeTogetherIcons.Hide),
                contentDescription = stringResource(id = R.string.platform_widget_hidden_due_to_app_lock),
                tint = CustomColorProvider.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.platform_widget_hidden_due_to_app_lock),
                style = MaterialTheme.typography.labelSmall,
                color = CustomColorProvider.colorScheme.onSurfaceMuted,
                textAlign = TextAlign.Center,
            )
        }
    }
}
