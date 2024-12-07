package com.yjy.data.database.model

import androidx.room.Entity
import java.time.LocalDateTime

@Entity(
    tableName = "community_posts",
    primaryKeys = ["id", "type"],
)
data class CommunityPostEntity(
    val id: Int,
    val content: String,
    val writerName: String,
    val writerBestRecordInSeconds: Long,
    val commentCount: Int,
    val likeCount: Int,
    val type: CommunityPostType,
    val writtenDateTime: LocalDateTime,
    val modifiedDateTime: LocalDateTime,
)
