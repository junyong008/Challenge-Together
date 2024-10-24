package com.yjy.navigation.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.data.auth.api.AuthRepository
import com.yjy.platform.network.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    private val isLoggedIn: Flow<Boolean> = authRepository.isLoggedIn
    private val isSessionTokenAvailable: Flow<Boolean> = authRepository.isSessionTokenAvailable

    val sessionExpireEvent: Flow<Boolean> = combine(
        isLoggedIn,
        isSessionTokenAvailable,
    ) { loggedIn, tokenAvailable ->
        SessionStatus(loggedIn, tokenAvailable)
    }.transform { status ->
        emit(isSessionExpired(status))
    }

    private suspend fun isSessionExpired(status: SessionStatus): Boolean {
        return if (status.isLoggedIn && !status.isTokenValid) {
            authRepository.logout()
            true
        } else {
            false
        }
    }

    private data class SessionStatus(
        val isLoggedIn: Boolean,
        val isTokenValid: Boolean,
    )
}
