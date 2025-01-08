package com.yjy.domain

import com.yjy.data.auth.api.AppLockRepository
import com.yjy.data.auth.api.AuthRepository
import com.yjy.data.challenge.api.ChallengePostRepository
import com.yjy.data.challenge.api.ChallengePreferencesRepository
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.user.api.NotificationRepository
import com.yjy.data.user.api.UserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val appLockRepository: AppLockRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
    private val challengeRepository: ChallengeRepository,
    private val challengePreferencesRepository: ChallengePreferencesRepository,
    private val challengePostRepository: ChallengePostRepository,
) {
    suspend operator fun invoke() {
        challengePostRepository.clearLocalData()
        challengePreferencesRepository.clearLocalData()
        challengeRepository.clearLocalData()
        userRepository.clearLocalData()
        notificationRepository.clearLocalData()
        appLockRepository.removePin()
        authRepository.logout()
    }
}
