package com.yjy.feature.challengeboard.model

sealed interface ChallengeBoardUiAction {
    data class OnSendClick(val content: String) : ChallengeBoardUiAction
    data object OnRetryClick : ChallengeBoardUiAction
}
