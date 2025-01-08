package com.yjy.feature.linkaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.network.HttpStatusCodes.CONFLICT
import com.yjy.common.network.HttpStatusCodes.FORBIDDEN
import com.yjy.common.network.handleNetworkResult
import com.yjy.data.user.api.UserRepository
import com.yjy.feature.linkaccount.model.LinkAccountUiAction
import com.yjy.feature.linkaccount.model.LinkAccountUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LinkAccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _isLinkingAccount = MutableStateFlow(false)
    val isLinkingAccount: StateFlow<Boolean> = _isLinkingAccount.asStateFlow()

    private val _uiEvent = Channel<LinkAccountUiEvent>()
    val uiEvent: Flow<LinkAccountUiEvent> = _uiEvent.receiveAsFlow()

    private fun sendEvent(event: LinkAccountUiEvent) {
        viewModelScope.launch { _uiEvent.send(event) }
    }

    fun processAction(action: LinkAccountUiAction) {
        when (action) {
            is LinkAccountUiAction.OnLinkWithKakao -> linkAccount(kakaoId = action.kakaoId)
            is LinkAccountUiAction.OnLinkWithGoogle -> linkAccount(googleId = action.googleId)
            is LinkAccountUiAction.OnLinkWithNaver -> linkAccount(naverId = action.naverId)
            LinkAccountUiAction.OnLinkFailed -> sendEvent(LinkAccountUiEvent.Failure.UnknownError)
        }
    }

    private fun linkAccount(kakaoId: String = "", googleId: String = "", naverId: String = "") {
        viewModelScope.launch {
            _isLinkingAccount.value = true
            handleNetworkResult(
                result = userRepository.linkAccount(kakaoId, googleId, naverId),
                onSuccess = { LinkAccountUiEvent.Success },
                onHttpError = { code ->
                    when (code) {
                        CONFLICT -> LinkAccountUiEvent.Failure.AlreadyRegistered
                        FORBIDDEN -> LinkAccountUiEvent.Failure.AlreadyLinked
                        else -> LinkAccountUiEvent.Failure.UnknownError
                    }
                },
                onNetworkError = { LinkAccountUiEvent.Failure.NetworkError },
                onUnknownError = { LinkAccountUiEvent.Failure.UnknownError },
            ).also { sendEvent(it) }

            _isLinkingAccount.value = false
        }
    }
}
