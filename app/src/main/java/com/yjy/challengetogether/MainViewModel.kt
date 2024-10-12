package com.yjy.challengetogether

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.data.auth.api.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val isLoggedIn: Flow<Boolean> = authRepository.isLoggedIn
    private val isSessionTokenAvailable: Flow<Boolean> = authRepository.isSessionTokenAvailable

    private val _uiEvent = Channel<MainUiEvent>()
    val uiEvent: Flow<MainUiEvent> = _uiEvent.receiveAsFlow()

    val uiState: StateFlow<MainUiState> = isLoggedIn.map {
        MainUiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainUiState.Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )

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
            _uiEvent.send(MainUiEvent.SessionExpired)
            authRepository.logout()
        }
    }

    private data class SessionStatus(
        val isLoggedIn: Boolean,
        val isTokenValid: Boolean
    )
}

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val isLoggedIn: Boolean) : MainUiState
}

sealed interface MainUiEvent {
    data object SessionExpired : MainUiEvent
}
