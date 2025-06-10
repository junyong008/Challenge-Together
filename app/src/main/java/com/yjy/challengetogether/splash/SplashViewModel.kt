package com.yjy.challengetogether.splash

import androidx.lifecycle.ViewModel
import com.yjy.data.auth.api.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    suspend fun getIsLoggedIn(): Boolean = authRepository.getIsLoggedIn()
}
