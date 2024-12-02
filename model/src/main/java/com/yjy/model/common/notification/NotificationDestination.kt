package com.yjy.model.common.notification

sealed interface NotificationDestination {
    data class StaredChallenge(val challengeId: Int) : NotificationDestination
    data object None : NotificationDestination
}
