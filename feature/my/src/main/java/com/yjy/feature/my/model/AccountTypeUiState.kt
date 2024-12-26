package com.yjy.feature.my.model

import com.yjy.model.common.AccountType

sealed interface AccountTypeUiState {
    data class Success(val type: AccountType) : AccountTypeUiState
    data object Error : AccountTypeUiState
    data object Loading : AccountTypeUiState
}

fun AccountTypeUiState.isError(): Boolean = this is AccountTypeUiState.Error
fun AccountTypeUiState.isLoading(): Boolean = this is AccountTypeUiState.Loading
fun AccountTypeUiState.getTypeOrNull(): AccountType? = (this as? AccountTypeUiState.Success)?.type
