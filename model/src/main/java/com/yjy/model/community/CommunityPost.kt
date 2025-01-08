package com.yjy.model.community

import com.yjy.model.common.User
import java.time.LocalDateTime

data class CommunityPost(
    val postId: Int,
    val writer: User,
    val content: String,
    val commentCount: Int,
    val likeCount: Int,
    val writtenDateTime: LocalDateTime,
    val modifiedDateTime: LocalDateTime,
    val isAuthor: Boolean,
    val isLiked: Boolean,
    val isBookmarked: Boolean,
)
