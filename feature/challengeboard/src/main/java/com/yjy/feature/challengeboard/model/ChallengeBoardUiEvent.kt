package com.yjy.feature.challengeboard.model

sealed interface ChallengeBoardUiEvent {
    data object DeleteSuccess : ChallengeBoardUiEvent
    data object DeleteFailure : ChallengeBoardUiEvent
    data object ReportSuccess : ChallengeBoardUiEvent
    data object ReportFailure : ChallengeBoardUiEvent
    data object ReportDuplicated : ChallengeBoardUiEvent
    data object NotificationOn : ChallengeBoardUiEvent
    data object NotificationOff : ChallengeBoardUiEvent
}
