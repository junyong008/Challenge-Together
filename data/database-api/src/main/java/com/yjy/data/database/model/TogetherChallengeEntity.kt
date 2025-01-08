package com.yjy.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "together_challenges")
data class TogetherChallengeEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val targetDays: Int,
    val currentParticipantCount: Int,
    val maxParticipantCount: Int,
    val isPrivate: Boolean,
)
