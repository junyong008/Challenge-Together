package com.yjy.feature.community.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.FooterState
import com.yjy.common.ui.LoadStateFooter
import com.yjy.model.community.SimpleCommunityPost

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun PostsBody(
    posts: LazyPagingItems<SimpleCommunityPost>,
    onPostClick: (SimpleCommunityPost) -> Unit,
    modifier: Modifier = Modifier,
    searchQuery: String = "",
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = posts.loadState.refresh is LoadState.Loading,
        onRefresh = { posts.refresh() },
    )

    Box(modifier = modifier.pullRefresh(pullRefreshState)) {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(
                count = posts.itemCount,
                key = posts.itemKey { it.postId },
            ) { index ->
                val post = posts[index] ?: return@items
                PostItem(
                    post = post,
                    onClick = { onPostClick(post) },
                    searchKeyword = searchQuery,
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = CustomColorProvider.colorScheme.divider,
                )
            }
            item {
                LoadStateFooter(
                    state = when (posts.loadState.append) {
                        is LoadState.Loading -> FooterState.Loading
                        is LoadState.Error -> FooterState.Error
                        is LoadState.NotLoading -> FooterState.Idle
                    },
                    onClickRetry = { posts.retry() },
                )
            }
        }

        PullRefreshIndicator(
            refreshing = posts.loadState.refresh is LoadState.Loading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = CustomColorProvider.colorScheme.surface,
            contentColor = CustomColorProvider.colorScheme.onSurface,
        )
    }
}
