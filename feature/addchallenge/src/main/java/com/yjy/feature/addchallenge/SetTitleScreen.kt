package com.yjy.feature.addchallenge

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.designsystem.ThemePreviews
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherBottomAppBar
import com.yjy.common.designsystem.component.ChallengeTogetherTextField
import com.yjy.common.designsystem.component.SingleLineTextField
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.feature.addchallenge.model.AddChallengeUiAction
import com.yjy.feature.addchallenge.model.AddChallengeUiEvent
import com.yjy.feature.addchallenge.model.AddChallengeUiState
import com.yjy.feature.addchallenge.navigation.AddChallengeStrings
import com.yjy.model.challenge.Mode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun SetTitleRoute(
    onBackClick: () -> Unit,
    onContinueToSetStartDate: () -> Unit,
    onContinueToSetTargetDay: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddChallengeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SetTitleScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onContinueToSetStartDate = onContinueToSetStartDate,
        onContinueToSetTargetDay = onContinueToSetTargetDay,
    )
}

@Composable
internal fun SetTitleScreen(
    modifier: Modifier = Modifier,
    uiState: AddChallengeUiState = AddChallengeUiState(),
    uiEvent: Flow<AddChallengeUiEvent> = flowOf(),
    processAction: (AddChallengeUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onContinueToSetStartDate: () -> Unit = {},
    onContinueToSetTargetDay: () -> Unit = {},
) {
    Scaffold(
        bottomBar = {
            ChallengeTogetherBottomAppBar(
                onBackClick = onBackClick,
                onContinueClick = {
                    when (uiState.mode) {
                        Mode.CHALLENGE -> onContinueToSetTargetDay()
                        Mode.FREE -> onContinueToSetStartDate()
                        else -> onBackClick()
                    }
                },
                enableContinueButton = uiState.title.isNotBlank(),
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            TitleWithDescription(
                titleRes = AddChallengeStrings.feature_addchallenge_title_set_title,
                descriptionRes = AddChallengeStrings.feature_addchallenge_description_set_title,
            )
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = stringResource(id = AddChallengeStrings.feature_addchallenge_title),
                color = CustomColorProvider.colorScheme.onBackground,
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            SingleLineTextField(
                value = uiState.title,
                onValueChange = { processAction(AddChallengeUiAction.OnTitleUpdated(it)) },
                placeholderText = stringResource(id = AddChallengeStrings.feature_addchallenge_title_placeholder),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = AddChallengeStrings.feature_addchallenge_description_optional),
                color = CustomColorProvider.colorScheme.onBackground,
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            ChallengeTogetherTextField(
                value = uiState.description,
                onValueChange = { processAction(AddChallengeUiAction.OnDescriptionUpdated(it)) },
                placeholderText = uiState.title.ifBlank {
                    stringResource(id = AddChallengeStrings.feature_addchallenge_description_placeholder)
                },
                modifier = Modifier.height(180.dp),
                contentAlignment = Alignment.TopStart,
            )
        }
    }
}

@ThemePreviews
@Composable
fun SetTitleScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            SetTitleScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
