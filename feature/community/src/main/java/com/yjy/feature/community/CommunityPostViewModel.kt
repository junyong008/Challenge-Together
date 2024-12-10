package com.yjy.feature.community

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.core.constants.CommunityConst.MAX_POST_COMMENT_LENGTH
import com.yjy.common.core.extensions.restartableStateIn
import com.yjy.common.network.HttpStatusCodes
import com.yjy.common.network.HttpStatusCodes.NOT_FOUND
import com.yjy.common.network.handleNetworkResult
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.community.api.CommunityRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.domain.GetCommunityPostUseCase
import com.yjy.feature.community.model.CommunityPostDetailUiState
import com.yjy.feature.community.model.CommunityPostUiAction
import com.yjy.feature.community.model.CommunityPostUiEvent
import com.yjy.feature.community.model.CommunityPostUiState
import com.yjy.model.common.ReportReason
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityPostViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCommunityPostUseCase: GetCommunityPostUseCase,
    private val userRepository: UserRepository,
    private val communityRepository: CommunityRepository,
) : ViewModel() {

    private val postId = savedStateHandle.getStateFlow<Int?>("postId", null)
    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private val _uiState = MutableStateFlow(CommunityPostUiState())
    val uiState: StateFlow<CommunityPostUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<CommunityPostUiEvent>()
    val uiEvent: Flow<CommunityPostUiEvent> = _uiEvent.receiveAsFlow()

    val postDetail = combine(
        postId.filterNotNull(),
        refreshTrigger.onStart { emit(Unit) },
    ) { postId, _ ->
        postId
    }.flatMapLatest {
        getCommunityPostUseCase(it)
    }.map { result ->
        handleNetworkResult(
            result = result,
            onSuccess = { postDetail -> CommunityPostDetailUiState.Success(postDetail) },
            onHttpError = { code ->
                when (code) {
                    NOT_FOUND -> CommunityPostUiEvent.LoadFailure.NotFound
                    else -> CommunityPostUiEvent.LoadFailure.Unknown
                }.also { sendEvent(it) }
                CommunityPostDetailUiState.Loading
            },
            onNetworkError = {
                sendEvent(CommunityPostUiEvent.LoadFailure.Network)
                CommunityPostDetailUiState.Loading
            },
            onUnknownError = {
                sendEvent(CommunityPostUiEvent.LoadFailure.Unknown)
                CommunityPostDetailUiState.Loading
            },
        )
    }.restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = CommunityPostDetailUiState.Loading,
    )

    val isNotificationOn = userRepository.mutedCommunityPostIds
        .map { mutedCommunityPostIds ->
            mutedCommunityPostIds.contains(postId.value).not()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true,
        )

    private fun sendEvent(event: CommunityPostUiEvent) = viewModelScope.launch {
        _uiEvent.send(event)
    }

    fun processAction(action: CommunityPostUiAction) {
        when (action) {
            CommunityPostUiAction.OnRefreshClick -> postDetail.restart()
            is CommunityPostUiAction.OnToggleNotificationClick ->
                toggleNotification(action.postId, action.isMuted)

            is CommunityPostUiAction.OnToggleLikeClick -> toggleLike(action.postId)
            is CommunityPostUiAction.OnToggleBookmarkClick ->
                toggleBookmark(action.postId, action.isBookmarked)

            is CommunityPostUiAction.OnCommentUpdated -> updateComment(action.comment)
            is CommunityPostUiAction.OnDeletePostClick -> deletePost(action.postId)
            is CommunityPostUiAction.OnDeleteCommentClick -> deleteComment(action.commentId)
            is CommunityPostUiAction.OnReportPostClick -> reportPost(action.postId, action.reason)

            is CommunityPostUiAction.OnReportCommentClick ->
                reportComment(action.commentId, action.reason)

            is CommunityPostUiAction.OnSendCommentClick ->
                sendComment(action.postId, action.content, action.replyTo)
        }
    }

    private fun updatePost() {
        refreshTrigger.tryEmit(Unit)
    }

    private fun toggleNotification(postId: Int, isMuted: Boolean) = viewModelScope.launch {
        if (isMuted) {
            userRepository.unMuteCommunityPostNotification(postId)
            sendEvent(CommunityPostUiEvent.NotificationOn)
        } else {
            userRepository.muteCommunityPostNotification(postId)
            sendEvent(CommunityPostUiEvent.NotificationOff)
        }
    }

    private fun toggleLike(postId: Int) = viewModelScope.launch {
        _uiState.update { it.copy(isLikingPost = true) }
        communityRepository.toggleLike(postId)
            .onFailure { sendEvent(CommunityPostUiEvent.UnknownFailure) }

        updatePost()
        _uiState.update { it.copy(isLikingPost = false) }
    }

    private fun toggleBookmark(postId: Int, isBookmarked: Boolean) = viewModelScope.launch {
        _uiState.update { it.copy(isBookmarkingPost = true) }
        communityRepository.toggleBookmark(postId)
            .onSuccess {
                if (isBookmarked) {
                    sendEvent(CommunityPostUiEvent.UnBookmarkSuccess)
                } else {
                    sendEvent(CommunityPostUiEvent.BookmarkSuccess)
                }
            }
            .onFailure { sendEvent(CommunityPostUiEvent.UnknownFailure) }

        updatePost()
        _uiState.update { it.copy(isBookmarkingPost = false) }
    }

    private fun updateComment(comment: String) {
        _uiState.update {
            it.copy(comment = comment.take(MAX_POST_COMMENT_LENGTH))
        }
    }

    private fun deletePost(postId: Int) = viewModelScope.launch {
        _uiState.update { it.copy(isDeletingPost = true) }
        communityRepository.deletePost(postId)
            .onSuccess { sendEvent(CommunityPostUiEvent.DeletePostSuccess) }
            .onFailure { sendEvent(CommunityPostUiEvent.UnknownFailure) }

        _uiState.update { it.copy(isDeletingPost = false) }
    }

    private fun deleteComment(commentId: Int) = viewModelScope.launch {
        communityRepository.deleteComment(commentId)
            .onSuccess { sendEvent(CommunityPostUiEvent.DeleteCommentSuccess) }
            .onFailure { sendEvent(CommunityPostUiEvent.UnknownFailure) }
        updatePost()
    }

    private fun reportPost(postId: Int, reason: ReportReason) = viewModelScope.launch {
        handleNetworkResult(
            result = communityRepository.reportPost(postId, reason),
            onSuccess = { CommunityPostUiEvent.ReportSuccess },
            onHttpError = { code ->
                when (code) {
                    HttpStatusCodes.CONFLICT -> CommunityPostUiEvent.ReportDuplicated
                    else -> CommunityPostUiEvent.UnknownFailure
                }
            },
            onNetworkError = { CommunityPostUiEvent.UnknownFailure },
            onUnknownError = { CommunityPostUiEvent.UnknownFailure },
        ).also { sendEvent(it) }
        updatePost()
    }

    private fun reportComment(commentId: Int, reason: ReportReason) = viewModelScope.launch {
        handleNetworkResult(
            result = communityRepository.reportComment(commentId, reason),
            onSuccess = { CommunityPostUiEvent.ReportSuccess },
            onHttpError = { code ->
                when (code) {
                    HttpStatusCodes.CONFLICT -> CommunityPostUiEvent.ReportDuplicated
                    else -> CommunityPostUiEvent.UnknownFailure
                }
            },
            onNetworkError = { CommunityPostUiEvent.UnknownFailure },
            onUnknownError = { CommunityPostUiEvent.UnknownFailure },
        ).also { sendEvent(it) }
        updatePost()
    }

    private fun sendComment(
        postId: Int,
        comment: String,
        parentCommentId: Int,
    ) = viewModelScope.launch {
        if (comment.isEmpty()) return@launch

        _uiState.update { it.copy(isAddingComments = true) }
        communityRepository.addComment(
            postId = postId,
            content = comment,
            parentCommentId = parentCommentId,
        ).onSuccess {
            updatePost()
            updateComment("")
            sendEvent(CommunityPostUiEvent.SendCommentSuccess)
        }.onFailure {
            sendEvent(CommunityPostUiEvent.UnknownFailure)
        }

        _uiState.update { it.copy(isAddingComments = false) }
    }
}
