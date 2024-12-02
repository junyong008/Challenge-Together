package com.yjy.model.common.notification

enum class NotificationType(
    val type: String,
    val category: NotificationCategory,
    val destination: (Int) -> NotificationDestination,
) {
    CHALLENGE_RESET(
        type = "challenge_reset",
        category = NotificationCategory.CHALLENGE,
        destination = { NotificationDestination.StaredChallenge(it) },
    ),
    CHALLENGE_DELETE(
        type = "challenge_delete",
        category = NotificationCategory.CHALLENGE,
        destination = { NotificationDestination.StaredChallenge(it) },
    ),
    CHALLENGE_STARTED_POST(
        type = "challenge_started_post",
        category = NotificationCategory.CHALLENGE,
        destination = { NotificationDestination.StaredChallenge(it) },
    ),
    CHALLENGE_FORCE_REMOVE(
        type = "challenge_force_remove",
        category = NotificationCategory.CHALLENGE,
        destination = { NotificationDestination.StaredChallenge(it) },
    ),
    COMMON(
        type = "common",
        category = NotificationCategory.COMMON,
        destination = { NotificationDestination.None },
    ),
    ;

    companion object {
        fun fromString(type: String) = entries.find { it.type == type } ?: COMMON
    }
}
