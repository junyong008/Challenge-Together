package com.yjy.platform.notifications.model

import com.yjy.platform.notifications.constants.DeepLinkPaths

sealed interface PlatformNotificationNavigation {
    val requestCode: Int
    val deepLinkPath: String
    val param: String

    data class StartedChallenge(override val param: String) : PlatformNotificationNavigation {
        override val requestCode = DeepLinkPaths.Challenge.STARTED_REQUEST_CODE
        override val deepLinkPath = DeepLinkPaths.Challenge.STARTED
    }
}
