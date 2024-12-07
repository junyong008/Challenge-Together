package com.yjy.feature.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.yjy.domain.GetCommunityPostsUseCase
import com.yjy.model.community.SimpleCommunityPostType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    getCommunityPostsUseCase: GetCommunityPostsUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val allPosts = searchQuery
        .flatMapLatest { query ->
            getCommunityPostsUseCase(query = query, postType = SimpleCommunityPostType.ALL)
        }.cachedIn(viewModelScope)

    val bookmarkedPosts = getCommunityPostsUseCase(postType = SimpleCommunityPostType.BOOKMARKED)
        .cachedIn(viewModelScope)

    val commentedPosts = getCommunityPostsUseCase(postType = SimpleCommunityPostType.COMMENTED)
        .cachedIn(viewModelScope)

    val authoredPosts = getCommunityPostsUseCase(postType = SimpleCommunityPostType.AUTHORED)
        .cachedIn(viewModelScope)

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
