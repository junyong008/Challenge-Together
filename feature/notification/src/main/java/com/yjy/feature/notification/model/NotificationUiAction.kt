package com.yjy.feature.notification.model

sealed interface NotificationUiAction {
    data class OnDeleteItemClick(val notificationId: Int) : NotificationUiAction
    data object OnDeleteAllClick : NotificationUiAction
}
