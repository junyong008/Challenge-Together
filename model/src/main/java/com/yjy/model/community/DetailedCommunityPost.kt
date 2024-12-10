package com.yjy.model.community

data class DetailedCommunityPost(
    val post: CommunityPost,
    val comments: List<CommunityComment>,
)
