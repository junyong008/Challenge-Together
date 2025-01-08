package com.yjy.data.user.impl.repository

import com.yjy.common.network.NetworkResult
import com.yjy.common.network.map
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.datastore.api.UserPreferencesDataSource
import com.yjy.data.network.datasource.UserDataSource
import com.yjy.data.network.request.user.ChangeUserNameRequest
import com.yjy.data.network.request.user.LinkAccountRequest
import com.yjy.data.network.request.user.RegisterFirebaseTokenRequest
import com.yjy.data.network.request.user.UpgradePremiumRequest
import com.yjy.data.user.api.FcmTokenProvider
import com.yjy.data.user.api.UserRepository
import com.yjy.data.user.impl.mapper.toLocalDateTimeOrNull
import com.yjy.data.user.impl.mapper.toModel
import com.yjy.model.common.Ban
import com.yjy.model.common.Version
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.security.MessageDigest
import java.time.LocalDateTime
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val userDataSource: UserDataSource,
    private val fcmTokenProvider: FcmTokenProvider,
) : UserRepository {

    override val timeDiff: Flow<Long> = userPreferencesDataSource.timeDiff
    override val isPremium: Flow<Boolean> = userPreferencesDataSource.isPremium

    override val remoteAppVersion: Flow<Version> =
        userDataSource.getRemoteAppVersion().map { Version(it) }

    override val maintenanceEndTime: Flow<LocalDateTime?> =
        userDataSource.getMaintenanceEndTime()
            .map { it.toLocalDateTimeOrNull() }
            .combine(timeDiff) { endTime, timeDiff ->
                endTime?.plusSeconds(timeDiff)
            }

    override suspend fun syncTime(): NetworkResult<Unit> = userDataSource.syncTime()

    override suspend fun getUserName(): NetworkResult<String> =
        userDataSource.getUserName().map { it.userName }

    override suspend fun getAccountType(): NetworkResult<com.yjy.model.common.AccountType> =
        userDataSource.getAccountType().map { it.toModel() }

    override suspend fun checkBan(identifier: String): NetworkResult<Ban?> {
        val hashedIdentifier = hashIdentifier(identifier)
        return userDataSource.checkBan(hashedIdentifier)
            .map { it?.toModel() }
            .map { it?.copy(endAt = it.endAt.plusSeconds(timeDiff.first())) }
    }

    override suspend fun getRemainSecondsForChangeName(): NetworkResult<Long> =
        userDataSource.getRemainTimeForChangeName().map { it.remainSecondsForChangeName }

    override suspend fun changeUserName(name: String): NetworkResult<Unit> =
        userDataSource.changeUserName(ChangeUserNameRequest(name))

    override suspend fun linkAccount(kakaoId: String, googleId: String, naverId: String): NetworkResult<Unit> =
        userDataSource.linkAccount(
            request = LinkAccountRequest(
                kakaoId = kakaoId,
                googleId = googleId,
                naverId = naverId,
            ),
        )

    override suspend fun upgradeToPremium(purchaseToken: String): NetworkResult<Unit> =
        userDataSource.upgradePremium(UpgradePremiumRequest(purchaseToken))
            .onSuccess { userPreferencesDataSource.setIsPremium(true) }

    override suspend fun registerFcmToken() {
        val currentToken: String = fcmTokenProvider.getToken()
        val lastRegisteredToken: String? = userPreferencesDataSource.getFcmToken()

        if (currentToken != lastRegisteredToken) {
            userDataSource.registerFirebaseToken(RegisterFirebaseTokenRequest(currentToken))
                .onSuccess { userPreferencesDataSource.setFcmToken(currentToken) }
                .onFailure { Timber.d("registerFcmToken() failed") }
        }
    }

    override suspend fun checkPremium() {
        userDataSource.checkPremium()
            .onSuccess { userPreferencesDataSource.setIsPremium(it.isPremium) }
            .onFailure { Timber.d("checkPremium() failed") }
    }

    override suspend fun clearLocalData() {
        userPreferencesDataSource.setFcmToken(null)
        userPreferencesDataSource.setIsPremium(false)
    }

    private fun hashIdentifier(identifier: String): String {
        val bytes = identifier.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val hashedBytes = md.digest(bytes)
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }
}
