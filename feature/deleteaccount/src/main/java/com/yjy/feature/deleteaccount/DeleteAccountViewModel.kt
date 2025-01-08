package com.yjy.feature.deleteaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.auth.api.AuthRepository
import com.yjy.domain.LogoutUseCase
import com.yjy.feature.deleteaccount.model.DeleteAccountUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAccountViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _isDeletingAccount = MutableStateFlow(false)
    val isDeletingAccount: StateFlow<Boolean> = _isDeletingAccount.asStateFlow()

    private val _uiEvent = Channel<DeleteAccountUiEvent>()
    val uiEvent: Flow<DeleteAccountUiEvent> = _uiEvent.receiveAsFlow()

    val countDown = flow {
        for (count in COUNTDOWN_START downTo 1) {
            emit(count)
            delay(COUNTDOWN_DELAY_MS)
        }
        emit(null)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = COUNTDOWN_START,
    )

    private fun sendEvent(event: DeleteAccountUiEvent) {
        viewModelScope.launch { _uiEvent.send(event) }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _isDeletingAccount.value = true
            authRepository.deleteAccount()
                .onSuccess {
                    sendEvent(DeleteAccountUiEvent.Success)
                    logoutUseCase()
                }
                .onFailure {
                    sendEvent(DeleteAccountUiEvent.Failure)
                }

            _isDeletingAccount.value = false
        }
    }

    companion object {
        private const val COUNTDOWN_START = 3
        private const val COUNTDOWN_DELAY_MS = 1000L
    }
}
