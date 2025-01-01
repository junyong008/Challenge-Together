package com.yjy.feature.together.model

sealed interface TogetherUiEvent {
    data object GlobalOn : TogetherUiEvent
    data object GlobalOff : TogetherUiEvent
}
