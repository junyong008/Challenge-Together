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
    CHALLENGE_GIVE_UP(
        type = "challenge_give_up",
        category = NotificationCategory.CHALLENGE,
        destination = { NotificationDestination.StaredChallenge(it) },
    ),
    CHALLENGE_STARTED_POST(
        type = "challenge_started_post",
        category = NotificationCategory.CHALLENGE,
        destination = { NotificationDestination.StaredChallenge(it) },
    ),
    CHALLENGE_WAITING_POST(
        type = "challenge_waiting_post",
        category = NotificationCategory.CHALLENGE,
        destination = { NotificationDestination.WaitingChallenge(it) },
    ),
    CHALLENGE_FORCE_REMOVE(
        type = "challenge_force_remove",
        category = NotificationCategory.CHALLENGE,
        destination = { NotificationDestination.StaredChallenge(it) },
    ),
    CHALLENGE_START(
        type = "challenge_start",
        category = NotificationCategory.CHALLENGE,
        destination = { NotificationDestination.StaredChallenge(it) },
    ),
    CHALLENGE_DELETE(
        type = "challenge_delete",
        category = NotificationCategory.CHALLENGE,
        destination = { NotificationDestination.None },
    ),
    CHALLENGE_JOIN(
        type = "challenge_join",
        category = NotificationCategory.CHALLENGE,
        destination = { NotificationDestination.WaitingChallenge(it) },
    ),
    CHALLENGE_LEAVE(
        type = "challenge_leave",
        category = NotificationCategory.CHALLENGE,
        destination = { NotificationDestination.WaitingChallenge(it) },
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
