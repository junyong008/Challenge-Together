package com.yjy.feature.community.model

sealed interface CommunityPostUiEvent {
    sealed class LoadFailure : CommunityPostUiEvent {
        data object NotFound : LoadFailure()
        data object Network : LoadFailure()
        data object Unknown : LoadFailure()
    }

    data object NotificationOn : CommunityPostUiEvent
    data object NotificationOff : CommunityPostUiEvent
    data object SendCommentSuccess : CommunityPostUiEvent
    data object DeletePostSuccess : CommunityPostUiEvent
    data object DeleteCommentSuccess : CommunityPostUiEvent
    data object ReportSuccess : CommunityPostUiEvent
    data object ReportDuplicated : CommunityPostUiEvent
    data object BookmarkSuccess : CommunityPostUiEvent
    data object UnBookmarkSuccess : CommunityPostUiEvent
    data object UnknownFailure : CommunityPostUiEvent
}
