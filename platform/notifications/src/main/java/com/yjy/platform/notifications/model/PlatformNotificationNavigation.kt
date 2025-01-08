package com.yjy.platform.notifications.model

import com.yjy.common.core.constants.DeepLinkPath.POST
import com.yjy.common.core.constants.DeepLinkPath.STARTED
import com.yjy.common.core.constants.DeepLinkPath.WAITING
import com.yjy.platform.notifications.constants.RequestCodes.POST_REQUEST_CODE
import com.yjy.platform.notifications.constants.RequestCodes.STARTED_REQUEST_CODE
import com.yjy.platform.notifications.constants.RequestCodes.WAITING_REQUEST_CODE

sealed interface PlatformNotificationNavigation {
    val requestCode: Int
    val deepLinkPath: String
    val param: String

    data class StartedChallenge(override val param: String) : PlatformNotificationNavigation {
        override val requestCode = STARTED_REQUEST_CODE
        override val deepLinkPath = STARTED
    }

    data class WaitingChallenge(override val param: String) : PlatformNotificationNavigation {
        override val requestCode = WAITING_REQUEST_CODE
        override val deepLinkPath = WAITING
    }

    data class CommunityPost(override val param: String) : PlatformNotificationNavigation {
        override val requestCode = POST_REQUEST_CODE
        override val deepLinkPath = POST
    }
}
