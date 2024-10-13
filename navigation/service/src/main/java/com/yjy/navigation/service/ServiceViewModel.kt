package com.yjy.navigation.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.data.auth.api.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val isLoggedIn: Flow<Boolean> = authRepository.isLoggedIn
    private val isSessionTokenAvailable: Flow<Boolean> = authRepository.isSessionTokenAvailable

    private val _uiEvent = Channel<ServiceUiEvent>()
    val uiEvent: Flow<ServiceUiEvent> = _uiEvent.receiveAsFlow()

    init {
        observeSessionValidation()
    }

    private fun observeSessionValidation() {
        viewModelScope.launch {
            combine(
                isLoggedIn,
                isSessionTokenAvailable,
            ) { loggedIn, tokenAvailable ->
                SessionStatus(loggedIn, tokenAvailable)
            }.collect { status ->
                handleSessionStatus(status)
            }
        }
    }

    private suspend fun handleSessionStatus(status: SessionStatus) {
        if (status.isLoggedIn && !status.isTokenValid) {
            _uiEvent.send(ServiceUiEvent.SessionExpired)
            authRepository.logout()
        }
    }

    private data class SessionStatus(
        val isLoggedIn: Boolean,
        val isTokenValid: Boolean,
    )
}

sealed interface ServiceUiEvent {
    data object SessionExpired : ServiceUiEvent
}
