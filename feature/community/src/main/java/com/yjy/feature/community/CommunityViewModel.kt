package com.yjy.feature.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yjy.common.core.extensions.restartableStateIn
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.community.api.CommunityRepository
import com.yjy.domain.GetCommunityPostsUseCase
import com.yjy.feature.community.model.BannerUiState
import com.yjy.feature.community.model.CommunityUiEvent
import com.yjy.model.community.SimpleCommunityPostType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    getCommunityPostsUseCase: GetCommunityPostsUseCase,
    private val communityRepository: CommunityRepository,
) : ViewModel() {

    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private val _uiEvent = Channel<CommunityUiEvent>()
    val uiEvent: Flow<CommunityUiEvent> = _uiEvent.receiveAsFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isAddingPost = MutableStateFlow(false)
    val isAddingPost = _isAddingPost.asStateFlow()

    private val _isEditingPost = MutableStateFlow(false)
    val isEditingPost = _isEditingPost.asStateFlow()

    private val _isGlobalActive = MutableStateFlow(false)
    val isGlobalActive = _isGlobalActive.asStateFlow()

    private val _currentLanguageCode = MutableStateFlow("")

    val allPosts = combine(
        refreshTrigger.onStart { emit(Unit) },
        searchQuery,
        _isGlobalActive,
        _currentLanguageCode,
    ) { _, query, isGlobal, languageCode ->
        Triple(query, isGlobal, languageCode)
    }.flatMapLatest { (query, isGlobal, languageCode) ->
        getCommunityPostsUseCase(
            query = query,
            languageCode = if (!isGlobal) languageCode else "",
            postType = SimpleCommunityPostType.ALL,
        )
    }.cachedIn(viewModelScope).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = PagingData.empty(),
    )

    val bookmarkedPosts = getCommunityPostsUseCase(postType = SimpleCommunityPostType.BOOKMARKED)
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty(),
        )

    val commentedPosts = getCommunityPostsUseCase(postType = SimpleCommunityPostType.COMMENTED)
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty(),
        )

    val authoredPosts = getCommunityPostsUseCase(postType = SimpleCommunityPostType.AUTHORED)
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty(),
        )

    val banners = flow {
        communityRepository.getBanners()
            .onSuccess { emit(BannerUiState.Success(it)) }
            .onFailure { emit(BannerUiState.Error) }
    }.restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BannerUiState.Loading,
    )

    fun reloadBanners() {
        banners.restart()
    }

    fun toggleGlobalMode() {
        _isGlobalActive.value = !_isGlobalActive.value
        if (_isGlobalActive.value) {
            sendEvent(CommunityUiEvent.GlobalOn)
        } else {
            sendEvent(CommunityUiEvent.GlobalOff)
        }
    }

    fun updateLanguageCode(languageCode: String) {
        _currentLanguageCode.value = languageCode
    }

    private fun sendEvent(event: CommunityUiEvent) {
        viewModelScope.launch { _uiEvent.send(event) }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addPost(content: String, languageCode: String) = viewModelScope.launch {
        _isAddingPost.value = true
        communityRepository.addPost(content, languageCode)
            .onSuccess {
                refreshTrigger.tryEmit(Unit)
                sendEvent(CommunityUiEvent.AddSuccess)
            }
            .onFailure {
                sendEvent(CommunityUiEvent.AddFailure)
            }

        _isAddingPost.value = false
    }

    fun editPost(postId: Int, content: String) = viewModelScope.launch {
        _isEditingPost.value = true
        communityRepository.editPost(postId, content)
            .onSuccess { sendEvent(CommunityUiEvent.EditSuccess) }
            .onFailure { sendEvent(CommunityUiEvent.EditFailure) }

        _isEditingPost.value = false
    }
}
