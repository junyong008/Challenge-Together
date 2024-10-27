package com.yjy.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val targetDays: Int,
    val currentParticipantCount: Int,
    val maxParticipantCount: Int,
    val recentResetDateTime: LocalDateTime?,
    val isStarted: Boolean,
    val isPrivate: Boolean,
    val isCompleted: Boolean,
    val mode: String,
)
