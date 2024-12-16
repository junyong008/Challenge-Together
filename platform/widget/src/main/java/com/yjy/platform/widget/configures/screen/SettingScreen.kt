package com.yjy.platform.widget.configures.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.platform.widget.R
import com.yjy.platform.widget.components.CustomSlider

@Composable
fun SettingScreen(
    backgroundAlpha: Float,
    onBackgroundAlphaChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    previewContent: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
                .background(CustomColorProvider.colorScheme.disable),
            contentAlignment = Alignment.Center,
        ) {
            previewContent()
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.platform_widget_opacity),
            style = MaterialTheme.typography.bodyMedium,
            color = CustomColorProvider.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(CustomColorProvider.colorScheme.surface)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CustomSlider(
                value = backgroundAlpha,
                onValueChange = onBackgroundAlphaChange,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${(backgroundAlpha * 100).toInt()}%",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = CustomColorProvider.colorScheme.onBackground,
                modifier = Modifier.widthIn(min = 50.dp),
            )
        }
    }
}
