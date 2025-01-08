package com.yjy.feature.changename.model

sealed interface UserNameUiState {
    data class Success(val name: String) : UserNameUiState
    data object Error : UserNameUiState
    data object Loading : UserNameUiState
}

fun UserNameUiState.isError(): Boolean = this is UserNameUiState.Error
fun UserNameUiState.isLoading(): Boolean = this is UserNameUiState.Loading
fun UserNameUiState.getNameOrNull(): String? = (this as? UserNameUiState.Success)?.name
