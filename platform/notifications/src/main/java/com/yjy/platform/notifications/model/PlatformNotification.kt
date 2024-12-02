package com.yjy.platform.notifications.model

data class PlatformNotification(
    val id: Int,
    val title: String,
    val message: String,
    val channelId: String,
    val group: String,
    val navigation: PlatformNotificationNavigation?,
)
