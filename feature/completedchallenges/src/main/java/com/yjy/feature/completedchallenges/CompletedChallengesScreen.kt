package com.yjy.feature.completedchallenges

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.SearchTextField
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.common.ui.EmptyBody
import com.yjy.common.ui.StartedChallengeCard
import com.yjy.common.ui.preview.CompletedChallengePreviewParameterProvider
import com.yjy.model.challenge.SimpleStartedChallenge

@Composable
internal fun CompletedChallengesRoute(
    onBackClick: () -> Unit,
    onCompletedChallengeClick: (SimpleStartedChallenge) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CompletedChallengesViewModel = hiltViewModel(),
) {
    val completedChallenges by viewModel.completedChallenges.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    CompletedChallengesScreen(
        modifier = modifier,
        completedChallenges = completedChallenges,
        searchQuery = searchQuery,
        updateSearchQuery = viewModel::updateSearchQuery,
        onBackClick = onBackClick,
        onCompletedChallengeClick = onCompletedChallengeClick,
    )
}

@Composable
internal fun CompletedChallengesScreen(
    modifier: Modifier = Modifier,
    completedChallenges: List<SimpleStartedChallenge> = emptyList(),
    searchQuery: String = "",
    updateSearchQuery: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onCompletedChallengeClick: (SimpleStartedChallenge) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    val scrolled by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0 ||
                    lazyListState.firstVisibleItemScrollOffset > 0
        }
    }

    var shouldShowSearchTextField by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                ChallengeTogetherTopAppBar(
                    onNavigationClick = onBackClick,
                    titleRes = R.string.feature_completechallenges_title,
                    rightContent = {
                        if (completedChallenges.isNotEmpty() || searchQuery.isNotEmpty()) {
                            SearchButton(
                                onClick = { shouldShowSearchTextField = !shouldShowSearchTextField },
                                isSearchActive = shouldShowSearchTextField,
                                modifier = Modifier.padding(end = 4.dp),
                            )
                        }
                    },
                )
                SearchTextField(
                    triggeredText = searchQuery,
                    onSearchTriggered = updateSearchQuery,
                    isVisible = shouldShowSearchTextField,
                    placeholderText = stringResource(id = R.string.feature_completechallenges_search),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                )
                if (scrolled) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = CustomColorProvider.colorScheme.divider,
                    )
                }
            }
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier.consumeWindowInsets(WindowInsets.navigationBars),
    ) { padding ->
        if (completedChallenges.isEmpty() && searchQuery.isEmpty()) {
            EmptyBody(
                title = stringResource(id = R.string.feature_completechallenges_empty_title),
                description = stringResource(id = R.string.feature_completechallenges_empty_description),
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
                    .animateContentSize(),
            ) {
                if (completedChallenges.isEmpty()) {
                    EmptyBody(
                        title = stringResource(id = R.string.feature_completechallenges_search_empty_title),
                        description = stringResource(
                            id = R.string.feature_completechallenges_search_empty_description,
                        ),
                    )
                } else {
                    CompletedChallenges(
                        scrollState = lazyListState,
                        searchQuery = searchQuery,
                        completedChallenges = completedChallenges,
                        onChallengeClick = onCompletedChallengeClick,
                    )
                }
            }
        }
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
            contentDescription = stringResource(id = R.string.feature_completechallenges_search),
            tint = CustomColorProvider.colorScheme.onBackground,
        )
    }
}

@Composable
private fun CompletedChallenges(
    scrollState: LazyListState,
    searchQuery: String,
    completedChallenges: List<SimpleStartedChallenge>,
    onChallengeClick: (SimpleStartedChallenge) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        items(
            items = completedChallenges,
            key = { it.id },
        ) { challenge ->
            StartedChallengeCard(
                challenge = challenge,
                onClick = onChallengeClick,
                searchKeyword = searchQuery,
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@DevicePreviews
@Composable
fun CompletedChallengesScreenPreview(
    @PreviewParameter(CompletedChallengePreviewParameterProvider::class)
    challenges: List<SimpleStartedChallenge>,
) {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            CompletedChallengesScreen(
                modifier = Modifier.fillMaxSize(),
                completedChallenges = challenges,
            )
        }
    }
}
