package com.yjy.data.user.impl.mapper

import com.yjy.data.database.model.NotificationEntity
import com.yjy.data.network.response.GetNotificationsResponse
import com.yjy.model.common.notification.Notification
import com.yjy.model.common.notification.NotificationType

internal fun GetNotificationsResponse.toEntity() = NotificationEntity(
    id = notificationId,
    header = header,
    body = body,
    createdDateTime = createdDateTime.toLocalDateTime(),
    type = type,
    linkId = linkId,
)

internal fun NotificationEntity.toModel() = Notification(
    id = id,
    header = header,
    body = body,
    createdDateTime = createdDateTime,
    type = NotificationType.fromString(type),
    linkId = linkId,
)
