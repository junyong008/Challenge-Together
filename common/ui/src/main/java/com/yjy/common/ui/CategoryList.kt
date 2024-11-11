package com.yjy.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.extensions.getDisplayNameResId
import com.yjy.common.designsystem.extensions.getIconResId
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.model.challenge.core.Category

@Composable
fun CategoryList(
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
                    R.string.common_ui_category_list_all
                } else {
                    category.getDisplayNameResId()
                },
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
    val shape = MaterialTheme.shapes.medium
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
                },
            )
            .clickable(
                role = Role.RadioButton,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(CustomColorProvider.colorScheme.background),
        ) {
            Icon(
                ImageVector.vectorResource(id = category.getIconResId()),
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
            ),
        )
    }
}

@ComponentPreviews
@Composable
fun CategoryListPreview() {
    ChallengeTogetherTheme {
        CategoryList(
            selectedCategory = Category.ALL,
            onCategorySelected = { _, _ -> },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
