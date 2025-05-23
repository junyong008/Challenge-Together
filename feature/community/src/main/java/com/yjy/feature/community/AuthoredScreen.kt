package com.yjy.feature.community

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.common.ui.EmptyBody
import com.yjy.common.ui.ErrorBody
import com.yjy.common.ui.preview.CommunityPostPreviewParameterProvider
import com.yjy.feature.community.component.PostsBody
import com.yjy.model.community.SimpleCommunityPost
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun AuthoredRoute(
    onBackClick: () -> Unit,
    onPostClick: (postId: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CommunityViewModel = hiltViewModel(),
) {
    val posts = viewModel.authoredPosts.collectAsLazyPagingItems()

    AuthoredScreen(
        modifier = modifier,
        posts = posts,
        onPostClick = onPostClick,
        onBackClick = onBackClick,
    )
}

@Composable
internal fun AuthoredScreen(
    modifier: Modifier = Modifier,
    posts: LazyPagingItems<SimpleCommunityPost>,
    onPostClick: (postId: Int) -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    val isLoading = posts.loadState.refresh is LoadState.Loading
    val isError = posts.loadState.refresh is LoadState.Error
    val isIdle = posts.loadState.isIdle
    val isEmpty = posts.itemCount == 0

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                onNavigationClick = onBackClick,
                titleRes = R.string.feature_community_my_posts,
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier.consumeWindowInsets(WindowInsets.navigationBars),
    ) { padding ->
        when {
            isLoading -> LoadingWheel()
            isError -> ErrorBody(onClickRetry = { posts.refresh() })
            isIdle && isEmpty -> {
                EmptyBody(
                    title = stringResource(id = R.string.feature_community_no_posts_title),
                    description = stringResource(
                        id = R.string.feature_community_no_my_posts_description,
                    ),
                )
            }

            else -> {
                PostsBody(
                    posts = posts,
                    onPostClick = { onPostClick(it.postId) },
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

@DevicePreviews
@Composable
fun AuthoredScreenPreview(
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
            AuthoredScreen(
                modifier = Modifier.fillMaxSize(),
                posts = pagingData.collectAsLazyPagingItems(),
                onPostClick = {},
            )
        }
    }
}
