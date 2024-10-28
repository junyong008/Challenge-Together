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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.yjy.common.core.util.formatTimeDuration
import com.yjy.common.designsystem.component.BaseBottomSheet
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherChip
import com.yjy.common.designsystem.component.ClickableText
import com.yjy.common.designsystem.component.DebouncedIconButton
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.component.LottieImage
import com.yjy.common.designsystem.component.RibbonMedal
import com.yjy.common.designsystem.component.RoundedLinearProgressBar
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.StableImage
import com.yjy.common.designsystem.extensions.getDisplayNameResId
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.common.ui.ErrorBody
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
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState = HomeUiState(),
    uiEvent: Flow<HomeUiEvent> = flowOf(),
    processAction: (HomeUiAction) -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            processAction(HomeUiAction.OnScreenLoad)
        }
    }

    if (uiState.recentCompletedChallengeTitles.isNotEmpty()) {
        ChallengeCompletedBottomSheet(
            completedChallenges = uiState.recentCompletedChallengeTitles,
            onDismiss = { processAction(HomeUiAction.OnCloseCompletedChallengeNotification) },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (!uiState.hasError && !uiState.isLoading) {
                    Modifier.verticalScroll(rememberScrollState())
                } else {
                    Modifier
                }
            ),
    ) {
        HomeTopBar(
            onShowCompleteChallengeClick = {},
            onShowNotificationClick = {},
            hasNewNotification = false,
        )

        when {
            uiState.isLoading -> LoadingWheel()
            uiState.hasError -> ErrorBody(onClickRetry = { processAction(HomeUiAction.OnRetryClick) })
            else -> {
                ProfileCard(
                    userName = uiState.userName,
                    remainDayForNextTier = 7,
                    tierProgress = 0.7f,
                    tier = Tier.IRON,
                )
                Spacer(modifier = Modifier.height(8.dp))
                HighestRecordCard(highestRecordInSeconds = uiState.currentBestRecordInSeconds)
                Spacer(modifier = Modifier.height(24.dp))
                MyChallengeSection(
                    sortOrder = SortOrder.LATEST,
                    categories = Category.entries,
                    selectedCategory = Category.ALL,
                    onCategorySelected = {},
                )
            }
        }
    }
}

@Composable
private fun ChallengeCompletedBottomSheet(
    completedChallenges: List<String>,
    onDismiss: () -> Unit,
) {
    val completedTitles = completedChallenges.joinToString(", ") { "\"$it\"" }

    BaseBottomSheet(onDismiss = onDismiss) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(id = HomeStrings.feature_home_congratulations),
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = HomeStrings.feature_home_challenge_success, completedTitles),
            color = CustomColorProvider.colorScheme.onSurfaceMuted,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
        )
        Box(contentAlignment = Alignment.Center) {
            StableImage(
                drawableResId = R.drawable.image_congrats,
                descriptionResId = HomeStrings.feature_home_congratulations_image_description,
                modifier = Modifier.size(150.dp)
            )
            LottieImage(
                animationResId = R.raw.anim_congrats,
                repeatCount = 1,
                modifier = Modifier.size(250.dp)
            )
        }
    }
}

@Composable
private fun MyChallengeSection(
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
                textResId = category.getDisplayNameResId(),
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
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = HomeStrings.feature_home_my_challenges),
            color = CustomColorProvider.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = ChallengeTogetherIcons.Sort),
                contentDescription = stringResource(
                    id = HomeStrings.feature_home_sort_challenge_content_description
                ),
                tint = CustomColorProvider.colorScheme.onBackgroundMuted,
                modifier = Modifier.size(20.dp),
            )
            ClickableText(
                text = stringResource(id = sortOrder.getDisplayNameResId()),
                textAlign = TextAlign.End,
                onClick = {},
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
            )
        }
    }
}

@Composable
private fun HighestRecordCard(highestRecordInSeconds: Long) {
    val formattedRecord: String = formatTimeDuration(highestRecordInSeconds)

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(CustomColorProvider.colorScheme.surface)
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(id = HomeStrings.feature_home_current_highest_record),
            color = CustomColorProvider.colorScheme.onSurfaceMuted,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.Start),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formattedRecord,
            color = CustomColorProvider.colorScheme.red,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.End,
            modifier = Modifier.align(Alignment.End),
        )
    }
}

@Composable
private fun ProfileCard(
    userName: String,
    remainDayForNextTier: Int,
    tierProgress: Float,
    tier: Tier,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .heightIn(min = 180.dp)
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
                text = userName,
                color = CustomColorProvider.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(
                    id = HomeStrings.feature_home_remain_time_for_next_tier,
                    remainDayForNextTier
                ),
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
                .size(150.dp)
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = HomeStrings.feature_home_title),
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f),
        )
        Row(modifier = Modifier.padding(end = 8.dp)) {
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
