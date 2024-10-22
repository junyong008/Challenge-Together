package com.yjy.navigation.service

import androidx.lifecycle.ViewModel
import com.yjy.data.auth.api.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val isLoggedIn: Flow<Boolean> = authRepository.isLoggedIn
    private val isSessionTokenAvailable: Flow<Boolean> = authRepository.isSessionTokenAvailable

    val uiEvent: Flow<ServiceUiEvent> = combine(
        isLoggedIn,
        isSessionTokenAvailable,
    ) { loggedIn, tokenAvailable ->
        SessionStatus(loggedIn, tokenAvailable)
    }.transform { status ->
        handleSessionStatus(status)?.let { emit(it) }
    }

    private suspend fun handleSessionStatus(status: SessionStatus): ServiceUiEvent? {
        return if (status.isLoggedIn && !status.isTokenValid) {
            authRepository.logout()
            ServiceUiEvent.SessionExpired
        } else {
            null
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
