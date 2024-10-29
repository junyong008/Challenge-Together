package com.yjy.feature.addchallenge

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.designsystem.ThemePreviews
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherBottomAppBar
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.extensions.getDisplayNameResId
import com.yjy.common.designsystem.extensions.getIconResId
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.feature.addchallenge.model.AddChallengeUiAction
import com.yjy.feature.addchallenge.model.AddChallengeUiState
import com.yjy.feature.addchallenge.navigation.AddChallengeStrings
import com.yjy.model.challenge.core.Category

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
                titleRes = AddChallengeStrings.feature_addchallenge_title_set_category,
                descriptionRes = AddChallengeStrings.feature_addchallenge_description_set_category,
            )
            Spacer(modifier = Modifier.height(50.dp))
            CategoryList(
                selectedCategory = uiState.category,
                onCategorySelected = { category, title ->
                    processAction(
                        AddChallengeUiAction.OnSelectCategory(
                            category = category,
                            title = title
                        )
                    )
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CategoryList(
    selectedCategory: Category,
    onCategorySelected: (Category, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        items(Category.entries) { category ->
            val categoryTitle = stringResource(
                id = if (category == Category.ALL) {
                    AddChallengeStrings.feature_addchallenge_my_challenge
                } else {
                    category.getDisplayNameResId()
                }
            )

            CategoryItem(
                category = category,
                categoryTitle = categoryTitle,
                isSelected = category == selectedCategory,
                onClick = {
                    onCategorySelected(category, categoryTitle)
                },
            )
        }
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    categoryTitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.small
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
                }
            )
            .clickable(
                role = Role.RadioButton,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(CustomColorProvider.colorScheme.background),
        ) {
            Icon(
                painter = painterResource(id = category.getIconResId()),
                contentDescription = categoryTitle,
                tint = CustomColorProvider.colorScheme.onBackgroundMuted,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp),
            )
        }
        Text(
            text = categoryTitle,
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(1f),
        )
        RadioButton(
            selected = isSelected,
            onClick = null,
            modifier = Modifier.padding(end = 16.dp),
            colors = RadioButtonDefaults.colors(
                selectedColor = CustomColorProvider.colorScheme.brand,
                unselectedColor = CustomColorProvider.colorScheme.onSurfaceMuted,
            )
        )
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
