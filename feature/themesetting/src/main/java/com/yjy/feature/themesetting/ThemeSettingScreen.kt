package com.yjy.feature.themesetting

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.themesetting.model.ThemeSettingUiAction
import com.yjy.feature.themesetting.model.ThemeSettingUiState

@Composable
internal fun ThemeSettingRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ThemeSettingViewModel = hiltViewModel(),
) {
    val themeState by viewModel.themeState.collectAsStateWithLifecycle()

    ThemeSettingScreen(
        modifier = modifier,
        themeState = themeState,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
    )
}

@Composable
internal fun ThemeSettingScreen(
    modifier: Modifier = Modifier,
    themeState: ThemeSettingUiState = ThemeSettingUiState.Loading,
    processAction: (ThemeSettingUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    LaunchedEffect(themeState) {
        if (themeState is ThemeSettingUiState.Success) {
            AppCompatDelegate.setDefaultNightMode(
                when (themeState.isDarkTheme) {
                    true -> AppCompatDelegate.MODE_NIGHT_YES
                    false -> AppCompatDelegate.MODE_NIGHT_NO
                    null -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                },
            )
        }
    }

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                onNavigationClick = onBackClick,
                titleRes = R.string.feature_themesetting_title,
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier.consumeWindowInsets(WindowInsets.navigationBars),
    ) { padding ->
        when (themeState) {
            is ThemeSettingUiState.Loading -> LoadingWheel()
            is ThemeSettingUiState.Success -> {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ThemeItem(
                        title = stringResource(id = R.string.feature_themesetting_system_theme),
                        isSelected = themeState.isDarkTheme == null,
                        onClick = { processAction(ThemeSettingUiAction.OnSelectSystemTheme) },
                    )
                    ThemeItem(
                        title = stringResource(id = R.string.feature_themesetting_light_theme),
                        isSelected = themeState.isDarkTheme == false,
                        onClick = { processAction(ThemeSettingUiAction.OnSelectLightTheme) },
                    )
                    ThemeItem(
                        title = stringResource(id = R.string.feature_themesetting_dark_theme),
                        isSelected = themeState.isDarkTheme == true,
                        onClick = { processAction(ThemeSettingUiAction.OnSelectDarkTheme) },
                    )
                }
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
            )
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
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

@DevicePreviews
@Composable
fun ThemeSettingScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            ThemeSettingScreen(
                modifier = Modifier.fillMaxSize(),
                themeState = ThemeSettingUiState.Success(null),
            )
        }
    }
}
