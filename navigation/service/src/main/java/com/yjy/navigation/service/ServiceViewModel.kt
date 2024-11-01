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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val isLoggedIn: Flow<Boolean> = authRepository.isLoggedIn
    private val isSessionTokenAvailable: Flow<Boolean> = authRepository.isSessionTokenAvailable

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false,
        )

    val sessionExpireEvent = combine(
        isLoggedIn,
        isSessionTokenAvailable,
    ) { loggedIn, tokenAvailable ->
        loggedIn && !tokenAvailable
    }.onEach { isExpired ->
        if (isExpired) authRepository.logout()
    }.shareIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        replay = 1,
    )
}
