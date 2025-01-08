package com.yjy.feature.linkaccount.model

sealed interface LinkAccountUiAction {
    data class OnLinkWithKakao(val kakaoId: String) : LinkAccountUiAction
    data class OnLinkWithGoogle(val googleId: String) : LinkAccountUiAction
    data class OnLinkWithNaver(val naverId: String) : LinkAccountUiAction
    data object OnLinkFailed : LinkAccountUiAction
}
