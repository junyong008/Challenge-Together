package com.yjy.model.challenge

import com.yjy.model.common.User
import java.time.LocalDateTime

data class ChallengePost(
    val postId: Int,
    val writer: User,
    val content: String,
    val writtenDateTime: LocalDateTime,
    val isAuthor: Boolean,
    val isSynced: Boolean,
)
