package com.yjy.feature.community.model

import com.yjy.model.community.DetailedCommunityPost

sealed interface CommunityPostDetailUiState {
    data object Loading : CommunityPostDetailUiState
    data class Success(val postDetail: DetailedCommunityPost) : CommunityPostDetailUiState
}

fun CommunityPostDetailUiState.postDetailOrNull(): DetailedCommunityPost? = when (this) {
    is CommunityPostDetailUiState.Loading -> null
    is CommunityPostDetailUiState.Success -> postDetail
}

fun CommunityPostDetailUiState.isLoading(): Boolean = this is CommunityPostDetailUiState.Loading
