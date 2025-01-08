package com.yjy.feature.my.model

import com.yjy.model.challenge.UserRecord

sealed interface RecordUiState {
    data class Success(val record: UserRecord) : RecordUiState
    data object Error : RecordUiState
    data object Loading : RecordUiState
}

fun RecordUiState.isError(): Boolean = this is RecordUiState.Error
fun RecordUiState.isLoading(): Boolean = this is RecordUiState.Loading
fun RecordUiState.getRecordOrNull(): UserRecord? {
    return (this as? RecordUiState.Success)?.record
}
