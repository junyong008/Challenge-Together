package com.yjy.navigation.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.data.auth.api.AppLockRepository
import com.yjy.data.auth.api.AuthRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.domain.LogoutUseCase
import com.yjy.platform.network.NetworkMonitor
import com.yjy.platform.time.TimeMonitor
import com.yjy.platform.worker.manager.WorkerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val userRepository: UserRepository,
    authRepository: AuthRepository,
    appLockRepository: AppLockRepository,
    logoutUseCase: LogoutUseCase,
    networkMonitor: NetworkMonitor,
    workerManager: WorkerManager,
    timeMonitor: TimeMonitor,
) : ViewModel() {

    val isOffline = networkMonitor.isOnline
        .onEach { isOnline ->
            if (isOnline) userRepository.registerFcmToken()
        }
        .map(Boolean::not)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    val isManualTime = timeMonitor.isAutoTime
        .map(Boolean::not)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    val isPinSet = appLockRepository.isPinSet

    val isLoggedIn = authRepository.isLoggedIn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true,
        )

    private val _sessionExpiredEvent = Channel<Unit>()
    val sessionExpiredEvent = _sessionExpiredEvent.receiveAsFlow()

    val isSessionExpired = combine(
        authRepository.isLoggedIn,
        authRepository.isSessionTokenAvailable,
    ) { loggedIn, tokenAvailable ->
        loggedIn && !tokenAvailable
    }.onEach { isExpired ->
        if (isExpired) {
            _sessionExpiredEvent.send(Unit)
            workerManager.stopPeriodicCheck()
            logoutUseCase()
        } else {
            workerManager.startPeriodicCheck()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false,
    )
}
