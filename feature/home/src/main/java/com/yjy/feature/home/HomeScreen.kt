package com.yjy.feature.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.yjy.common.core.util.formatTimeDuration
import com.yjy.common.designsystem.component.BaseBottomSheet
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherChip
import com.yjy.common.designsystem.component.ChallengeTogetherOutlinedButton
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
import com.yjy.common.ui.StartedChallengeCard
import com.yjy.common.ui.WaitingChallengeCard
import com.yjy.feature.home.model.ChallengeSyncUiState
import com.yjy.feature.home.model.HomeUiAction
import com.yjy.feature.home.model.HomeUiEvent
import com.yjy.feature.home.model.HomeUiState
import com.yjy.feature.home.model.TierUpAnimationState
import com.yjy.feature.home.model.UnViewedNotificationUiState
import com.yjy.feature.home.model.UserNameUiState
import com.yjy.feature.home.model.getNameOrDefault
import com.yjy.feature.home.model.hasNewNotification
import com.yjy.feature.home.model.isError
import com.yjy.feature.home.model.isInitialLoading
import com.yjy.feature.home.model.isLoading
import com.yjy.feature.home.model.isTimeSyncLoading
import com.yjy.feature.home.navigation.HomeStrings
import com.yjy.model.challenge.StartedChallenge
import com.yjy.model.challenge.WaitingChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.SortOrder
import com.yjy.model.common.Tier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun HomeRoute(
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentTier by viewModel.currentTier.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
    val userNameUiState by viewModel.userName.collectAsStateWithLifecycle()
    val unViewedNotificationUiState by viewModel.unViewedNotificationState.collectAsStateWithLifecycle()
    val challengeSyncUiState by viewModel.challengeSyncState.collectAsStateWithLifecycle()
    val startedChallenges by viewModel.startedChallenges.collectAsStateWithLifecycle()
    val waitingChallenges by viewModel.waitingChallenges.collectAsStateWithLifecycle()
    val recentCompletedChallenges by viewModel.recentCompletedChallenges.collectAsStateWithLifecycle()

    HomeScreen(
        modifier = modifier,
        currentTier = currentTier,
        sortOrder = sortOrder,
        userNameUiState = userNameUiState,
        unViewedNotificationUiState = unViewedNotificationUiState,
        challengeSyncUiState = challengeSyncUiState,
        startedChallenges = startedChallenges,
        waitingChallenges = waitingChallenges,
        recentCompletedChallenges = recentCompletedChallenges,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    currentTier: Tier = Tier.IRON,
    sortOrder: SortOrder = SortOrder.LATEST,
    userNameUiState: UserNameUiState = UserNameUiState.Success(""),
    unViewedNotificationUiState: UnViewedNotificationUiState = UnViewedNotificationUiState.Success(0),
    challengeSyncUiState: ChallengeSyncUiState = ChallengeSyncUiState.Success,
    startedChallenges: List<StartedChallenge> = emptyList(),
    waitingChallenges: List<WaitingChallenge> = emptyList(),
    recentCompletedChallenges: List<String> = emptyList(),
    uiState: HomeUiState = HomeUiState(),
    uiEvent: Flow<HomeUiEvent> = flowOf(),
    processAction: (HomeUiAction) -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val hasError = userNameUiState.isError() ||
            unViewedNotificationUiState.isError() ||
            challengeSyncUiState.isError()

    val isLoading = userNameUiState.isLoading() ||
            unViewedNotificationUiState.isLoading() ||
            challengeSyncUiState.isInitialLoading() ||
            challengeSyncUiState.isTimeSyncLoading()

    val homeContents = HomeContents(
        userName = userNameUiState.getNameOrDefault(),
        currentTier = currentTier,
        startedChallenges = startedChallenges,
        waitingChallenges = waitingChallenges,
        selectedCategory = uiState.selectedCategory,
        categories = uiState.categories,
        sortOrder = sortOrder,
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            processAction(HomeUiAction.OnScreenLoad)
        }
    }

    HandleHomeDialogs(
        tierUpAnimation = uiState.tierUpAnimation,
        completedChallengeTitles = recentCompletedChallenges,
        selectedSortOrder = sortOrder,
        shouldShowSortOrderBottomSheet = uiState.shouldShowSortOrderBottomSheet,
        onSortOrderSelected = { processAction(HomeUiAction.OnSortOrderSelect(it)) },
        onDismissTierUp = { processAction(HomeUiAction.OnDismissTierUpAnimation) },
        onDismissCompleted = { processAction(HomeUiAction.OnCloseCompletedChallengeNotification) },
        onDismissSortOrder = { processAction(HomeUiAction.OnDismissSortOrder) },
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (hasError || isLoading) Modifier
                else Modifier.verticalScroll(rememberScrollState())
            ),
    ) {
        HomeTopBar(
            onShowCompleteChallengeClick = {},
            onShowNotificationClick = {},
            hasNewNotification = unViewedNotificationUiState.hasNewNotification(),
        )
        HomeBody(
            uiState = uiState,
            processAction = processAction,
            isLoading = isLoading,
            hasError = hasError,
            homeContents = homeContents,
        )
    }
}

@Composable
private fun HomeBody(
    uiState: HomeUiState,
    processAction: (HomeUiAction) -> Unit,
    isLoading: Boolean,
    hasError: Boolean,
    homeContents: HomeContents,
) {
    when {
        isLoading -> LoadingWheel()
        hasError -> ErrorBody(onClickRetry = { processAction(HomeUiAction.OnRetryClick) })
        else -> HomeContent(uiState = uiState, processAction = processAction, content = homeContents)
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    processAction: (HomeUiAction) -> Unit,
    content: HomeContents
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ProfileSection(
            userName = content.userName,
            remainDayForNextTier = uiState.remainDayForNextTier,
            tierProgress = uiState.tierProgress,
            tier = content.currentTier,
            highestRecordInSeconds = uiState.currentBestRecordInSeconds,
        )
        ChallengesSection(
            startedChallenges = content.startedChallenges,
            waitingChallenges = content.waitingChallenges,
            selectedCategory = content.selectedCategory,
            categories = content.categories,
            sortOrder = content.sortOrder,
            onCategorySelected = { processAction(HomeUiAction.OnCategorySelect(it)) },
            onSortOrderClick = { processAction(HomeUiAction.OnSortOrderClick) },
            onStartedChallengeClick = {},
            onWaitingChallengeClick = {},
        )
    }
}

data class HomeContents(
    val userName: String,
    val currentTier: Tier,
    val startedChallenges: List<StartedChallenge>,
    val waitingChallenges: List<WaitingChallenge>,
    val selectedCategory: Category,
    val categories: List<Category>,
    val sortOrder: SortOrder,
)

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

@Composable
private fun ProfileSection(
    userName: String,
    remainDayForNextTier: Int,
    tierProgress: Float,
    tier: Tier,
    highestRecordInSeconds: Long,
) {
    Column {
        ProfileCard(
            userName = userName,
            remainDayForNextTier = remainDayForNextTier,
            tierProgress = tierProgress,
            tier = tier,
        )
        Spacer(modifier = Modifier.height(8.dp))
        HighestRecordCard(highestRecordInSeconds = highestRecordInSeconds)
        Spacer(modifier = Modifier.height(24.dp))
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
    val progressDescription = when {
        tier == Tier.highestTier -> stringResource(id = HomeStrings.feature_home_highest_tier)
        tierProgress == 0f -> stringResource(id = HomeStrings.feature_home_no_active_challenge_mode)
        else -> stringResource(id = HomeStrings.feature_home_remain_time_for_next_tier, remainDayForNextTier)
    }

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
                text = progressDescription,
                color = CustomColorProvider.colorScheme.onSurfaceMuted,
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(modifier = Modifier.height(4.dp))
            RoundedLinearProgressBar(
                progress = { tierProgress },
                enabled = tier != Tier.highestTier,
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
private fun ChallengesSection(
    startedChallenges: List<StartedChallenge>,
    waitingChallenges: List<WaitingChallenge>,
    selectedCategory: Category,
    categories: List<Category>,
    sortOrder: SortOrder,
    onCategorySelected: (Category) -> Unit,
    onSortOrderClick: () -> Unit,
    onStartedChallengeClick: (StartedChallenge) -> Unit,
    onWaitingChallengeClick: (WaitingChallenge) -> Unit,
) {
    if (startedChallenges.isEmpty() && waitingChallenges.isEmpty()) {
        EmptyChallengesBody()
        return
    }

    Column {
        if (startedChallenges.isNotEmpty()) {
            StartedChallengesSection(
                sortOrder = sortOrder,
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected,
                onSortOrderClick = onSortOrderClick,
                startedChallenges = startedChallenges,
                onChallengeClick = onStartedChallengeClick,
            )
        }

        if (waitingChallenges.isNotEmpty()) {
            WaitingChallengesSection(
                waitingChallenges = waitingChallenges,
                onChallengeClick = onWaitingChallengeClick,
            )
        }
    }
}

@Composable
private fun StartedChallengesSection(
    sortOrder: SortOrder,
    categories: List<Category>,
    selectedCategory: Category,
    startedChallenges: List<StartedChallenge>,
    onSortOrderClick: () -> Unit,
    onCategorySelected: (Category) -> Unit,
    onChallengeClick: (StartedChallenge) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MyChallengeTitle(
            sortOrder = sortOrder,
            onSortOrderClick = onSortOrderClick,
        )
        CategoryChipGroup(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
        )
        Spacer(modifier = Modifier.height(8.dp))
        StartedChallengesList(
            startedChallenges = startedChallenges.filter {
                selectedCategory == Category.ALL || it.category == selectedCategory
            },
            onChallengeClick = onChallengeClick,
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun WaitingChallengesSection(
    waitingChallenges: List<WaitingChallenge>,
    onChallengeClick: (WaitingChallenge) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = stringResource(id = HomeStrings.feature_home_joined_challenges),
            color = CustomColorProvider.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(16.dp))
        WaitingChallengesList(
            waitingChallenges = waitingChallenges,
            onChallengeClick = onChallengeClick,
        )
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
private fun EmptyChallengesBody() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        Text(
            text = stringResource(id = HomeStrings.feature_home_no_active_challenge),
            color = CustomColorProvider.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = HomeStrings.feature_home_start_new_challenge_prompt),
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun StartedChallengesList(
    startedChallenges: List<StartedChallenge>,
    onChallengeClick: (StartedChallenge) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        startedChallenges.forEach { challenge ->
            StartedChallengeCard(
                challenge = challenge,
                onClick = onChallengeClick,
            )
        }
    }
}

@Composable
private fun WaitingChallengesList(
    waitingChallenges: List<WaitingChallenge>,
    onChallengeClick: (WaitingChallenge) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        waitingChallenges.forEach { challenge ->
            WaitingChallengeCard(
                challenge = challenge,
                onClick = onChallengeClick,
            )
        }
    }
}

@Composable
private fun MyChallengeTitle(
    sortOrder: SortOrder,
    onSortOrderClick: () -> Unit,
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
                onClick = onSortOrderClick,
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
            )
        }
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
private fun HandleHomeDialogs(
    tierUpAnimation: TierUpAnimationState?,
    completedChallengeTitles: List<String>,
    selectedSortOrder: SortOrder,
    shouldShowSortOrderBottomSheet: Boolean,
    onSortOrderSelected: (SortOrder) -> Unit,
    onDismissTierUp: () -> Unit,
    onDismissCompleted: () -> Unit,
    onDismissSortOrder: () -> Unit,
) {
    if (tierUpAnimation != null) {
        TierUpAnimationDialog(
            from = tierUpAnimation.from,
            to = tierUpAnimation.to,
            onDismiss = onDismissTierUp,
        )
    }

    if (completedChallengeTitles.isNotEmpty() && tierUpAnimation == null) {
        ChallengeCompletedBottomSheet(
            completedChallenges = completedChallengeTitles,
            onDismiss = onDismissCompleted,
        )
    }

    if (shouldShowSortOrderBottomSheet) {
        SortOrderBottomSheet(
            selectedSortOrder = selectedSortOrder,
            onSelected = onSortOrderSelected,
            onDismiss = onDismissSortOrder,
        )
    }
}

@Composable
private fun SortOrderBottomSheet(
    selectedSortOrder: SortOrder,
    onSelected: (SortOrder) -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss = onDismiss) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(id = HomeStrings.feature_home_sort),
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SortOrder.entries.forEach { sortOrder ->
                SortItem(
                    title = stringResource(id = sortOrder.getDisplayNameResId()),
                    isSelected = sortOrder == selectedSortOrder,
                    onClick = { onSelected(sortOrder) },
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SortItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.medium
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(CustomColorProvider.colorScheme.background)
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
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = CustomColorProvider.colorScheme.onBackground,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(1f),
        )
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = CustomColorProvider.colorScheme.brand,
                unselectedColor = CustomColorProvider.colorScheme.onSurfaceMuted,
            )
        )
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
private fun TierUpAnimationDialog(
    from: Tier,
    to: Tier,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        )
    ) {
        val promotedTierText = stringResource(id = to.getDisplayNameResId())
        val animation = when (from) {
            Tier.IRON -> R.raw.anim_iron_to_bronze
            Tier.BRONZE -> R.raw.anim_bronze_to_silver
            Tier.SILVER -> R.raw.anim_silver_to_gold
            Tier.GOLD -> R.raw.anim_gold_to_platinum
            Tier.PLATINUM -> R.raw.anim_platinum_to_diamond
            Tier.DIAMOND -> R.raw.anim_diamond_to_master
            Tier.MASTER -> R.raw.anim_master_to_legend
            else -> return@Dialog
        }

        var isAnimationEnd by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TierUpAnimatedText(
                visible = isAnimationEnd,
                text = stringResource(id = HomeStrings.feature_home_promoted),
                color = Color.White,
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            TierUpAnimatedText(
                visible = isAnimationEnd,
                text = stringResource(
                    id = HomeStrings.feature_home_promoted_description, promotedTierText
                ),
                color = Color.LightGray,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(32.dp))
            LottieImage(
                animationResId = animation,
                repeatCount = 1,
                modifier = Modifier.size(250.dp),
                onAnimationEnd = {
                    isAnimationEnd = true
                },
            )
            Spacer(modifier = Modifier.height(50.dp))
            TierUpAnimatedButton(
                visible = isAnimationEnd,
                onClick = onDismiss,
            )
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
private fun TierUpAnimatedText(
    visible: Boolean,
    text: String,
    color: Color,
    style: TextStyle,
    offsetY: Dp = (-40).dp,
) {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "AnimatedText Alpha Animation"
    )

    val offset by animateDpAsState(
        targetValue = if (visible) 0.dp else offsetY,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "AnimatedText Offset Animation"
    )

    Text(
        text = text,
        color = color.copy(alpha = alpha),
        style = style,
        textAlign = TextAlign.Center,
        modifier = Modifier.offset { IntOffset(0, offset.roundToPx()) }
    )
}

@Composable
private fun TierUpAnimatedButton(
    visible: Boolean,
    onClick: () -> Unit,
) {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "AnimatedButton Alpha Animation"
    )

    val offset by animateDpAsState(
        targetValue = if (visible) 0.dp else 40.dp,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "AnimatedButton Offset Animation"
    )

    Box(
        modifier = Modifier
            .offset { IntOffset(0, offset.roundToPx()) }
            .alpha(alpha)
    ) {
        ChallengeTogetherOutlinedButton(
            onClick = onClick,
            shape = MaterialTheme.shapes.extraLarge,
            borderColor = Color.LightGray,
            contentColor = Color.LightGray,
        ) {
            Text(
                text = stringResource(id = HomeStrings.feature_home_promoted_confirm),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
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
