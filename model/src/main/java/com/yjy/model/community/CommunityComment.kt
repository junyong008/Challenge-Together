package com.yjy.model.community

import com.yjy.model.common.User
import java.time.LocalDateTime

data class CommunityComment(
    val commentId: Int,
    val parentCommentId: Int,
    val writer: User,
    val content: String,
    val writtenDateTime: LocalDateTime,
    val isAuthor: Boolean,
    val isPostWriter: Boolean,
)
