package com.yjy.feature.community.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.FooterState
import com.yjy.common.ui.LoadStateFooter
import com.yjy.model.community.Banner
import com.yjy.model.community.SimpleCommunityPost

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun PostsBody(
    posts: LazyPagingItems<SimpleCommunityPost>,
    onPostClick: (SimpleCommunityPost) -> Unit,
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    banner: Banner? = null,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = posts.loadState.refresh is LoadState.Loading,
        onRefresh = { posts.refresh() },
    )

    Box(modifier = modifier.pullRefresh(pullRefreshState)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            banner?.let { item { Banner(it) } }
            items(
                count = posts.itemCount,
                key = posts.itemKey { it.postId },
            ) { index ->
                val post = posts[index] ?: return@items
                PostItem(
                    post = post,
                    onClick = { onPostClick(post) },
                    searchKeyword = searchQuery,
                    modifier = Modifier.animateItem(fadeOutSpec = null),
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = CustomColorProvider.colorScheme.divider,
                    modifier = Modifier.animateItem(fadeOutSpec = null),
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

@Composable
private fun Banner(banner: Banner) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val aspectRatio = banner.imageWidth.toFloat() / banner.imageHeight.toFloat()

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(banner.imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Banner",
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .background(CustomColorProvider.colorScheme.surface)
            .clickable { uriHandler.openUri(banner.clickUrl) },
        contentScale = ContentScale.FillWidth,
    )
}
