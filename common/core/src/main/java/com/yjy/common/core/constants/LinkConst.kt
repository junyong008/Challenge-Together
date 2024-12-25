package com.yjy.common.core.constants

object UrlConst {
    const val PRIVACY_POLICY = "https://sites.google.com/view/challenge-together/%ED%99%88"
    const val PLAY_STORE = "https://play.google.com/store/apps/details?id=com.yjy.challengetogether"
}

object DeepLinkConfig {
    const val SCHEME_AND_HOST = "challenge-together://app"

    private const val ONE_LINK_BASE = "https://challenge-together.onelink.me"
    private const val ONE_LINK_DEFAULT_PATH = "4CDY"
    private const val ONE_LINK_SHORT_KEY = "p0dl8qn3"
    const val ONE_LINK_URL = "$ONE_LINK_BASE/$ONE_LINK_DEFAULT_PATH/$ONE_LINK_SHORT_KEY"
}

object DeepLinkType {
    const val TYPE_PARAM = "type"
    const val ID_PARAM = "id"
    const val WAITING = "waiting"
    const val POST = "post"
}

object DeepLinkPath {
    const val STARTED = "challenge/started"
    const val WAITING = "challenge/waiting"
    const val POST = "community/post"
}
