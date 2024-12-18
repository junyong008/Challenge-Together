package com.yjy.feature.changename.model

sealed interface RemainSecondForChangeUiState {
    data class Success(val value: Long) : RemainSecondForChangeUiState
    data object Error : RemainSecondForChangeUiState
    data object Loading : RemainSecondForChangeUiState
}

fun RemainSecondForChangeUiState.isError(): Boolean = this is RemainSecondForChangeUiState.Error
fun RemainSecondForChangeUiState.isLoading(): Boolean = this is RemainSecondForChangeUiState.Loading
fun RemainSecondForChangeUiState.getValueOrNull(): Long? = (this as? RemainSecondForChangeUiState.Success)?.value
