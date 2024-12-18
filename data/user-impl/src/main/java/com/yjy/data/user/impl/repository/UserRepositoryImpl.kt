package com.yjy.data.user.impl.repository

import com.yjy.common.network.NetworkResult
import com.yjy.common.network.map
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.datastore.api.UserPreferencesDataSource
import com.yjy.data.network.datasource.UserDataSource
import com.yjy.data.network.request.user.ChangeUserNameRequest
import com.yjy.data.network.request.user.RegisterFirebaseTokenRequest
import com.yjy.data.user.api.FcmTokenProvider
import com.yjy.data.user.api.UserRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val userDataSource: UserDataSource,
    private val fcmTokenProvider: FcmTokenProvider,
) : UserRepository {

    override val timeDiff: Flow<Long> = userPreferencesDataSource.timeDiff

    override suspend fun syncTime(): NetworkResult<Unit> = userDataSource.syncTime()

    override suspend fun getUserName(): NetworkResult<String> =
        userDataSource.getUserName().map { it.userName }

    override suspend fun getRemainSecondsForChangeName(): NetworkResult<Long> =
        userDataSource.getRemainTimeForChangeName().map { it.remainSecondsForChangeName }

    override suspend fun changeUserName(name: String): NetworkResult<Unit> =
        userDataSource.changeUserName(ChangeUserNameRequest(name))

    override suspend fun registerFcmToken() {
        val currentToken: String = fcmTokenProvider.getToken()
        val lastRegisteredToken: String? = userPreferencesDataSource.getFcmToken()

        if (currentToken != lastRegisteredToken) {
            userDataSource.registerFirebaseToken(RegisterFirebaseTokenRequest(currentToken))
                .onSuccess { userPreferencesDataSource.setFcmToken(currentToken) }
                .onFailure { Timber.d("registerFcmToken() failed") }
        }
    }

    override suspend fun clearLocalData() {
        userPreferencesDataSource.setFcmToken(null)
    }
}
