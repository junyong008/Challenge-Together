package com.yjy.feature.intro.model

sealed interface IntroUiAction {
    data class OnKakaoLoginClick(val kakaoId: String) : IntroUiAction
    data class OnGoogleLoginClick(val googleId: String) : IntroUiAction
    data class OnNaverLoginClick(val naverId: String) : IntroUiAction
    data class OnGuestLoginClick(val guestId: String) : IntroUiAction
    data object OnLoginFailure : IntroUiAction
}
