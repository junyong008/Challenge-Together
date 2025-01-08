package com.yjy.feature.community.model

sealed interface CommunityUiEvent {
    data object AddSuccess : CommunityUiEvent
    data object AddFailure : CommunityUiEvent
    data object EditSuccess : CommunityUiEvent
    data object EditFailure : CommunityUiEvent
    data object GlobalOn : CommunityUiEvent
    data object GlobalOff : CommunityUiEvent
}
