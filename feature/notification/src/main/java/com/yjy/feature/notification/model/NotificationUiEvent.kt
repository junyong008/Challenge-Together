package com.yjy.feature.notification.model

sealed interface NotificationUiEvent {
    data object DeleteSuccess : NotificationUiEvent
    data object DeleteFailed : NotificationUiEvent
}
