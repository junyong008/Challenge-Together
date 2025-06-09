package com.yjy.navigation.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.network.onSuccess
import com.yjy.data.auth.api.AppLockRepository
import com.yjy.data.auth.api.AuthRepository
import com.yjy.data.challenge.api.StartedChallengeRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.domain.LogoutUseCase
import com.yjy.model.common.Ban
import com.yjy.platform.network.NetworkMonitor
import com.yjy.platform.time.TimeMonitor
import com.yjy.platform.worker.manager.WorkerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val startedChallengeRepository: StartedChallengeRepository,
    authRepository: AuthRepository,
    appLockRepository: AppLockRepository,
    logoutUseCase: LogoutUseCase,
    networkMonitor: NetworkMonitor,
    workerManager: WorkerManager,
    timeMonitor: TimeMonitor,
) : ViewModel() {

    private val _banStatus = MutableStateFlow<Ban?>(null)
    val banStatus: StateFlow<Ban?> = _banStatus.asStateFlow()

    val isPremium = userRepository.isPremium
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    val themeState = userRepository.isDarkTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    val isOffline = networkMonitor.isOnline
        .onEach { isOnline ->
            if (isOnline) {
                userRepository.checkPremium()
                userRepository.registerFcmToken()
            }
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

    val remoteAppVersion = userRepository.remoteAppVersion
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    val maintenanceEndTime = userRepository.maintenanceEndTime
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
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

    fun checkBan(identifier: String) {
        viewModelScope.launch {
            userRepository.checkBan(identifier)
                .onSuccess { _banStatus.value = it }
        }
    }

    suspend fun shouldShowPremiumDialog(): Boolean {
        val lastShownTime = userRepository.premiumDialogLastShown.first()
        val startedChallenges = startedChallengeRepository.startedChallenges.first()
        val isPremium = userRepository.isPremium.first()

        val now = System.currentTimeMillis()
        val diffDays = (now - lastShownTime) / MILLIS_IN_A_DAY

        if (lastShownTime == 0L) {
            val delayUntilFirstDialogDays = PREMIUM_DIALOG_INTERVAL_DAYS - FIRST_PREMIUM_DIALOG_DELAY_DAYS
            val delayInMillis = delayUntilFirstDialogDays * MILLIS_IN_A_DAY
            val initialShownTime = now - delayInMillis
            userRepository.setPremiumDialogLastShown(initialShownTime)
            return false
        }

        return diffDays >= PREMIUM_DIALOG_INTERVAL_DAYS && startedChallenges.isNotEmpty() && !isPremium
    }

    fun markPremiumDialogShown() {
        viewModelScope.launch { userRepository.setPremiumDialogLastShown(System.currentTimeMillis()) }
    }

    companion object {
        private const val PREMIUM_DIALOG_INTERVAL_DAYS = 7L
        private const val FIRST_PREMIUM_DIALOG_DELAY_DAYS = 1L
        private const val MILLIS_IN_A_DAY = 24 * 60 * 60 * 1000
    }
}
