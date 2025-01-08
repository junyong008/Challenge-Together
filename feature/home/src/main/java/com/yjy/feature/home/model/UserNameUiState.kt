package com.yjy.feature.home.model

sealed interface UserNameUiState {
    data class Success(val name: String) : UserNameUiState
    data object Error : UserNameUiState
    data object Loading : UserNameUiState
}

fun UserNameUiState.isSuccess(): Boolean = this is UserNameUiState.Success
fun UserNameUiState.isError(): Boolean = this is UserNameUiState.Error
fun UserNameUiState.isLoading(): Boolean = this is UserNameUiState.Loading
fun UserNameUiState.getNameOrDefault(default: String = ""): String {
    return (this as? UserNameUiState.Success)?.name ?: default
}
