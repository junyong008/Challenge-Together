package com.yjy.feature.challengereward.model

import com.yjy.model.challenge.reward.RewardInfo

sealed interface RewardInfoUiState {
    data class Success(val rewardInfo: RewardInfo?) : RewardInfoUiState
    data object Loading : RewardInfoUiState
    data object Error : RewardInfoUiState
}

fun RewardInfoUiState.isSuccess() = this is RewardInfoUiState.Success
fun RewardInfoUiState.getRewardInfo() = (this as? RewardInfoUiState.Success)?.rewardInfo
