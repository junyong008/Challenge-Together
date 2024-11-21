package com.yjy.feature.challengeboard.model

import com.yjy.model.common.ReportReason

sealed interface ChallengeBoardUiAction {
    data class OnSendClick(val content: String) : ChallengeBoardUiAction
    data class OnDeletePostClick(val postId: Int) : ChallengeBoardUiAction
    data class OnReportPostClick(val postId: Int, val reason: ReportReason) : ChallengeBoardUiAction
    data object OnRetryClick : ChallengeBoardUiAction
}
