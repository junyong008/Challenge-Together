package com.yjy.feature.addchallenge

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.designsystem.ThemePreviews
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherBottomAppBar
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.CategoryList
import com.yjy.feature.addchallenge.model.AddChallengeUiAction
import com.yjy.feature.addchallenge.model.AddChallengeUiState

@Composable
internal fun SetCategoryRoute(
    onBackClick: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddChallengeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SetCategoryScreen(
        modifier = modifier,
        uiState = uiState,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onContinue = onContinue,
    )
}

@Composable
internal fun SetCategoryScreen(
    modifier: Modifier = Modifier,
    uiState: AddChallengeUiState = AddChallengeUiState(),
    processAction: (AddChallengeUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onContinue: () -> Unit = {},
) {
    Scaffold(
        bottomBar = {
            ChallengeTogetherBottomAppBar(
                onBackClick = onBackClick,
                onContinueClick = onContinue,
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
                titleRes = R.string.feature_addchallenge_title_set_category,
                descriptionRes = R.string.feature_addchallenge_description_set_category,
            )
            Spacer(modifier = Modifier.height(50.dp))
            CategoryList(
                selectedCategory = uiState.category,
                onCategorySelected = { category, title ->
                    processAction(
                        AddChallengeUiAction.OnSelectCategory(
                            category = category,
                            title = title,
                        ),
                    )
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@ThemePreviews
@Composable
fun SetCategoryScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            SetCategoryScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
