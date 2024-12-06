package com.yjy.navigation.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.data.auth.api.AuthRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.domain.LogoutUseCase
import com.yjy.platform.network.NetworkMonitor
import com.yjy.platform.time.TimeMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val userRepository: UserRepository,
    authRepository: AuthRepository,
    logoutUseCase: LogoutUseCase,
    networkMonitor: NetworkMonitor,
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

    val sessionExpireEvent = combine(
        authRepository.isLoggedIn,
        authRepository.isSessionTokenAvailable,
    ) { loggedIn, tokenAvailable ->
        loggedIn && !tokenAvailable
    }.onEach { isExpired ->
        if (isExpired) logoutUseCase()
    }.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1,
    )
}
