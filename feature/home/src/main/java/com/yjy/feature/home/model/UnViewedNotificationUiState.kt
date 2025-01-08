package com.yjy.feature.home.model

sealed interface UnViewedNotificationUiState {
    data class Success(val count: Int) : UnViewedNotificationUiState
    data object Error : UnViewedNotificationUiState
    data object Loading : UnViewedNotificationUiState
}

fun UnViewedNotificationUiState.isSuccess(): Boolean = this is UnViewedNotificationUiState.Success
fun UnViewedNotificationUiState.isError(): Boolean = this is UnViewedNotificationUiState.Error
fun UnViewedNotificationUiState.isLoading(): Boolean = this is UnViewedNotificationUiState.Loading
fun UnViewedNotificationUiState.hasNewNotification(): Boolean =
    this is UnViewedNotificationUiState.Success && this.count > 0
