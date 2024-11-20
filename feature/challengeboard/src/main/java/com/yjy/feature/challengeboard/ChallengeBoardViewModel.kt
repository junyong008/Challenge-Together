package com.yjy.feature.challengeboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.yjy.common.core.extensions.restartableStateIn
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.domain.AddChallengePostUseCase
import com.yjy.domain.GetChallengePostsUseCase
import com.yjy.feature.challengeboard.model.ChallengeBoardUiAction
import com.yjy.feature.challengeboard.model.PostsUpdateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengeBoardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getChallengePostsUseCase: GetChallengePostsUseCase,
    private val addChallengePostUseCase: AddChallengePostUseCase,
    private val challengeRepository: ChallengeRepository,
) : ViewModel() {

    private val challengeId = savedStateHandle.getStateFlow<Int?>("challengeId", null)

    val posts = challengeId
        .filterNotNull()
        .flatMapLatest { challengeId ->
            getChallengePostsUseCase(challengeId)
        }
        .cachedIn(viewModelScope)

    val latestPost = challengeId
        .filterNotNull()
        .flatMapLatest {
            challengeRepository.getLatestChallengePost(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    val postsUpdateState = challengeId
        .filterNotNull()
        .flatMapLatest { challengeId ->
            challengeRepository.observeChallengePostUpdates(challengeId)
        }
        .map<Unit, PostsUpdateState> { PostsUpdateState.Connected }
        .catch { emit(PostsUpdateState.Error) }
        .restartableStateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PostsUpdateState.Loading,
        )

    fun processAction(action: ChallengeBoardUiAction) {
        when (action) {
            is ChallengeBoardUiAction.OnSendClick -> addPost(action.content)
            ChallengeBoardUiAction.OnRetryClick -> postsUpdateState.restart()
        }
    }

    private fun addPost(content: String) = viewModelScope.launch {
        val id = challengeId.value ?: return@launch
        addChallengePostUseCase(id, content)
    }
}
