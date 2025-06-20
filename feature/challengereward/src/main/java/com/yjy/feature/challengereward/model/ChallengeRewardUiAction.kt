package com.yjy.feature.challengereward.model

import com.yjy.model.challenge.reward.Reward

sealed interface ChallengeRewardUiAction {
    data object OnRetryClick : ChallengeRewardUiAction
    data class OnAddRewardClick(val name: String, val price: Double, val url: String) : ChallengeRewardUiAction
    data class OnEditRewardClick(val reward: Reward) : ChallengeRewardUiAction
    data class OnPurchaseRewardClick(val reward: Reward) : ChallengeRewardUiAction
    data class OnCancelPurchaseRewardClick(val reward: Reward) : ChallengeRewardUiAction
    data class OnDeleteRewardClick(val reward: Reward) : ChallengeRewardUiAction
    data class OnResetClick(val challengeId: Int) : ChallengeRewardUiAction
    data class OnEditAmountClick(val challengeId: Int, val amount: Double) : ChallengeRewardUiAction
}
