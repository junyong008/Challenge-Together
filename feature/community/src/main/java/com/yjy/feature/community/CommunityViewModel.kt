package com.yjy.feature.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.community.api.CommunityRepository
import com.yjy.domain.GetCommunityPostsUseCase
import com.yjy.feature.community.model.CommunityUiEvent
import com.yjy.model.community.SimpleCommunityPostType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
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

    val allPosts = combine(
        refreshTrigger.onStart { emit(Unit) },
        searchQuery,
    ) { _, query ->
        query
    }.flatMapLatest { query ->
        getCommunityPostsUseCase(query = query, postType = SimpleCommunityPostType.ALL)
    }.cachedIn(viewModelScope)

    val bookmarkedPosts = getCommunityPostsUseCase(postType = SimpleCommunityPostType.BOOKMARKED)
        .cachedIn(viewModelScope)

    val commentedPosts = getCommunityPostsUseCase(postType = SimpleCommunityPostType.COMMENTED)
        .cachedIn(viewModelScope)

    val authoredPosts = getCommunityPostsUseCase(postType = SimpleCommunityPostType.AUTHORED)
        .cachedIn(viewModelScope)

    private fun sendEvent(event: CommunityUiEvent) = viewModelScope.launch {
        _uiEvent.send(event)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addPost(content: String) = viewModelScope.launch {
        _isAddingPost.value = true
        communityRepository.addPost(content)
            .onSuccess {
                refreshTrigger.tryEmit(Unit)
                sendEvent(CommunityUiEvent.AddSuccess)
            }
            .onFailure {
                sendEvent(CommunityUiEvent.AddFailure)
            }

        _isAddingPost.value = false
    }
}
