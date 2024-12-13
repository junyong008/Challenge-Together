package com.yjy.feature.addchallenge

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yjy.common.core.extensions.clickableSingle
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.ThemePreviews
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.StableImage
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.feature.addchallenge.model.AddChallengeUiAction
import com.yjy.feature.addchallenge.model.AddChallengeUiEvent
import com.yjy.model.challenge.core.Mode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.yjy.common.designsystem.R as designSystemR

@Composable
internal fun SetModeRoute(
    onBackClick: () -> Unit,
    onSelectMode: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddChallengeViewModel = hiltViewModel(),
) {
    SetModeScreen(
        modifier = modifier,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onSelectMode = onSelectMode,
    )
}

@Composable
internal fun SetModeScreen(
    modifier: Modifier = Modifier,
    uiEvent: Flow<AddChallengeUiEvent> = flowOf(),
    processAction: (AddChallengeUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onSelectMode: () -> Unit = {},
) {
    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            is AddChallengeUiEvent.ModeSelected -> onSelectMode()
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                onNavigationClick = onBackClick,
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier.consumeWindowInsets(WindowInsets.navigationBars),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
        ) {
            TitleWithDescription(
                titleRes = R.string.feature_addchallenge_title_set_mode,
                descriptionRes = R.string.feature_addchallenge_description_set_mode,
            )
            Spacer(modifier = Modifier.height(50.dp))
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                ModeButton(
                    drawableResId = designSystemR.drawable.image_trophy,
                    titleResId = R.string.feature_addchallenge_mode_challenge,
                    descriptionResId = R.string.feature_addchallenge_mode_challenge_description,
                    onClick = { processAction(AddChallengeUiAction.OnSelectMode(Mode.CHALLENGE)) },
                )
                Spacer(modifier = Modifier.height(8.dp))
                ModeButton(
                    imagePadding = 5.dp,
                    drawableResId = designSystemR.drawable.image_calendar,
                    titleResId = R.string.feature_addchallenge_mode_free,
                    descriptionResId = R.string.feature_addchallenge_mode_free_description,
                    onClick = { processAction(AddChallengeUiAction.OnSelectMode(Mode.FREE)) },
                )
            }
        }
    }
}

@Composable
private fun ModeButton(
    @DrawableRes drawableResId: Int,
    @StringRes titleResId: Int,
    @StringRes descriptionResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imagePadding: Dp = 0.dp,
) {
    val shape = MaterialTheme.shapes.large
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape)
            .background(CustomColorProvider.colorScheme.surface)
            .clickableSingle(onClick = onClick)
            .padding(24.dp),
    ) {
        StableImage(
            drawableResId = drawableResId,
            descriptionResId = titleResId,
            modifier = Modifier
                .size(55.dp)
                .padding(imagePadding),
        )
        TitleWithDescription(
            titleRes = titleResId,
            descriptionRes = descriptionResId,
            titleStyle = MaterialTheme.typography.bodyLarge,
            descriptionStyle = MaterialTheme.typography.labelSmall,
            titleColor = CustomColorProvider.colorScheme.onSurface,
            descriptionColor = CustomColorProvider.colorScheme.onSurfaceMuted,
            spacing = 4.dp,
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight()
                .padding(start = 24.dp),
        )
    }
}

@ThemePreviews
@Composable
fun SetModeScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            SetModeScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
