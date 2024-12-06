package com.yjy.feature.challengeboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.yjy.common.core.extensions.restartableStateIn
import com.yjy.common.network.HttpStatusCodes
import com.yjy.common.network.handleNetworkResult
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.challenge.api.ChallengePostRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.domain.AddChallengePostUseCase
import com.yjy.domain.GetChallengePostsUseCase
import com.yjy.feature.challengeboard.model.ChallengeBoardUiAction
import com.yjy.feature.challengeboard.model.ChallengeBoardUiEvent
import com.yjy.feature.challengeboard.model.PostsUpdateState
import com.yjy.model.common.ReportReason
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeBoardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getChallengePostsUseCase: GetChallengePostsUseCase,
    private val addChallengePostUseCase: AddChallengePostUseCase,
    private val challengePostRepository: ChallengePostRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val challengeId = savedStateHandle.getStateFlow<Int?>("challengeId", null)

    private val _uiEvent = Channel<ChallengeBoardUiEvent>()
    val uiEvent: Flow<ChallengeBoardUiEvent> = _uiEvent.receiveAsFlow()

    val posts = challengeId
        .filterNotNull()
        .flatMapLatest { challengeId ->
            getChallengePostsUseCase(challengeId)
        }
        .cachedIn(viewModelScope)

    val latestPost = challengeId
        .filterNotNull()
        .flatMapLatest {
            challengePostRepository.getLatestChallengePost(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    val postsUpdateState = challengeId
        .filterNotNull()
        .flatMapLatest { challengeId ->
            challengePostRepository.observeChallengePostUpdates(challengeId)
        }
        .map<Unit, PostsUpdateState> { PostsUpdateState.Connected }
        .catch { emit(PostsUpdateState.Error) }
        .restartableStateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PostsUpdateState.Loading,
        )

    val isNotificationOn = userRepository.mutedChallengeBoardIds
        .map { mutedChallengeBoardIds ->
            mutedChallengeBoardIds.contains(challengeId.value).not()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true,
        )

    private fun sendEvent(event: ChallengeBoardUiEvent) = viewModelScope.launch {
        _uiEvent.send(event)
    }

    fun processAction(action: ChallengeBoardUiAction) {
        when (action) {
            is ChallengeBoardUiAction.OnSendClick -> addPost(action.content)
            is ChallengeBoardUiAction.OnDeletePostClick -> deletePost(action.postId)
            is ChallengeBoardUiAction.OnReportPostClick -> reportPost(action.postId, action.reason)
            ChallengeBoardUiAction.OnRetryClick -> postsUpdateState.restart()
            ChallengeBoardUiAction.ToggleNotification -> toggleNotification()
        }
    }

    private fun addPost(content: String) = viewModelScope.launch {
        val id = challengeId.value ?: return@launch
        addChallengePostUseCase(id, content)
    }

    private fun deletePost(postId: Int) = viewModelScope.launch {
        challengePostRepository.deleteChallengePost(postId)
            .onSuccess { sendEvent(ChallengeBoardUiEvent.DeleteSuccess) }
            .onFailure { sendEvent(ChallengeBoardUiEvent.DeleteFailure) }
    }

    private fun reportPost(postId: Int, reason: ReportReason) = viewModelScope.launch {
        val event = handleNetworkResult(
            result = challengePostRepository.reportChallengePost(postId, reason),
            onSuccess = { ChallengeBoardUiEvent.ReportSuccess },
            onHttpError = { code ->
                when (code) {
                    HttpStatusCodes.CONFLICT -> ChallengeBoardUiEvent.ReportDuplicated
                    else -> ChallengeBoardUiEvent.ReportFailure
                }
            },
            onNetworkError = { ChallengeBoardUiEvent.ReportFailure },
            onUnknownError = { ChallengeBoardUiEvent.ReportFailure },
        )
        sendEvent(event)
    }

    private fun toggleNotification() = viewModelScope.launch {
        if (challengeId.value == null) return@launch
        if (isNotificationOn.value) {
            userRepository.muteChallengeBoardNotification(challengeId.value!!)
            sendEvent(ChallengeBoardUiEvent.NotificationOff)
        } else {
            userRepository.unMuteChallengeBoardNotification(challengeId.value!!)
            sendEvent(ChallengeBoardUiEvent.NotificationOn)
        }
    }
}
