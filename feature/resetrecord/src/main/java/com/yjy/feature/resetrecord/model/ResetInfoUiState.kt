package com.yjy.feature.resetrecord.model

import com.yjy.model.challenge.ResetInfo

sealed interface ResetInfoUiState {
    data class Success(val resetInfo: ResetInfo) : ResetInfoUiState
    data object Loading : ResetInfoUiState
    data object Error : ResetInfoUiState
}

fun ResetInfoUiState.getOrNull(): ResetInfo? {
    return if (this is ResetInfoUiState.Success) {
        resetInfo
    } else {
        null
    }
}
