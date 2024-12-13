package com.yjy.feature.community

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.core.util.toDisplayTimeFormat
import com.yjy.common.designsystem.component.BaseBottomSheet
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherDialog
import com.yjy.common.designsystem.component.ChallengeTogetherTextField
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.ClickableText
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.component.ReportDialog
import com.yjy.common.designsystem.component.SelectableText
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.extensions.getDisplayColor
import com.yjy.common.designsystem.extensions.getDisplayName
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.community.component.UserProfile
import com.yjy.feature.community.model.CommunityPostDetailUiState
import com.yjy.feature.community.model.CommunityPostUiAction
import com.yjy.feature.community.model.CommunityPostUiEvent
import com.yjy.feature.community.model.CommunityPostUiState
import com.yjy.feature.community.model.isLoading
import com.yjy.feature.community.model.postDetailOrNull
import com.yjy.model.common.Tier
import com.yjy.model.common.User
import com.yjy.model.common.UserStatus
import com.yjy.model.community.CommunityComment
import com.yjy.model.community.CommunityPost
import com.yjy.model.community.DetailedCommunityPost
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime

private const val KEYBOARD_CLOSE_DELAY = 200L

@Composable
internal fun PostRoute(
    onBackClick: () -> Unit,
    onEditPostClick: (postId: Int, content: String) -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CommunityPostViewModel = hiltViewModel(),
) {
    val isNotificationOn by viewModel.isNotificationOn.collectAsStateWithLifecycle()
    val postDetail by viewModel.postDetail.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PostScreen(
        modifier = modifier,
        isNotificationOn = isNotificationOn,
        postDetail = postDetail,
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onEditPostClick = onEditPostClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun PostScreen(
    modifier: Modifier = Modifier,
    isNotificationOn: Boolean = true,
    postDetail: CommunityPostDetailUiState = CommunityPostDetailUiState.Loading,
    uiState: CommunityPostUiState = CommunityPostUiState(),
    uiEvent: Flow<CommunityPostUiEvent> = flowOf(),
    processAction: (CommunityPostUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onEditPostClick: (postId: Int, content: String) -> Unit = { _, _ -> },
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var shouldShowPostMenu by remember { mutableStateOf(false) }
    var shouldShowPostReportDialog by remember { mutableStateOf(false) }
    var shouldShowPostDeleteConfirmDialog by remember { mutableStateOf(false) }
    var selectedCommentForMenu by remember { mutableStateOf<CommunityComment?>(null) }
    var selectedCommentForDelete by remember { mutableStateOf<CommunityComment?>(null) }
    var selectedCommentForReport by remember { mutableStateOf<CommunityComment?>(null) }
    var selectedCommentForReply by remember { mutableStateOf<CommunityComment?>(null) }

    val postNotFoundMessage = stringResource(R.string.feature_community_post_not_found)
    val checkNetworkMessage = stringResource(R.string.feature_community_check_network_connection)
    val unknownErrorMessage = stringResource(R.string.feature_community_unknown_error)
    val deleteSuccessMessage = stringResource(R.string.feature_community_post_deleted_successfully)
    val reportSuccessMessage = stringResource(R.string.feature_community_post_reported_successfully)
    val reportDuplicatedMessage = stringResource(R.string.feature_community_post_already_reported)
    val bookmarkSuccessMessage = stringResource(R.string.feature_community_post_bookmarked)
    val unBookmarkSuccessMessage = stringResource(R.string.feature_community_post_un_bookmarked)
    val notificationOnMessage = stringResource(R.string.feature_community_post_notification_enabled)
    val notificationOffMessage = stringResource(R.string.feature_community_post_notification_disabled)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            CommunityPostUiEvent.LoadFailure.NotFound -> {
                onShowSnackbar(SnackbarType.ERROR, postNotFoundMessage)
                onBackClick()
            }

            CommunityPostUiEvent.LoadFailure.Network -> {
                onShowSnackbar(SnackbarType.ERROR, checkNetworkMessage)
                onBackClick()
            }

            CommunityPostUiEvent.LoadFailure.Unknown -> {
                onShowSnackbar(SnackbarType.ERROR, unknownErrorMessage)
                onBackClick()
            }

            CommunityPostUiEvent.DeletePostSuccess -> {
                onShowSnackbar(SnackbarType.SUCCESS, deleteSuccessMessage)
                onBackClick()
            }

            CommunityPostUiEvent.SendCommentSuccess -> {
                if (selectedCommentForReply == null) {
                    delay(KEYBOARD_CLOSE_DELAY)
                    scrollState.animateScrollTo(
                        value = scrollState.maxValue,
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing,
                        ),
                    )
                } else {
                    selectedCommentForReply = null
                }
            }

            CommunityPostUiEvent.NotificationOn ->
                onShowSnackbar(SnackbarType.MESSAGE, notificationOnMessage)

            CommunityPostUiEvent.NotificationOff ->
                onShowSnackbar(SnackbarType.MESSAGE, notificationOffMessage)

            CommunityPostUiEvent.DeleteCommentSuccess ->
                onShowSnackbar(SnackbarType.SUCCESS, deleteSuccessMessage)

            CommunityPostUiEvent.ReportSuccess ->
                onShowSnackbar(SnackbarType.SUCCESS, reportSuccessMessage)

            CommunityPostUiEvent.ReportDuplicated ->
                onShowSnackbar(SnackbarType.ERROR, reportDuplicatedMessage)

            CommunityPostUiEvent.BookmarkSuccess ->
                onShowSnackbar(SnackbarType.MESSAGE, bookmarkSuccessMessage)

            CommunityPostUiEvent.UnBookmarkSuccess ->
                onShowSnackbar(SnackbarType.MESSAGE, unBookmarkSuccessMessage)

            CommunityPostUiEvent.UnknownFailure ->
                onShowSnackbar(SnackbarType.ERROR, unknownErrorMessage)
        }
    }

    BackHandler(enabled = selectedCommentForReply != null) {
        selectedCommentForReply = null
    }

    LaunchedEffect(selectedCommentForReply) {
        if (selectedCommentForReply != null) {
            focusManager.clearFocus()
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                onNavigationClick = onBackClick,
                titleRes = R.string.feature_community_post_title,
                shouldShowDivider = true,
                rightContent = {
                    val detail = postDetail.postDetailOrNull() ?: return@ChallengeTogetherTopAppBar
                    if (detail.post.isAuthor || detail.comments.any { it.isAuthor }) {
                        NotificationButton(
                            isNotificationOn = isNotificationOn,
                            onClick = {
                                processAction(
                                    CommunityPostUiAction.OnToggleNotificationClick(
                                        postId = detail.post.postId,
                                        isMuted = isNotificationOn.not(),
                                    ),
                                )
                            },
                        )
                    }
                },
            )
        },
        bottomBar = {
            InputCommentTextField(
                inputText = uiState.comment,
                onTextChange = { processAction(CommunityPostUiAction.OnCommentUpdated(it)) },
                onSendClick = { content ->
                    focusManager.clearFocus()
                    processAction(
                        CommunityPostUiAction.OnSendCommentClick(
                            postId = postDetail.postDetailOrNull()?.post?.postId ?: return@InputCommentTextField,
                            content = content,
                            replyTo = selectedCommentForReply?.commentId ?: 0,
                        ),
                    )
                },
                isSending = uiState.isAddingComments,
                modifier = Modifier.focusRequester(focusRequester),
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier,
    ) { padding ->

        if (postDetail.isLoading() || uiState.isDeletingPost) {
            LoadingWheel(modifier = Modifier.background(CustomColorProvider.colorScheme.background))
        } else {
            val postInfo = postDetail.postDetailOrNull() ?: return@Scaffold
            val post = postInfo.post
            val comments = postInfo.comments

            if (shouldShowPostMenu) {
                PostMenuBottomSheet(
                    isAuthor = post.isAuthor,
                    onRefreshClick = {
                        shouldShowPostMenu = false
                        processAction(CommunityPostUiAction.OnRefreshClick)
                    },
                    onReportClick = { shouldShowPostReportDialog = true },
                    onEditClick = {
                        shouldShowPostMenu = false
                        onEditPostClick(post.postId, post.content)
                    },
                    onDeleteAllClick = { shouldShowPostDeleteConfirmDialog = true },
                    onDismiss = { shouldShowPostMenu = false },
                )
            }

            if (shouldShowPostDeleteConfirmDialog) {
                ChallengeTogetherDialog(
                    title = stringResource(id = R.string.feature_community_post_delete),
                    description = stringResource(id = R.string.feature_community_post_delete_prompt),
                    positiveTextRes = R.string.feature_community_post_delete,
                    positiveTextColor = CustomColorProvider.colorScheme.red,
                    onClickPositive = {
                        shouldShowPostDeleteConfirmDialog = false
                        shouldShowPostMenu = false
                        processAction(CommunityPostUiAction.OnDeletePostClick(post.postId))
                    },
                    onClickNegative = { shouldShowPostDeleteConfirmDialog = false },
                )
            }

            if (shouldShowPostReportDialog) {
                ReportDialog(
                    onClickReport = { reason ->
                        shouldShowPostReportDialog = false
                        shouldShowPostMenu = false
                        processAction(
                            CommunityPostUiAction.OnReportPostClick(postId = post.postId, reason = reason),
                        )
                    },
                    onClickNegative = { shouldShowPostReportDialog = false },
                )
            }

            if (selectedCommentForMenu != null) {
                CommentMenuBottomSheet(
                    isAuthor = selectedCommentForMenu!!.isAuthor,
                    onReportClick = {
                        selectedCommentForReport = selectedCommentForMenu
                    },
                    onDeleteAllClick = {
                        selectedCommentForDelete = selectedCommentForMenu
                    },
                    onDismiss = { selectedCommentForMenu = null },
                )
            }

            if (selectedCommentForReport != null) {
                ReportDialog(
                    onClickReport = { reason ->
                        val commentIdToReport = selectedCommentForReport!!.commentId
                        selectedCommentForReport = null
                        selectedCommentForMenu = null
                        processAction(
                            CommunityPostUiAction.OnReportCommentClick(commentIdToReport, reason),
                        )
                    },
                    onClickNegative = { selectedCommentForReport = null },
                )
            }

            if (selectedCommentForDelete != null) {
                ChallengeTogetherDialog(
                    title = stringResource(id = R.string.feature_community_post_delete),
                    description = stringResource(id = R.string.feature_community_comment_delete_prompt),
                    positiveTextRes = R.string.feature_community_post_delete,
                    positiveTextColor = CustomColorProvider.colorScheme.red,
                    onClickPositive = {
                        val commentIdToDelete = selectedCommentForDelete!!.commentId
                        selectedCommentForDelete = null
                        selectedCommentForMenu = null
                        processAction(CommunityPostUiAction.OnDeleteCommentClick(commentIdToDelete))
                    },
                    onClickNegative = { selectedCommentForDelete = null },
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState),
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProfileSection(
                        user = post.writer,
                        writtenDateTime = post.writtenDateTime,
                        modifiedDateTime = post.modifiedDateTime,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f),
                    )
                    BookmarkButton(
                        isBookmarked = post.isBookmarked,
                        isBookmarking = uiState.isBookmarkingPost,
                        onClick = {
                            processAction(
                                CommunityPostUiAction.OnToggleBookmarkClick(post.postId, post.isBookmarked),
                            )
                        },
                    )
                    MenuButton(
                        onClick = { shouldShowPostMenu = true },
                        modifier = Modifier.padding(end = 4.dp),
                    )
                }
                SelectableText(
                    text = post.content,
                    color = CustomColorProvider.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .heightIn(min = 200.dp)
                        .padding(16.dp)
                        .padding(start = 4.dp),
                )
                Spacer(modifier = Modifier.height(16.dp))
                LikeButton(
                    isLiked = post.isLiked,
                    isLiking = uiState.isLikingPost,
                    likeCount = post.likeCount,
                    onClick = { processAction(CommunityPostUiAction.OnToggleLikeClick(post.postId)) },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(id = R.string.feature_community_post_comment_count, comments.size),
                    color = CustomColorProvider.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (comments.isEmpty()) {
                    Text(
                        text = stringResource(R.string.feature_community_post_empty_comments),
                        color = CustomColorProvider.colorScheme.onBackgroundMuted,
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(horizontal = 32.dp, vertical = 65.dp),
                    )
                } else {
                    CommentList(
                        comments = comments,
                        selectedCommentForReply = selectedCommentForReply,
                        onMenuClick = { selectedCommentForMenu = it },
                        onReplyClick = { selectedCommentForReply = it },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun NotificationButton(
    isNotificationOn: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.padding(end = 4.dp),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(
                id = if (isNotificationOn) {
                    ChallengeTogetherIcons.NotificationOn
                } else {
                    ChallengeTogetherIcons.NotificationOff
                },
            ),
            contentDescription = stringResource(
                id = R.string.feature_community_post_toggle_notification,
            ),
            tint = if (isNotificationOn) {
                CustomColorProvider.colorScheme.onBackground
            } else {
                CustomColorProvider.colorScheme.onBackgroundMuted
            },
        )
    }
}

@Composable
private fun CommentList(
    comments: List<CommunityComment>,
    selectedCommentForReply: CommunityComment?,
    onMenuClick: (CommunityComment) -> Unit,
    onReplyClick: (CommunityComment) -> Unit,
    modifier: Modifier = Modifier,
) {
    val commentsByParent = remember(comments) {
        comments.groupBy { it.parentCommentId }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        commentsByParent[0]?.forEach { rootComment ->
            CommentItem(
                comment = rootComment,
                isSelectedForReply = selectedCommentForReply == rootComment,
                replies = commentsByParent[rootComment.commentId] ?: emptyList(),
                onMenuClick = onMenuClick,
                onReplyClick = onReplyClick,
            )
        }
    }
}

@Composable
fun CommentItem(
    comment: CommunityComment,
    isSelectedForReply: Boolean,
    replies: List<CommunityComment>,
    onMenuClick: (CommunityComment) -> Unit,
    onReplyClick: (CommunityComment) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(
                if (isSelectedForReply) {
                    CustomColorProvider.colorScheme.brandBright.copy(alpha = 0.4f)
                } else {
                    CustomColorProvider.colorScheme.surface
                },
            ),
    ) {
        CommentContent(
            comment = comment,
            shouldShowReplyButton = replies.isEmpty(),
            onMenuClick = { onMenuClick(comment) },
            onReplyClick = { onReplyClick(comment) },
        )

        if (replies.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    ImageVector.vectorResource(id = ChallengeTogetherIcons.Reply),
                    contentDescription = stringResource(id = R.string.feature_community_post_reply),
                    tint = CustomColorProvider.colorScheme.onSurfaceMuted,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Column {
                    replies.forEachIndexed { index, reply ->
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = CustomColorProvider.colorScheme.divider,
                            modifier = Modifier.padding(end = 16.dp),
                        )
                        CommentContent(
                            comment = reply,
                            isReply = true,
                            shouldShowReplyButton = index == replies.lastIndex,
                            onMenuClick = { onMenuClick(reply) },
                            onReplyClick = { onReplyClick(comment) },
                        )
                    }
                }
            }
        }
    }
}

private const val LINE_FOR_COMMENT_EXPAND = 10

@Composable
fun CommentContent(
    comment: CommunityComment,
    onMenuClick: () -> Unit,
    onReplyClick: () -> Unit,
    modifier: Modifier = Modifier,
    shouldShowReplyButton: Boolean = true,
    isReply: Boolean = false,
) {
    var textLineCount by remember { mutableIntStateOf(0) }
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row {
            Row(
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(
                        start = if (isReply) 0.dp else 16.dp,
                        top = 16.dp,
                    )
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                UserProfile(
                    tier = comment.writer.tier,
                    backgroundColor = CustomColorProvider.colorScheme.background,
                    modifier = Modifier.size(32.dp),
                )
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = comment.writer.getDisplayName(),
                        color = comment.writer.getDisplayColor(),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    if (comment.isPostWriter) {
                        VerticalDivider(
                            thickness = 2.dp,
                            color = CustomColorProvider.colorScheme.divider,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(vertical = 1.dp, horizontal = 8.dp),
                        )
                        Text(
                            text = stringResource(id = R.string.feature_community_post_author),
                            color = CustomColorProvider.colorScheme.brandDim,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
            MenuButton(
                onClick = onMenuClick,
                modifier = Modifier.padding(top = 4.dp, end = 4.dp),
            )
        }
        SelectableText(
            text = if (comment.writer.status == UserStatus.DELETED) {
                stringResource(id = R.string.feature_community_post_comment_deleted)
            } else {
                comment.content
            },
            color = if (comment.writer.status == UserStatus.DELETED) {
                CustomColorProvider.colorScheme.onSurfaceMuted
            } else {
                CustomColorProvider.colorScheme.onSurface
            },
            style = MaterialTheme.typography.labelSmall,
            maxLines = if (isExpanded) Int.MAX_VALUE else LINE_FOR_COMMENT_EXPAND,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = if (isReply) 40.dp else 56.dp),
            onTextLayout = { textLayoutResult ->
                textLineCount = textLayoutResult.lineCount
            },
        )
        if (textLineCount >= LINE_FOR_COMMENT_EXPAND && !isExpanded) {
            ClickableText(
                text = stringResource(id = R.string.feature_community_post_more),
                style = MaterialTheme.typography.bodySmall,
                color = CustomColorProvider.colorScheme.onSurfaceMuted,
                modifier = Modifier.padding(start = if (isReply) 32.dp else 48.dp),
                onClick = { isExpanded = true },
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                start = if (isReply) 40.dp else 56.dp,
                end = 16.dp,
            ),
        ) {
            Text(
                text = comment.writtenDateTime.toDisplayTimeFormat(),
                color = CustomColorProvider.colorScheme.onSurfaceMuted,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .weight(1f, fill = false),
            )
            if (shouldShowReplyButton) {
                Text(
                    text = stringResource(id = R.string.feature_community_post_dot),
                    color = CustomColorProvider.colorScheme.onSurfaceMuted,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 8.dp),
                )
                ClickableText(
                    text = stringResource(id = R.string.feature_community_post_reply),
                    color = CustomColorProvider.colorScheme.onSurfaceMuted,
                    style = MaterialTheme.typography.bodySmall,
                    onClick = onReplyClick,
                )
            }
        }
    }
}

@Composable
private fun LikeButton(
    isLiked: Boolean,
    isLiking: Boolean,
    likeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dislikeColor = CustomColorProvider.colorScheme.onBackgroundMuted.copy(alpha = 0.5f)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = likeCount.toString(),
            color = if (isLiked) {
                CustomColorProvider.colorScheme.onSurface
            } else {
                CustomColorProvider.colorScheme.onBackground
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(
                    if (isLiked) {
                        CustomColorProvider.colorScheme.surface
                    } else {
                        CustomColorProvider.colorScheme.background
                    },
                )
                .border(
                    width = 1.dp,
                    color = if (isLiked) {
                        CustomColorProvider.colorScheme.surface
                    } else {
                        dislikeColor
                    },
                    shape = MaterialTheme.shapes.medium,
                )
                .padding(8.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(
                    if (isLiked) {
                        CustomColorProvider.colorScheme.brandDim
                    } else {
                        CustomColorProvider.colorScheme.background
                    },
                )
                .border(
                    width = 1.dp,
                    color = if (isLiked) {
                        CustomColorProvider.colorScheme.brandDim
                    } else {
                        dislikeColor
                    },
                    shape = MaterialTheme.shapes.medium,
                )
                .clickable(
                    enabled = !isLiking,
                    onClick = onClick,
                )
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.alpha(if (isLiking) 0f else 1f),
                ) {
                    Icon(
                        ImageVector.vectorResource(
                            id = if (isLiked) {
                                ChallengeTogetherIcons.LikeOn
                            } else {
                                ChallengeTogetherIcons.LikeOff
                            },
                        ),
                        contentDescription = stringResource(id = R.string.feature_community_post_cheer),
                        tint = if (isLiked) {
                            CustomColorProvider.colorScheme.red
                        } else {
                            dislikeColor
                        },
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = stringResource(id = R.string.feature_community_post_cheer),
                        color = if (isLiked) {
                            CustomColorProvider.colorScheme.onBrandDim
                        } else {
                            dislikeColor
                        },
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                if (isLiking) {
                    CircularProgressIndicator(
                        color = if (isLiked) {
                            CustomColorProvider.colorScheme.onBrandDim
                        } else {
                            CustomColorProvider.colorScheme.brandDim
                        },
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentMenuBottomSheet(
    isAuthor: Boolean,
    onReportClick: () -> Unit,
    onDeleteAllClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss = onDismiss) {
        Spacer(modifier = Modifier.height(16.dp))
        if (isAuthor) {
            ClickableText(
                text = stringResource(id = R.string.feature_community_post_delete),
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.None,
                style = MaterialTheme.typography.labelMedium,
                color = CustomColorProvider.colorScheme.red,
                onClick = onDeleteAllClick,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp),
            )
        } else {
            ClickableText(
                text = stringResource(id = R.string.feature_community_post_report),
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.None,
                style = MaterialTheme.typography.labelMedium,
                color = CustomColorProvider.colorScheme.onSurface,
                onClick = onReportClick,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PostMenuBottomSheet(
    isAuthor: Boolean,
    onRefreshClick: () -> Unit,
    onReportClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteAllClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss = onDismiss) {
        Spacer(modifier = Modifier.height(16.dp))
        ClickableText(
            text = stringResource(id = R.string.feature_community_post_refresh),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.onSurface,
            onClick = onRefreshClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
        )
        if (isAuthor) {
            ClickableText(
                text = stringResource(id = R.string.feature_community_post_edit),
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.None,
                style = MaterialTheme.typography.labelMedium,
                color = CustomColorProvider.colorScheme.onSurface,
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp),
            )
            ClickableText(
                text = stringResource(id = R.string.feature_community_post_delete),
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.None,
                style = MaterialTheme.typography.labelMedium,
                color = CustomColorProvider.colorScheme.red,
                onClick = onDeleteAllClick,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp),
            )
        } else {
            ClickableText(
                text = stringResource(id = R.string.feature_community_post_report),
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.None,
                style = MaterialTheme.typography.labelMedium,
                color = CustomColorProvider.colorScheme.onSurface,
                onClick = onReportClick,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            ImageVector.vectorResource(id = ChallengeTogetherIcons.MoreVertical),
            contentDescription = stringResource(id = R.string.feature_community_menu),
            tint = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
    }
}

@Composable
private fun BookmarkButton(
    isBookmarked: Boolean,
    isBookmarking: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isBookmarking) {
        CircularProgressIndicator(
            color = CustomColorProvider.colorScheme.brandDim,
            modifier = Modifier.size(20.dp),
        )
    } else {
        IconButton(
            onClick = onClick,
            modifier = modifier,
        ) {
            Icon(
                ImageVector.vectorResource(
                    id = if (isBookmarked) {
                        ChallengeTogetherIcons.BookmarkOn
                    } else {
                        ChallengeTogetherIcons.BookmarkOff
                    },
                ),
                contentDescription = stringResource(id = R.string.feature_community_post_bookmark),
                tint = CustomColorProvider.colorScheme.onBackgroundMuted,
            )
        }
    }
}

@Composable
private fun ProfileSection(
    user: User,
    writtenDateTime: LocalDateTime,
    modifiedDateTime: LocalDateTime,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UserProfile(
            tier = user.tier,
            modifier = Modifier.size(42.dp),
            innerPadding = 8.dp,
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = user.getDisplayName(),
                color = user.getDisplayColor(),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = if (modifiedDateTime > writtenDateTime) {
                    stringResource(
                        R.string.feature_community_post_edited,
                        modifiedDateTime.toDisplayTimeFormat(),
                    )
                } else {
                    writtenDateTime.toDisplayTimeFormat()
                },
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun InputCommentTextField(
    inputText: String,
    isSending: Boolean,
    onTextChange: (String) -> Unit,
    onSendClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column {
        HorizontalDivider(
            thickness = 1.dp,
            color = CustomColorProvider.colorScheme.divider,
        )
        ChallengeTogetherTextField(
            value = inputText,
            onValueChange = onTextChange,
            placeholderText = stringResource(id = R.string.feature_community_post_comment_placeholder),
            trailingIcon = {
                if (isSending) {
                    CircularProgressIndicator(
                        color = CustomColorProvider.colorScheme.brandDim,
                        modifier = Modifier.size(20.dp),
                    )
                } else {
                    IconButton(onClick = { onSendClick(inputText) }) {
                        Icon(
                            ImageVector.vectorResource(id = ChallengeTogetherIcons.Send),
                            contentDescription = stringResource(id = R.string.feature_community_post_add_comment),
                            tint = CustomColorProvider.colorScheme.onSurface,
                        )
                    }
                }
            },
            shape = RectangleShape,
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp, max = 200.dp),
        )
    }
}

@DevicePreviews
@Composable
fun PostScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            PostScreen(
                modifier = Modifier.fillMaxSize(),
                postDetail = CommunityPostDetailUiState.Success(
                    DetailedCommunityPost(
                        post = CommunityPost(
                            postId = 1,
                            writer = User(
                                name = "작성자",
                                tier = Tier.BRONZE,
                            ),
                            content = "게시글 내용",
                            commentCount = 1,
                            likeCount = 5,
                            writtenDateTime = LocalDateTime.now(),
                            modifiedDateTime = LocalDateTime.now(),
                            isAuthor = false,
                            isLiked = false,
                            isBookmarked = false,
                        ),
                        comments = listOf(
                            CommunityComment(
                                commentId = 1,
                                parentCommentId = 0,
                                writer = User(
                                    name = "댓글 작성자",
                                    tier = Tier.BRONZE,
                                ),
                                content = "댓글 내용",
                                writtenDateTime = LocalDateTime.now(),
                                isAuthor = false,
                                isPostWriter = false,
                            ),
                        ),
                    ),
                ),
            )
        }
    }
}
