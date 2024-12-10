package com.yjy.feature.community.model

data class CommunityPostUiState(
    val comment: String = "",
    val isDeletingPost: Boolean = false,
    val isBookmarkingPost: Boolean = false,
    val isLikingPost: Boolean = false,
    val isAddingComments: Boolean = false,
)
