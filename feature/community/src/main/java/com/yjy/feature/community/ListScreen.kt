package com.yjy.feature.community

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.BaseBottomSheet
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ClickableText
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.component.SearchTextField
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.common.ui.EmptyBody
import com.yjy.common.ui.ErrorBody
import com.yjy.common.ui.preview.CommunityPostPreviewParameterProvider
import com.yjy.feature.community.component.PostsBody
import com.yjy.feature.community.model.CommunityUiEvent
import com.yjy.model.community.SimpleCommunityPost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun ListRoute(
    onPostClick: (postId: Int) -> Unit,
    onBookmarkedClick: () -> Unit,
    onAuthoredClick: () -> Unit,
    onCommentedClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CommunityViewModel = hiltViewModel(),
) {
    val posts = viewModel.allPosts.collectAsLazyPagingItems()
    val isGlobalActive by viewModel.isGlobalActive.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    ListScreen(
        modifier = modifier,
        searchQuery = searchQuery,
        isGlobalActive = isGlobalActive,
        posts = posts,
        uiEvent = viewModel.uiEvent,
        updateSearchQuery = viewModel::updateSearchQuery,
        updateLanguageCode = viewModel::updateLanguageCode,
        toggleGlobalMode = viewModel::toggleGlobalMode,
        onPostClick = onPostClick,
        onBookmarkedClick = onBookmarkedClick,
        onAuthoredClick = onAuthoredClick,
        onCommentedClick = onCommentedClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun ListScreen(
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    isGlobalActive: Boolean = false,
    posts: LazyPagingItems<SimpleCommunityPost>,
    uiEvent: Flow<CommunityUiEvent> = flowOf(),
    updateSearchQuery: (String) -> Unit = {},
    updateLanguageCode: (String) -> Unit = {},
    toggleGlobalMode: () -> Unit = {},
    onPostClick: (postId: Int) -> Unit = {},
    onBookmarkedClick: () -> Unit = {},
    onAuthoredClick: () -> Unit = {},
    onCommentedClick: () -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val locale = context.resources.configuration.locales[0]
    val languageCode = locale.language
    val displayLanguage = locale.displayLanguage

    var shouldShowSearchTextField by rememberSaveable { mutableStateOf(false) }
    var shouldShowCommunityMenu by remember { mutableStateOf(false) }

    val isLoading = posts.loadState.refresh is LoadState.Loading
    val isError = posts.loadState.refresh is LoadState.Error
    val isIdle = posts.loadState.isIdle
    val isEmpty = posts.itemCount == 0

    if (shouldShowCommunityMenu) {
        CommunityMenuBottomSheet(
            onBookmarkedClick = {
                shouldShowCommunityMenu = false
                onBookmarkedClick()
            },
            onAuthoredClick = {
                shouldShowCommunityMenu = false
                onAuthoredClick()
            },
            onCommentedClick = {
                shouldShowCommunityMenu = false
                onCommentedClick()
            },
            onDismiss = { shouldShowCommunityMenu = false },
        )
    }

    val globalOnMessage = stringResource(id = R.string.feature_community_global_posts_on)
    val globalOffMessage = stringResource(id = R.string.feature_community_global_posts_off, displayLanguage)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            CommunityUiEvent.GlobalOn -> onShowSnackbar(SnackbarType.MESSAGE, globalOnMessage)
            CommunityUiEvent.GlobalOff -> onShowSnackbar(SnackbarType.MESSAGE, globalOffMessage)
            else -> Unit
        }
    }

    LaunchedEffect(languageCode) {
        updateLanguageCode(languageCode)
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.padding(start = 32.dp, top = 32.dp, bottom = 32.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TitleWithDescription(
                titleRes = R.string.feature_community_title,
                descriptionRes = R.string.feature_community_description,
                modifier = Modifier.weight(1f),
            )
            SearchButton(
                onClick = { shouldShowSearchTextField = !shouldShowSearchTextField },
                isSearchActive = shouldShowSearchTextField,
            )
            LanguageButton(
                onClick = { toggleGlobalMode() },
                isGlobalActive = isGlobalActive,
            )
            MenuButton(onClick = { shouldShowCommunityMenu = true })
        }
        SearchTextField(
            triggeredText = searchQuery,
            onSearchTriggered = updateSearchQuery,
            isVisible = shouldShowSearchTextField,
            placeholderText = stringResource(id = R.string.feature_community_search),
            modifier = Modifier.padding(bottom = 20.dp, start = 20.dp, end = 20.dp),
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = CustomColorProvider.colorScheme.divider,
        )
        when {
            isLoading -> LoadingWheel()
            isError -> ErrorBody(onClickRetry = { posts.refresh() })
            isIdle && isEmpty && searchQuery.isEmpty() -> {
                EmptyBody(
                    title = stringResource(id = R.string.feature_community_empty_title),
                    description = stringResource(id = R.string.feature_community_empty_description, displayLanguage),
                )
            }

            isEmpty -> {
                EmptyBody(
                    title = stringResource(id = R.string.feature_community_search_empty_title),
                    description = stringResource(id = R.string.feature_community_search_empty_description),
                )
            }

            else -> {
                PostsBody(
                    posts = posts,
                    onPostClick = { onPostClick(it.postId) },
                    searchQuery = searchQuery,
                )
            }
        }
    }
}

@Composable
private fun CommunityMenuBottomSheet(
    onBookmarkedClick: () -> Unit,
    onAuthoredClick: () -> Unit,
    onCommentedClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss = onDismiss) {
        Spacer(modifier = Modifier.height(16.dp))
        ClickableText(
            text = stringResource(id = R.string.feature_community_bookmarks),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.onSurface,
            onClick = onBookmarkedClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
        )
        ClickableText(
            text = stringResource(id = R.string.feature_community_my_posts),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.onSurface,
            onClick = onAuthoredClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
        )
        ClickableText(
            text = stringResource(id = R.string.feature_community_commented_posts),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.onSurface,
            onClick = onCommentedClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MenuButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            ImageVector.vectorResource(id = ChallengeTogetherIcons.MoreVertical),
            contentDescription = stringResource(id = R.string.feature_community_menu),
            tint = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
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
            contentDescription = stringResource(id = R.string.feature_community_global),
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
            contentDescription = stringResource(id = R.string.feature_community_search),
            tint = if (isSearchActive) {
                CustomColorProvider.colorScheme.onBackground
            } else {
                CustomColorProvider.colorScheme.onBackgroundMuted
            },
        )
    }
}

@DevicePreviews
@Composable
fun ListScreenPreview(
    @PreviewParameter(CommunityPostPreviewParameterProvider::class)
    posts: List<SimpleCommunityPost>,
) {
    val pagingData = flowOf(
        PagingData.from(
            posts,
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false),
                prepend = LoadState.NotLoading(false),
            ),
        ),
    )

    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            ListScreen(
                modifier = Modifier.fillMaxSize(),
                posts = pagingData.collectAsLazyPagingItems(),
                onPostClick = {},
            )
        }
    }
}
