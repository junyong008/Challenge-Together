package com.yjy.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.util.formatTimeDuration
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherChip
import com.yjy.common.designsystem.component.ClickableText
import com.yjy.common.designsystem.component.DebouncedIconButton
import com.yjy.common.designsystem.component.RibbonMedal
import com.yjy.common.designsystem.component.RoundedLinearProgressBar
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.home.model.HomeUiAction
import com.yjy.feature.home.model.HomeUiEvent
import com.yjy.feature.home.model.HomeUiState
import com.yjy.feature.home.navigation.HomeStrings
import com.yjy.model.Tier
import com.yjy.model.challenge.Category
import com.yjy.model.challenge.SortOrder
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
            .verticalScroll(rememberScrollState()),
    ) {
        HomeTopBar(
            onShowCompleteChallengeClick = {},
            onShowNotificationClick = {},
            hasNewNotification = false,
        )
        ProfileCard(
            nickname = "닉네임 예시",
            remainDayForNextTier = 7,
            tierProgress = 0.7f,
            tier = Tier.IRON,
        )
        Spacer(modifier = Modifier.height(8.dp))
        HighestRecordCard(
            highestRecordInSeconds = 500351515L,
        )
        Spacer(modifier = Modifier.height(24.dp))
        MyChallengeContents(
            sortOrder = SortOrder.LATEST,
            categories = Category.entries,
            selectedCategory = Category.ALL,
            onCategorySelected = {},
        )
    }
}

@Composable
private fun MyChallengeContents(
    sortOrder: SortOrder,
    categories: List<Category>,
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MyChallengeTitle(sortOrder)
        CategoryChipGroup(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
        )
    }
}

@Composable
private fun CategoryChipGroup(
    categories: List<Category>,
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(categories) { category ->
            ChallengeTogetherChip(
                textResId = category.displayNameResId,
                isSelected = category == selectedCategory,
                onSelectionChanged = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
private fun MyChallengeTitle(
    sortOrder: SortOrder,
) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 8.dp)
            .wrapContentHeight()
            .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(id = HomeStrings.feature_home_my_challenges),
            color = CustomColorProvider.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Row(
            modifier = Modifier.align(Alignment.BottomEnd),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = ChallengeTogetherIcons.Sort),
                contentDescription = null,
                tint = CustomColorProvider.colorScheme.onBackgroundMuted,
                modifier = Modifier.size(20.dp),
            )
            ClickableText(
                text = stringResource(id = sortOrder.displayNameResId),
                onClick = {},
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
            )
        }
    }
}

@Composable
private fun HighestRecordCard(
    highestRecordInSeconds: Long,
) {
    val formattedRecord: String = formatTimeDuration(highestRecordInSeconds)
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .wrapContentHeight()
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(CustomColorProvider.colorScheme.surface),
    ) {
        Text(
            text = stringResource(id = HomeStrings.feature_home_current_highest_record),
            color = CustomColorProvider.colorScheme.onSurfaceMuted,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 16.dp, top = 16.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formattedRecord,
            color = CustomColorProvider.colorScheme.red,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 16.dp, end = 16.dp),
        )
    }
}

@Composable
private fun ProfileCard(
    nickname: String,
    remainDayForNextTier: Int,
    tierProgress: Float,
    tier: Tier,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .height(180.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(CustomColorProvider.colorScheme.surface),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(1f)
                .align(Alignment.Bottom),
        ) {
            Text(
                text = nickname,
                color = CustomColorProvider.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = HomeStrings.feature_home_remain_time_for_next_tier, remainDayForNextTier),
                color = CustomColorProvider.colorScheme.onSurfaceMuted,
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(modifier = Modifier.height(4.dp))
            RoundedLinearProgressBar(
                progress = { tierProgress },
                modifier = Modifier
                    .height(15.dp)
                    .fillMaxWidth(),
            )
        }
        RibbonMedal(
            tier = tier,
            modifier = Modifier
                .padding(end = 10.dp, bottom = 24.dp, top = 24.dp)
                .align(Alignment.CenterVertically),
        )
    }
}

@Composable
private fun HomeTopBar(
    onShowCompleteChallengeClick: () -> Unit,
    onShowNotificationClick: () -> Unit,
    hasNewNotification: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = stringResource(id = HomeStrings.feature_home_title),
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp),
        )
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
        ) {
            DebouncedIconButton(
                onClick = onShowCompleteChallengeClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = ChallengeTogetherIcons.CompleteChallenge),
                    contentDescription = stringResource(
                        id = HomeStrings.feature_home_complete_challenge_description,
                    ),
                    tint = CustomColorProvider.colorScheme.onBackgroundMuted,
                )
            }
            DebouncedIconButton(
                onClick = onShowNotificationClick,
                modifier = Modifier.size(40.dp)
            ) {
                BadgedBox(
                    badge = {
                        if (hasNewNotification) {
                            Badge(containerColor = CustomColorProvider.colorScheme.brand)
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource(id = ChallengeTogetherIcons.Notification),
                        contentDescription = stringResource(
                            id = HomeStrings.feature_home_notification_description,
                        ),
                        tint = CustomColorProvider.colorScheme.onBackgroundMuted,
                    )
                }
            }
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
