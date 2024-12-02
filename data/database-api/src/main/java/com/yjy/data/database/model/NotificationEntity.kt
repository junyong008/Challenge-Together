package com.yjy.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: Int,
    val header: String,
    val body: String,
    val createdDateTime: LocalDateTime,
    val type: String,
    val linkId: Int,
)
