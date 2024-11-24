package com.yjy.feature.challengeboard

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
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
import com.yjy.common.core.constants.ChallengeConst.MAX_CHALLENGE_POST_LENGTH
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.BaseBottomSheet
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherTextField
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.CircleMedal
import com.yjy.common.designsystem.component.ClickableText
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.component.ReportDialog
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.extensions.getDisplayColor
import com.yjy.common.designsystem.extensions.getDisplayName
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.common.ui.EmptyBody
import com.yjy.common.ui.ErrorBody
import com.yjy.common.ui.ErrorItem
import com.yjy.common.ui.preview.ChallengePostPreviewParameterProvider
import com.yjy.feature.challengeboard.model.ChallengeBoardUiAction
import com.yjy.feature.challengeboard.model.ChallengeBoardUiEvent
import com.yjy.feature.challengeboard.model.PostsUpdateState
import com.yjy.feature.challengeboard.model.isError
import com.yjy.feature.challengeboard.model.isLoading
import com.yjy.model.challenge.ChallengePost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

@Composable
internal fun ChallengeBoardRoute(
    isAlone: Boolean,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChallengeBoardViewModel = hiltViewModel(),
) {
    val posts = viewModel.posts.collectAsLazyPagingItems()
    val latestPost by viewModel.latestPost.collectAsStateWithLifecycle()
    val postsUpdateState by viewModel.postsUpdateState.collectAsStateWithLifecycle()
    val isNotificationOn by viewModel.isNotificationOn.collectAsStateWithLifecycle()

    ChallengeBoardScreen(
        modifier = modifier,
        isAlone = isAlone,
        posts = posts,
        latestPost = latestPost,
        postsUpdateState = postsUpdateState,
        isNotificationOn = isNotificationOn,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onBackClick = onBackClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun ChallengeBoardScreen(
    modifier: Modifier = Modifier,
    isAlone: Boolean = false,
    posts: LazyPagingItems<ChallengePost>,
    latestPost: ChallengePost? = null,
    postsUpdateState: PostsUpdateState = PostsUpdateState.Connected,
    isNotificationOn: Boolean = true,
    uiEvent: Flow<ChallengeBoardUiEvent> = flowOf(),
    processAction: (ChallengeBoardUiAction) -> Unit = {},
    onBackClick: () -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val deleteSuccessMessage = stringResource(id = R.string.feature_challengeboard_delete_success)
    val deleteFailureMessage = stringResource(id = R.string.feature_challengeboard_delete_failed)
    val reportSuccessMessage = stringResource(id = R.string.feature_challengeboard_report_success)
    val reportFailureMessage = stringResource(id = R.string.feature_challengeboard_report_failed)
    val reportDuplicateMessage = stringResource(id = R.string.feature_challengeboard_report_already_reported)
    val notificationOnMessage = stringResource(id = R.string.feature_challengeboard_notification_enabled)
    val notificationOffMessage = stringResource(id = R.string.feature_challengeboard_notification_disabled)

    ObserveAsEvents(flow = uiEvent) { event ->
        when (event) {
            ChallengeBoardUiEvent.DeleteSuccess ->
                onShowSnackbar(SnackbarType.SUCCESS, deleteSuccessMessage)

            ChallengeBoardUiEvent.DeleteFailure ->
                onShowSnackbar(SnackbarType.ERROR, deleteFailureMessage)

            ChallengeBoardUiEvent.ReportSuccess ->
                onShowSnackbar(SnackbarType.SUCCESS, reportSuccessMessage)

            ChallengeBoardUiEvent.ReportFailure ->
                onShowSnackbar(SnackbarType.ERROR, reportFailureMessage)

            ChallengeBoardUiEvent.ReportDuplicated ->
                onShowSnackbar(SnackbarType.ERROR, reportDuplicateMessage)

            ChallengeBoardUiEvent.NotificationOn ->
                onShowSnackbar(SnackbarType.MESSAGE, notificationOnMessage)

            ChallengeBoardUiEvent.NotificationOff ->
                onShowSnackbar(SnackbarType.MESSAGE, notificationOffMessage)
        }
    }

    var inputText by rememberSaveable { mutableStateOf("") }
    var selectedPost by remember { mutableStateOf<ChallengePost?>(null) }
    var shouldShowReportDialog by rememberSaveable { mutableStateOf(false) }
    var shouldShowScrollToBottom by rememberSaveable { mutableStateOf(false) }
    var newPostContent by rememberSaveable { mutableStateOf("") }
    var previousFirstContent by rememberSaveable { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    val isNearBottom by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex <= NEAR_BOTTOM_THRESHOLD_ITEMS
        }
    }

    LaunchedEffect(isNearBottom) {
        shouldShowScrollToBottom = !isNearBottom
        if (isNearBottom) newPostContent = ""
    }

    LaunchedEffect(posts.itemCount) {
        val latestContent = latestPost?.content ?: return@LaunchedEffect
        if (previousFirstContent == latestContent) return@LaunchedEffect
        previousFirstContent = latestContent

        if (isNearBottom) {
            listState.animateScrollToItem(0)
        } else {
            newPostContent = latestPost.content
        }
    }

    if (shouldShowReportDialog && selectedPost != null) {
        ReportDialog(
            onClickReport = { reason ->
                processAction(
                    ChallengeBoardUiAction.OnReportPostClick(
                        postId = selectedPost!!.postId,
                        reason = reason,
                    ),
                )
                shouldShowReportDialog = false
                selectedPost = null
            },
            onClickNegative = { shouldShowReportDialog = false },
        )
    }

    if (selectedPost != null) {
        PostMenuBottomSheet(
            isAlone = isAlone,
            isAuthor = selectedPost!!.isAuthor,
            onCopyClick = {
                clipboardManager.setText(AnnotatedString(selectedPost!!.content))
                selectedPost = null
            },
            onReportClick = {
                shouldShowReportDialog = true
            },
            onDeleteClick = {
                processAction(ChallengeBoardUiAction.OnDeletePostClick(selectedPost!!.postId))
                selectedPost = null
            },
            onDismiss = { selectedPost = null },
        )
    }

    val isLoading = posts.loadState.refresh is LoadState.Loading || postsUpdateState.isLoading()
    val isError = posts.loadState.refresh is LoadState.Error || postsUpdateState.isError()
    val isIdle = posts.loadState.isIdle
    val isEmpty = posts.itemCount == 0

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                onNavigationClick = onBackClick,
                titleRes = R.string.feature_challengeboard_title,
                rightContent = {
                    if (!isAlone) {
                        NotificationButton(
                            isNotificationOn = isNotificationOn,
                            onClick = { processAction(ChallengeBoardUiAction.ToggleNotification) },
                        )
                    }
                },
            )
        },
        bottomBar = {
            InputContents(
                inputText = inputText,
                onValueChange = { newText ->
                    inputText = newText.take(MAX_CHALLENGE_POST_LENGTH)
                },
                onSendClick = {
                    if (inputText.isEmpty()) return@InputContents
                    processAction(ChallengeBoardUiAction.OnSendClick(it))
                    inputText = ""

                    if (!isNearBottom) {
                        coroutineScope.launch {
                            listState.scrollToItem(0)
                        }
                    }
                },
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier,
    ) { padding ->
        when {
            isLoading -> LoadingWheel()
            isError -> {
                ErrorBody(
                    onClickRetry = {
                        if (posts.loadState.refresh is LoadState.Error) posts.refresh()
                        if (postsUpdateState.isError()) processAction(ChallengeBoardUiAction.OnRetryClick)
                    },
                )
            }

            isIdle && isEmpty -> {
                EmptyBody(
                    title = stringResource(id = R.string.feature_challengeboard_no_posts_title),
                    description = stringResource(id = R.string.feature_challengeboard_no_posts_description),
                )
            }

            else -> {
                Box(modifier = Modifier.padding(padding)) {
                    PostsBody(
                        posts = posts,
                        listState = listState,
                        onPostLongPress = { selectedPost = it },
                    )
                    ScrollToBottomButton(
                        visible = shouldShowScrollToBottom,
                        onClick = {
                            coroutineScope.launch {
                                listState.scrollToItem(0)
                                shouldShowScrollToBottom = false
                                newPostContent = ""
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 8.dp, end = 8.dp),
                    )
                    NewPostPopup(
                        content = newPostContent,
                        onClick = {
                            coroutineScope.launch {
                                listState.scrollToItem(0)
                                shouldShowScrollToBottom = false
                                newPostContent = ""
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PostMenuBottomSheet(
    isAlone: Boolean,
    isAuthor: Boolean,
    onCopyClick: () -> Unit,
    onReportClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseBottomSheet(onDismiss = onDismiss) {
        Spacer(modifier = Modifier.height(16.dp))
        ClickableText(
            text = stringResource(id = R.string.feature_challengeboard_menu_copy),
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.None,
            style = MaterialTheme.typography.labelMedium,
            color = CustomColorProvider.colorScheme.onSurface,
            onClick = onCopyClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
        )
        if (!isAuthor) {
            ClickableText(
                text = stringResource(id = R.string.feature_challengeboard_menu_report),
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.None,
                style = MaterialTheme.typography.labelMedium,
                color = CustomColorProvider.colorScheme.onSurface,
                onClick = onReportClick,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp),
            )
        }
        if (isAlone) {
            ClickableText(
                text = stringResource(id = R.string.feature_challengeboard_menu_delete),
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.None,
                style = MaterialTheme.typography.labelMedium,
                color = CustomColorProvider.colorScheme.red,
                onClick = onDeleteClick,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
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
                id = R.string.feature_challengeboard_toggle_notification,
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
private fun InputContents(
    inputText: String,
    onValueChange: (String) -> Unit,
    onSendClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ChallengeTogetherTextField(
        value = inputText,
        onValueChange = onValueChange,
        placeholderText = stringResource(id = R.string.feature_challengeboard_content_placeholder),
        trailingIcon = {
            IconButton(onClick = { onSendClick(inputText) }) {
                Icon(
                    ImageVector.vectorResource(id = ChallengeTogetherIcons.Send),
                    contentDescription = stringResource(id = R.string.feature_challengeboard_send),
                    tint = CustomColorProvider.colorScheme.onSurface,
                )
            }
        },
        shape = RectangleShape,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp, max = 200.dp),
    )
}

@Composable
private fun ScrollToBottomButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (visible) {
        FloatingActionButton(
            onClick = onClick,
            shape = CircleShape,
            modifier = modifier.size(45.dp),
            containerColor = CustomColorProvider.colorScheme.surface,
            contentColor = CustomColorProvider.colorScheme.onSurface,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = ChallengeTogetherIcons.ArrowDown),
                contentDescription = stringResource(
                    id = R.string.feature_challengeboard_scroll_to_bottom,
                ),
            )
        }
    }
}

@Composable
private fun NewPostPopup(
    content: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = content.isNotEmpty(),
        modifier = modifier.padding(horizontal = 32.dp),
        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
    ) {
        Surface(
            onClick = onClick,
            shape = MaterialTheme.shapes.extraLarge,
            color = CustomColorProvider.colorScheme.surface.copy(alpha = 0.8f),
            contentColor = CustomColorProvider.colorScheme.onSurface.copy(alpha = 0.8f),
            tonalElevation = 2.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = ChallengeTogetherIcons.ArrowDown),
                    contentDescription = stringResource(
                        id = R.string.feature_challengeboard_scroll_to_bottom,
                    ),
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun PostsBody(
    posts: LazyPagingItems<ChallengePost>,
    listState: LazyListState,
    onPostLongPress: (ChallengePost) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        reverseLayout = true,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        items(
            count = posts.itemCount,
            key = posts.itemKey { it.postId },
        ) { index ->
            PostItem(
                index = index,
                posts = posts,
                showBottomSpacer = index == 0,
                onLongPress = onPostLongPress,
            )
        }
        item {
            posts.lastItem?.let { lastPost ->
                DateDivider(lastPost.writtenDateTime)
            }
        }
        item {
            LoadStateFooter(
                loadState = posts.loadState.append,
                onClickRetry = { posts.retry() },
            )
        }
    }
}

private val <T : Any> LazyPagingItems<T>.lastItem: T?
    get() = if (itemCount > 0) get(itemCount - 1) else null

@Composable
private fun PostItem(
    index: Int,
    posts: LazyPagingItems<ChallengePost>,
    showBottomSpacer: Boolean,
    onLongPress: (ChallengePost) -> Unit,
) {
    val post = posts[index] ?: return
    val previousPost = if (index < posts.itemCount - 1) posts.peek(index + 1) else null
    val nextPost = if (index > 0) posts.peek(index - 1) else null

    if (showBottomSpacer) {
        Spacer(modifier = Modifier.height(8.dp))
    }

    ChatMessageItem(
        post = post,
        previousPost = previousPost,
        nextPost = nextPost,
        onLongPress = { onLongPress(post) },
    )

    if (previousPost != null && !isSameDay(post.writtenDateTime, previousPost.writtenDateTime)) {
        DateDivider(post.writtenDateTime)
    }
}

@Composable
private fun LoadStateFooter(
    loadState: LoadState,
    onClickRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (loadState) {
            is LoadState.Loading -> LoadingWheel()
            is LoadState.Error -> ErrorItem(onClickRetry = onClickRetry)
            else -> Unit
        }
    }
}

@Composable
private fun DateDivider(date: LocalDateTime) {
    val context = LocalContext.current
    val formattedDate = remember(date) {
        val javaDate = Date.from(date.atZone(ZoneId.systemDefault()).toInstant())
        DateUtils.formatDateTime(
            context,
            javaDate.time,
            DateUtils.FORMAT_SHOW_YEAR or
                DateUtils.FORMAT_SHOW_DATE or
                DateUtils.FORMAT_SHOW_WEEKDAY,
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = CustomColorProvider.colorScheme.divider,
            thickness = 1.dp,
        )
        Text(
            text = formattedDate,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = CustomColorProvider.colorScheme.divider,
            thickness = 1.dp,
        )
    }
}

@Composable
private fun ChatMessageItem(
    post: ChallengePost,
    previousPost: ChallengePost?,
    nextPost: ChallengePost?,
    onLongPress: () -> Unit,
) {
    val isSameAuthorAsPrevious = previousPost?.writer?.name == post.writer.name &&
        isSameMinute(previousPost.writtenDateTime, post.writtenDateTime)
    val isSameAuthorAsNext = nextPost?.writer?.name == post.writer.name &&
        isSameMinute(nextPost.writtenDateTime, post.writtenDateTime)

    if (post.isAuthor) {
        MyMessage(
            post = post,
            showTime = !isSameAuthorAsNext,
            onLongPress = onLongPress,
            shape = RoundedCornerShape(
                topStart = MaterialTheme.shapes.medium.topStart,
                topEnd = MaterialTheme.shapes.extraSmall.topEnd,
                bottomStart = MaterialTheme.shapes.medium.bottomStart,
                bottomEnd = MaterialTheme.shapes.medium.bottomEnd,
            ),
        )
    } else {
        OthersMessage(
            post = post,
            showProfile = !isSameAuthorAsPrevious,
            showTime = !isSameAuthorAsNext,
            onLongPress = onLongPress,
            shape = RoundedCornerShape(
                topStart = MaterialTheme.shapes.extraSmall.topStart,
                topEnd = MaterialTheme.shapes.medium.topEnd,
                bottomStart = MaterialTheme.shapes.medium.bottomStart,
                bottomEnd = MaterialTheme.shapes.medium.bottomEnd,
            ),
        )
    }
}

@Composable
private fun MyMessage(
    post: ChallengePost,
    showTime: Boolean,
    shape: RoundedCornerShape,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(start = 32.dp),
        ) {
            if (showTime && post.isSynced) {
                Text(
                    text = post.writtenDateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.labelSmall,
                    color = CustomColorProvider.colorScheme.onBackgroundMuted,
                    modifier = Modifier.padding(end = 8.dp),
                )
            }
            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(
                        color = CustomColorProvider.colorScheme.surface.copy(
                            alpha = if (post.isSynced) 1f else 0.5f,
                        ),
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { onLongPress() },
                        )
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    text = post.content,
                    color = CustomColorProvider.colorScheme.onSurface.copy(
                        alpha = if (post.isSynced) 1f else 0.3f,
                    ),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
        if (showTime) {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun OthersMessage(
    post: ChallengePost,
    showProfile: Boolean,
    showTime: Boolean,
    shape: RoundedCornerShape,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (showProfile) {
                Box(
                    modifier = Modifier.width(40.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(CustomColorProvider.colorScheme.surface)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircleMedal(tier = post.writer.tier)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }

            Column {
                if (showProfile) {
                    Text(
                        text = post.writer.getDisplayName(),
                        style = MaterialTheme.typography.labelSmall,
                        color = post.writer.getDisplayColor(
                            activeColor = CustomColorProvider.colorScheme.onBackgroundMuted,
                            inActiveColor = CustomColorProvider.colorScheme.divider,
                        ),
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }

                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.padding(end = 32.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .clip(shape)
                            .background(CustomColorProvider.colorScheme.surface)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = { onLongPress() },
                                )
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .weight(1f, fill = false),
                    ) {
                        Text(
                            text = post.content,
                            color = CustomColorProvider.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }

                    if (showTime) {
                        Text(
                            text = post.writtenDateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                            style = MaterialTheme.typography.labelSmall,
                            color = CustomColorProvider.colorScheme.onBackgroundMuted,
                            modifier = Modifier.padding(start = 8.dp),
                            maxLines = 1,
                        )
                    }
                }
            }
        }
        if (showTime) {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private const val NEAR_BOTTOM_THRESHOLD_ITEMS = 5

private fun isSameDay(date1: LocalDateTime, date2: LocalDateTime): Boolean {
    return date1.toLocalDate() == date2.toLocalDate()
}

private fun isSameMinute(date1: LocalDateTime, date2: LocalDateTime): Boolean {
    return date1.toLocalDate() == date2.toLocalDate() &&
        date1.hour == date2.hour &&
        date1.minute == date2.minute
}

@DevicePreviews
@Composable
fun ChallengeBoardScreenPreview(
    @PreviewParameter(ChallengePostPreviewParameterProvider::class)
    posts: List<ChallengePost>,
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
            ChallengeBoardScreen(
                posts = pagingData.collectAsLazyPagingItems(),
            )
        }
    }
}
