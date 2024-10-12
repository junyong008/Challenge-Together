package com.yjy.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.StableImage
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.home.model.HomeUiAction
import com.yjy.feature.home.model.HomeUiEvent
import com.yjy.feature.home.model.HomeUiState
import com.yjy.feature.home.navigation.HomeStrings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
    )
}

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState = HomeUiState(),
    uiEvent: Flow<HomeUiEvent> = flowOf(),
    processAction: (HomeUiAction) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Box(modifier = Modifier.height(120.dp)) {
            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxSize()
                    .align(Alignment.BottomCenter)
                    .clip(MaterialTheme.shapes.large)
                    .background(CustomColorProvider.colorScheme.surface),
            ) {
                StableImage(
                    drawableResId = R.drawable.image_mindfull,
                    descriptionResId = HomeStrings.feature_home_image_mind_full_description,
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp),
                )
            }
            StableImage(
                drawableResId = ChallengeTogetherIcons.IronRibbon,
                descriptionResId = HomeStrings.feature_home_medal_description,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(70.dp)
                    .padding(start = 8.dp),
            )
        }
    }
}

@DevicePreviews
@Composable
fun HomeScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            HomeScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
