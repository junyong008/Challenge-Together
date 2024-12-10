package com.yjy.feature.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.yjy.common.core.extensions.clickableSingle
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.core.util.toDisplayTimeFormat
import com.yjy.common.designsystem.component.BaseBottomSheet
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.ClickableText
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.extensions.formatMessage
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.common.ui.EmptyBody
import com.yjy.common.ui.ErrorBody
import com.yjy.common.ui.FooterState
import com.yjy.common.ui.LoadStateFooter
import com.yjy.common.ui.preview.NotificationPreviewParameterProvider
import com.yjy.feature.notification.model.NotificationUiAction
import com.yjy.feature.notification.model.NotificationUiEvent
import com.yjy.model.common.notification.Notification
import com.yjy.model.common.notification.NotificationCategory
import com.yjy.model.common.notification.NotificationDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun NotificationRoute(
    onBackClick: () -> Unit,
    onSettingClick: () -> Unit,
    onWaitingChallengeNotificationClick: (challengeId: Int) -> Unit,
    onStartedChallengeNotificationClick: (challengeId: Int) -> Unit,
    onCommunityPostNotificationClick: (postId: Int) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val notifications = viewModel.notifications.collectAsLazyPagingItems()

    NotificationScreen(
        modifier = modifier,
        notifications = notifications,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onSettingClick = onSettingClick,
        onWaitingChallengeNotificationClick = onWaitingChallengeNotificationClick,
        onStartedChallengeNotificationClick = onStartedChallengeNotificationClick,
        onCommunityPostNotificationClick = onCommunityPostNotificationClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun NotificationScreen(
    modifier: Modifier = Modifier,
    notifications: LazyPagingItems<Notification>,
    uiEvent: Flow<NotificationUiEvent> = flowOf(),
    processAction: (NotificationUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    onWaitingChallengeNotificationClick: (challengeId: Int) -> Unit = {},
    onStartedChallengeNotificationClick: (challengeId: Int) -> Unit = {},
    onCommunityPostNotificationClick: (postId: Int) -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val deleteSuccessMessage = stringResource(id = R.string.feature_notification_delete_success)
    val deleteFailureMessage = stringResource(id = R.string.feature_notification_delete_failed)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            NotificationUiEvent.DeleteSuccess -> onShowSnackbar(SnackbarType.SUCCESS, deleteSuccessMessage)
            NotificationUiEvent.DeleteFailed -> onShowSnackbar(SnackbarType.ERROR, deleteFailureMessage)
        }
    }

    var selectedNotification by remember { mutableStateOf<Notification?>(null) }
    var shouldShowNotificationMenu by remember { mutableStateOf(false) }

    if (selectedNotification != null) {
        ItemMenuBottomSheet(
            onDeleteClick = {
                processAction(NotificationUiAction.OnDeleteItemClick(selectedNotification!!.id))
                selectedNotification = null
            },
            onDismiss = { selectedNotification = null },
        )
    }

    if (shouldShowNotificationMenu) {
        NotificationMenuBottomSheet(
            onSettingClick = {
                shouldShowNotificationMenu = false
                onSettingClick()
            },
            onDeleteAllClick = {
                processAction(NotificationUiAction.OnDeleteAllClick)
                shouldShowNotificationMenu = false
            },
            onDismiss = { shouldShowNotificationMenu = false },
        )
    }

    val isLoading = notifications.loadState.refresh is LoadState.Loading
    val isError = notifications.loadState.refresh is LoadState.Error
    val isIdle = notifications.loadState.isIdle
    val isEmpty = notifications.itemCount == 0

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                onNavigationClick = onBackClick,
                titleRes = R.string.feature_notification_title,
                rightContent = {
                    MenuButton(
                        onClick = { shouldShowNotificationMenu = true },
                        modifier = Modifier.padding(end = 4.dp),
                        iconColor = CustomColorProvider.colorScheme.onBackground,
                    )
                },
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier.consumeWindowInsets(WindowInsets.navigationBars),
    ) { padding ->
        when {
            isLoading -> LoadingWheel()
            isError -> ErrorBody(onClickRetry = { notifications.refresh() })
            isIdle && isEmpty -> {
                EmptyBody(
                    title = stringResource(id = R.string.feature_notification_empty_title),
                    description = stringResource(id = R.string.feature_notification_empty_description),
                )
            }

            else -> {
                NotificationBody(
                    notifications = notifications,
                    onWaitingChallengeNotificationClick = onWaitingChallengeNotificationClick,
                    onStartedChallengeNotificationClick = onStartedChallengeNotificationClick,
                    onCommunityPostNotificationClick = onCommunityPostNotificationClick,
                    onMenuClick = { selectedNotification = it },
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

@Composable
private fun ItemMenuBottomSheet(
    onDeleteClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss = onDismiss) {
        Spacer(modifier = Modifier.height(16.dp))
        ClickableText(
            text = stringResource(id = R.string.feature_notification_delete),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.red,
            onClick = onDeleteClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun NotificationMenuBottomSheet(
    onSettingClick: () -> Unit,
    onDeleteAllClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss = onDismiss) {
        Spacer(modifier = Modifier.height(16.dp))
        ClickableText(
            text = stringResource(id = R.string.feature_notification_setting),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.onSurface,
            onClick = onSettingClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
        )
        ClickableText(
            text = stringResource(id = R.string.feature_notification_delete_all),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.red,
            onClick = onDeleteAllClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconColor: Color = CustomColorProvider.colorScheme.onBackgroundMuted,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            ImageVector.vectorResource(id = ChallengeTogetherIcons.MoreVertical),
            contentDescription = stringResource(id = R.string.feature_notification_more),
            tint = iconColor,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun NotificationBody(
    notifications: LazyPagingItems<Notification>,
    onWaitingChallengeNotificationClick: (challengeId: Int) -> Unit,
    onStartedChallengeNotificationClick: (challengeId: Int) -> Unit,
    onCommunityPostNotificationClick: (postId: Int) -> Unit,
    onMenuClick: (Notification) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = notifications.loadState.refresh is LoadState.Loading,
        onRefresh = { notifications.refresh() },
    )

    Box(modifier = modifier.pullRefresh(pullRefreshState)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                count = notifications.itemCount,
                key = notifications.itemKey { it.id },
            ) { index ->
                notifications[index]?.let {
                    NotificationItem(
                        notification = it,
                        onItemClick = {
                            when (it.destination) {
                                is NotificationDestination.StaredChallenge ->
                                    onStartedChallengeNotificationClick(it.linkId)

                                is NotificationDestination.WaitingChallenge ->
                                    onWaitingChallengeNotificationClick(it.linkId)

                                is NotificationDestination.CommunityPost ->
                                    onCommunityPostNotificationClick(it.linkId)

                                NotificationDestination.None -> Unit
                            }
                        },
                        onMenuClick = { onMenuClick(it) },
                        modifier = Modifier.animateItem(fadeOutSpec = null),
                    )
                }
            }
            item {
                LoadStateFooter(
                    state = when (notifications.loadState.append) {
                        is LoadState.Loading -> FooterState.Loading
                        is LoadState.Error -> FooterState.Error
                        is LoadState.NotLoading -> FooterState.Idle
                    },
                    onClickRetry = { notifications.retry() },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        PullRefreshIndicator(
            refreshing = notifications.loadState.refresh is LoadState.Loading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = CustomColorProvider.colorScheme.surface,
            contentColor = CustomColorProvider.colorScheme.onSurface,
        )
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    onItemClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.clickableSingle { onItemClick() }) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(CustomColorProvider.colorScheme.surface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        when (notification.category) {
                            NotificationCategory.CHALLENGE -> ChallengeTogetherIcons.NotificationChallenge
                            NotificationCategory.COMMUNITY -> ChallengeTogetherIcons.NotificationCommunity
                            NotificationCategory.COMMON -> ChallengeTogetherIcons.NotificationOn
                        },
                    ),
                    contentDescription = stringResource(id = R.string.feature_notification_challenge_category),
                    tint = CustomColorProvider.colorScheme.onSurfaceMuted,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp),
            ) {
                val header = notification.header
                val body = notification.body
                val (title, description) = notification.type.formatMessage(header, body)
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CustomColorProvider.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelSmall,
                    color = CustomColorProvider.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = notification.createdDateTime.toDisplayTimeFormat(),
                    style = MaterialTheme.typography.bodySmall,
                    color = CustomColorProvider.colorScheme.onBackgroundMuted,
                )
            }
            MenuButton(
                onClick = onMenuClick,
                modifier = Modifier.padding(4.dp),
            )
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = CustomColorProvider.colorScheme.divider,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@DevicePreviews
@Composable
fun NotificationScreenPreview(
    @PreviewParameter(NotificationPreviewParameterProvider::class)
    notifications: List<Notification>,
) {
    val pagingData = flowOf(
        PagingData.from(
            notifications,
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false),
                prepend = LoadState.NotLoading(false),
            ),
        ),
    )

    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            NotificationScreen(
                notifications = pagingData.collectAsLazyPagingItems(),
            )
        }
    }
}
