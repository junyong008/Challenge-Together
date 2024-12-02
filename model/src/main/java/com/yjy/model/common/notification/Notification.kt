package com.yjy.model.common.notification

import java.time.LocalDateTime

data class Notification(
    val id: Int,
    val header: String,
    val body: String,
    val createdDateTime: LocalDateTime,
    val type: NotificationType,
    val linkId: Int,
) {
    val category: NotificationCategory
        get() = type.category

    val destination: NotificationDestination
        get() = type.destination(linkId)
}
