package com.yjy.feature.community.model

import com.yjy.model.common.ReportReason

sealed interface CommunityPostUiAction {
    data object OnRefreshClick : CommunityPostUiAction
    data class OnToggleNotificationClick(val postId: Int, val isMuted: Boolean) : CommunityPostUiAction
    data class OnToggleLikeClick(val postId: Int) : CommunityPostUiAction
    data class OnToggleBookmarkClick(val postId: Int, val isBookmarked: Boolean) : CommunityPostUiAction
    data class OnCommentUpdated(val comment: String) : CommunityPostUiAction
    data class OnSendCommentClick(val postId: Int, val content: String, val replyTo: Int) : CommunityPostUiAction
    data class OnDeletePostClick(val postId: Int) : CommunityPostUiAction
    data class OnDeleteCommentClick(val commentId: Int) : CommunityPostUiAction
    data class OnReportPostClick(val postId: Int, val reason: ReportReason) : CommunityPostUiAction
    data class OnReportCommentClick(val commentId: Int, val reason: ReportReason) : CommunityPostUiAction
}
