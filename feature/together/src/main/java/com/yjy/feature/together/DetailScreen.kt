package com.yjy.feature.together

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.component.SearchTextField
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.extensions.getDisplayNameResId
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.common.ui.EmptyBody
import com.yjy.common.ui.ErrorBody
import com.yjy.common.ui.FooterState
import com.yjy.common.ui.LoadStateFooter
import com.yjy.common.ui.WaitingChallengeCard
import com.yjy.common.ui.preview.WaitingChallengePreviewParameterProvider
import com.yjy.feature.together.model.TogetherUiEvent
import com.yjy.model.challenge.SimpleWaitingChallenge
import com.yjy.model.challenge.core.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun DetailRoute(
    category: Category,
    onBackClick: () -> Unit,
    onWaitingChallengeClick: (SimpleWaitingChallenge) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TogetherViewModel = hiltViewModel(),
) {
    val waitingChallenges = viewModel.waitingChallenges.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isGlobalActive by viewModel.isGlobalActive.collectAsStateWithLifecycle()

    DetailScreen(
        modifier = modifier,
        category = category,
        searchQuery = searchQuery,
        isGlobalActive = isGlobalActive,
        waitingChallenges = waitingChallenges,
        uiEvent = viewModel.uiEvent,
        updateSearchQuery = viewModel::updateSearchQuery,
        updateLanguageCode = viewModel::updateLanguageCode,
        toggleGlobalMode = viewModel::toggleGlobalMode,
        onBackClick = onBackClick,
        onWaitingChallengeClick = onWaitingChallengeClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun DetailScreen(
    modifier: Modifier = Modifier,
    category: Category = Category.ALL,
    searchQuery: String = "",
    isGlobalActive: Boolean = false,
    uiEvent: Flow<TogetherUiEvent> = flowOf(),
    updateSearchQuery: (String) -> Unit = {},
    updateLanguageCode: (String) -> Unit = {},
    toggleGlobalMode: () -> Unit = {},
    waitingChallenges: LazyPagingItems<SimpleWaitingChallenge>,
    onBackClick: () -> Unit = {},
    onWaitingChallengeClick: (SimpleWaitingChallenge) -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val locale = context.resources.configuration.locales[0]
    val languageCode = locale.language
    val displayLanguage = locale.displayLanguage
    var shouldShowSearchTextField by rememberSaveable { mutableStateOf(false) }

    val isLoading = waitingChallenges.loadState.refresh is LoadState.Loading
    val isError = waitingChallenges.loadState.refresh is LoadState.Error
    val isIdle = waitingChallenges.loadState.isIdle
    val isEmpty = waitingChallenges.itemCount == 0

    val globalOnMessage = stringResource(id = R.string.feature_together_global_challenges_on)
    val globalOffMessage = stringResource(id = R.string.feature_together_global_challenges_off, displayLanguage)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            TogetherUiEvent.GlobalOn -> onShowSnackbar(SnackbarType.MESSAGE, globalOnMessage)
            TogetherUiEvent.GlobalOff -> onShowSnackbar(SnackbarType.MESSAGE, globalOffMessage)
        }
    }

    LaunchedEffect(languageCode) {
        updateLanguageCode(languageCode)
    }

    Scaffold(
        topBar = {
            Column {
                ChallengeTogetherTopAppBar(
                    onNavigationClick = onBackClick,
                    titleRes = category.getDisplayNameResId(),
                    rightContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!isEmpty || searchQuery.isNotEmpty()) {
                                SearchButton(
                                    onClick = { shouldShowSearchTextField = !shouldShowSearchTextField },
                                    isSearchActive = shouldShowSearchTextField,
                                )
                            }
                            LanguageButton(
                                onClick = { toggleGlobalMode() },
                                isGlobalActive = isGlobalActive,
                                modifier = Modifier.padding(end = 4.dp),
                            )
                        }
                    },
                )
                SearchTextField(
                    triggeredText = searchQuery,
                    onSearchTriggered = updateSearchQuery,
                    isVisible = shouldShowSearchTextField,
                    placeholderText = stringResource(id = R.string.feature_together_search_placeholder),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                )
            }
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier.consumeWindowInsets(WindowInsets.navigationBars),
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .animateContentSize(),
        ) {
            when {
                isLoading -> LoadingWheel()
                isError -> ErrorBody(onClickRetry = { waitingChallenges.refresh() })
                isIdle && isEmpty && searchQuery.isEmpty() -> {
                    EmptyBody(
                        title = stringResource(id = R.string.feature_together_empty_title),
                        description = stringResource(id = R.string.feature_together_empty_description),
                    )
                }

                isEmpty -> {
                    EmptyBody(
                        title = stringResource(id = R.string.feature_together_search_empty_title),
                        description = stringResource(
                            id = R.string.feature_together_search_empty_description,
                        ),
                    )
                }

                else -> {
                    WaitingChallengesBody(
                        searchQuery = searchQuery,
                        waitingChallenges = waitingChallenges,
                        onChallengeClick = { onWaitingChallengeClick(it) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageButton(
    onClick: () -> Unit,
    isGlobalActive: Boolean,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            ImageVector.vectorResource(id = ChallengeTogetherIcons.Language),
            contentDescription = stringResource(id = R.string.feature_together_global),
            tint = if (isGlobalActive) {
                CustomColorProvider.colorScheme.onBackground
            } else {
                CustomColorProvider.colorScheme.onBackgroundMuted
            },
        )
    }
}

@Composable
private fun SearchButton(
    onClick: () -> Unit,
    isSearchActive: Boolean,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            ImageVector.vectorResource(
                id = if (isSearchActive) {
                    ChallengeTogetherIcons.SearchOff
                } else {
                    ChallengeTogetherIcons.Search
                },
            ),
            contentDescription = stringResource(id = R.string.feature_together_search_placeholder),
            tint = if (isSearchActive) {
                CustomColorProvider.colorScheme.onBackground
            } else {
                CustomColorProvider.colorScheme.onBackgroundMuted
            },
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun WaitingChallengesBody(
    searchQuery: String,
    waitingChallenges: LazyPagingItems<SimpleWaitingChallenge>,
    onChallengeClick: (SimpleWaitingChallenge) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = waitingChallenges.loadState.refresh is LoadState.Loading,
        onRefresh = { waitingChallenges.refresh() },
    )

    Box(modifier = modifier.pullRefresh(pullRefreshState)) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.fillMaxSize(),
        ) {
            items(
                count = waitingChallenges.itemCount,
                key = waitingChallenges.itemKey { it.id },
            ) { index ->
                val waitingChallenge = waitingChallenges[index] ?: return@items
                WaitingChallengeCard(
                    challenge = waitingChallenge,
                    onClick = onChallengeClick,
                    searchKeyword = searchQuery,
                )
            }
            item {
                LoadStateFooter(
                    state = when (waitingChallenges.loadState.append) {
                        is LoadState.Loading -> FooterState.Loading
                        is LoadState.Error -> FooterState.Error
                        is LoadState.NotLoading -> FooterState.Idle
                    },
                    onClickRetry = { waitingChallenges.retry() },
                )
            }
        }

        PullRefreshIndicator(
            refreshing = waitingChallenges.loadState.refresh is LoadState.Loading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = CustomColorProvider.colorScheme.surface,
            contentColor = CustomColorProvider.colorScheme.onSurface,
        )
    }
}

@DevicePreviews
@Composable
fun DetailScreenPreview(
    @PreviewParameter(WaitingChallengePreviewParameterProvider::class)
    challenges: List<SimpleWaitingChallenge>,
) {
    val pagingData = flowOf(
        PagingData.from(
            challenges,
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false),
                prepend = LoadState.NotLoading(false),
            ),
        ),
    )

    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            DetailScreen(
                modifier = Modifier.fillMaxSize(),
                waitingChallenges = pagingData.collectAsLazyPagingItems(),
                onWaitingChallengeClick = {},
            )
        }
    }
}
