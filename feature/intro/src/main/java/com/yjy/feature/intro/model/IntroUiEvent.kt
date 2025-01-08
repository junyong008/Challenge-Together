package com.yjy.feature.intro.model

sealed interface IntroUiEvent {
    data class NeedKakaoSignUp(val id: String) : IntroUiEvent
    data class NeedGoogleSignUp(val id: String) : IntroUiEvent
    data class NeedNaverSignUp(val id: String) : IntroUiEvent
    data class NeedGuestSignUp(val id: String) : IntroUiEvent
    data object LoginSuccess : IntroUiEvent
    data object LoginFailure : IntroUiEvent
}
