package com.yjy.feature.together

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.yjy.common.core.extensions.clickableSingle
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.extensions.getDisplayNameResId
import com.yjy.common.designsystem.extensions.getIconResId
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.model.challenge.core.Category

@Composable
internal fun ListRoute(
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListScreen(
        modifier = modifier,
        onCategorySelected = onCategorySelected,
    )
}

@Composable
internal fun ListScreen(
    modifier: Modifier = Modifier,
    onCategorySelected: (Category) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        TitleWithDescription(
            titleRes = R.string.feature_together_title,
            descriptionRes = R.string.feature_together_description,
            modifier = Modifier.padding(32.dp),
        )
        CategoryList(
            onCategorySelected = onCategorySelected,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@Composable
private fun CategoryList(
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    val categories = Category.entries.filter { it != Category.ALL }
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize(),
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            CategoryItem(
                category = Category.ALL,
                categoryTitle = stringResource(id = Category.ALL.getDisplayNameResId()),
                iconColor = CustomColorProvider.colorScheme.brandDim,
                onClick = { onCategorySelected(Category.ALL) },
            )
        }
        items(
            count = categories.size,
            key = { index -> categories[index].name },
        ) { index ->
            val category = categories[index]
            CategoryItem(
                category = category,
                categoryTitle = stringResource(id = category.getDisplayNameResId()),
                onClick = { onCategorySelected(category) },
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    categoryTitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconColor: Color = CustomColorProvider.colorScheme.onBackgroundMuted,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(MaterialTheme.shapes.large)
            .background(CustomColorProvider.colorScheme.surface)
            .clickableSingle { onClick() }
            .padding(16.dp),
    ) {
        Text(
            text = categoryTitle,
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(CustomColorProvider.colorScheme.background)
                .align(Alignment.End),
        ) {
            Icon(
                ImageVector.vectorResource(id = category.getIconResId()),
                contentDescription = categoryTitle,
                tint = iconColor,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp),
            )
        }
    }
}

@DevicePreviews
@Composable
fun ListScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            ListScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
