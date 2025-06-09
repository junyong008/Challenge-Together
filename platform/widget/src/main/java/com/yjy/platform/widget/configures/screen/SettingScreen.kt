package com.yjy.platform.widget.configures.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.platform.widget.R
import com.yjy.platform.widget.components.CustomSlider
import com.yjy.platform.widget.model.ThemeType

@Composable
fun SettingScreen(
    backgroundAlpha: Float,
    themeType: ThemeType,
    onBackgroundAlphaChange: (Float) -> Unit,
    onThemeTypeChange: (ThemeType) -> Unit,
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
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
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
                    modifier = Modifier.widthIn(min = 60.dp),
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.platform_widget_theme_setting),
                style = MaterialTheme.typography.bodyMedium,
                color = CustomColorProvider.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(CustomColorProvider.colorScheme.surface),
            ) {
                ThemeItem(
                    title = stringResource(R.string.platform_widget_system_theme),
                    isSelected = themeType.value == ThemeType.SYSTEM.value,
                    onClick = { onThemeTypeChange(ThemeType.SYSTEM) },
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = CustomColorProvider.colorScheme.divider,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
                ThemeItem(
                    title = stringResource(R.string.platform_widget_light_theme),
                    isSelected = themeType.value == ThemeType.LIGHT.value,
                    onClick = { onThemeTypeChange(ThemeType.LIGHT) },
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = CustomColorProvider.colorScheme.divider,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
                ThemeItem(
                    title = stringResource(R.string.platform_widget_dark_theme),
                    isSelected = themeType.value == ThemeType.DARK.value,
                    onClick = { onThemeTypeChange(ThemeType.DARK) },
                )
            }
        }
    }
}

@Composable
private fun ThemeItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.medium
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .clip(shape)
            .clickable(
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.weight(1f),
        )
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = CustomColorProvider.colorScheme.brand,
                unselectedColor = CustomColorProvider.colorScheme.onSurfaceMuted,
            ),
        )
    }
}
