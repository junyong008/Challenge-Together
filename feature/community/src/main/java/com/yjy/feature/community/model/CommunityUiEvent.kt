package com.yjy.feature.community.model

sealed interface CommunityUiEvent {
    data object AddSuccess : CommunityUiEvent
    data object AddFailure : CommunityUiEvent
}
