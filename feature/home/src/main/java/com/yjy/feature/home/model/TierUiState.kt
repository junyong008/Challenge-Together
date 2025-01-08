package com.yjy.feature.home.model

import com.yjy.model.common.Tier

sealed interface TierUiState {
    data class Success(val tier: Tier) : TierUiState
    data object Error : TierUiState
    data object Loading : TierUiState
}

fun TierUiState.isSuccess(): Boolean = this is TierUiState.Success
fun TierUiState.isError(): Boolean = this is TierUiState.Error
fun TierUiState.isLoading(): Boolean = this is TierUiState.Loading
fun TierUiState.getTierOrDefault(default: Tier = Tier.UNSPECIFIED): Tier {
    return (this as? TierUiState.Success)?.tier ?: default
}
