package com.yjy.challengetogether.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.data.auth.api.AuthRepository
import com.yjy.data.user.api.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    suspend fun getIsLoggedIn(): Boolean = authRepository.getIsLoggedIn()

    val themeState = userRepository.isDarkTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )
}
