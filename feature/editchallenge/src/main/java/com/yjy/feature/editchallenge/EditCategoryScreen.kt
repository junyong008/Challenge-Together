package com.yjy.feature.editchallenge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.CategoryList
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.editchallenge.component.ConfirmButton
import com.yjy.feature.editchallenge.model.EditChallengeUiAction
import com.yjy.feature.editchallenge.model.EditChallengeUiEvent
import com.yjy.feature.editchallenge.model.EditChallengeUiState
import com.yjy.model.challenge.core.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun EditCategoryRoute(
    challengeId: Int,
    selectedCategory: Category,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditChallengeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EditCategoryScreen(
        modifier = modifier,
        challengeId = challengeId,
        selectedCategory = selectedCategory,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun EditCategoryScreen(
    modifier: Modifier = Modifier,
    challengeId: Int = 0,
    selectedCategory: Category = Category.ALL,
    uiState: EditChallengeUiState = EditChallengeUiState(),
    uiEvent: Flow<EditChallengeUiEvent> = flowOf(),
    processAction: (EditChallengeUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    var currentCategory by rememberSaveable { mutableStateOf(selectedCategory) }

    val editSuccessMessage = stringResource(id = R.string.feature_editchallenge_edit_successful)
    val editFailedMessage = stringResource(id = R.string.feature_editchallenge_edit_failed)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            is EditChallengeUiEvent.EditSuccess -> {
                onShowSnackbar(SnackbarType.SUCCESS, editSuccessMessage)
                onBackClick()
            }

            is EditChallengeUiEvent.EditFailure -> onShowSnackbar(SnackbarType.ERROR, editFailedMessage)
        }
    }

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                onNavigationClick = onBackClick,
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
            ) {
                ConfirmButton(
                    onClick = {
                        processAction(
                            EditChallengeUiAction.OnEditCategory(
                                challengeId = challengeId,
                                category = currentCategory,
                            ),
                        )
                    },
                    enabled = !uiState.isEditing,
                    isLoading = uiState.isEditing,
                )
            }
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
            TitleWithDescription(
                titleRes = R.string.feature_editchallenge_edit_category_title,
                descriptionRes = R.string.feature_editchallenge_edit_category_description,
            )
            Spacer(modifier = Modifier.height(50.dp))
            CategoryList(
                selectedCategory = currentCategory,
                onCategorySelected = { category, _ ->
                    if (!uiState.isEditing) currentCategory = category
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@DevicePreviews
@Composable
fun EditCategoryScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            EditCategoryScreen(
                modifier = Modifier.fillMaxSize(),
                selectedCategory = Category.QUIT_DRUGS,
            )
        }
    }
}
