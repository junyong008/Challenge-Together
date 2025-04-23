package com.yjy.feature.resetrecord.model

import com.yjy.model.challenge.ResetRecord

sealed interface ResetRecordsUiState {
    data class Success(val resetRecords: List<ResetRecord>) : ResetRecordsUiState
    data object Loading : ResetRecordsUiState
    data object Error : ResetRecordsUiState
}

fun ResetRecordsUiState.getOrNull(): List<ResetRecord>? {
    return if (this is ResetRecordsUiState.Success) {
        resetRecords
    } else {
        null
    }
}
