package com.yjy.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "challenge_posts")
data class ChallengePostEntity(
    @PrimaryKey
    val id: Int,
    val tempId: Int,
    val challengeId: Int,
    val writerName: String,
    val writerBestRecordInSeconds: Long,
    val content: String,
    val writtenDateTime: LocalDateTime,
    val isAuthor: Boolean,
    val isSynced: Boolean,
)
