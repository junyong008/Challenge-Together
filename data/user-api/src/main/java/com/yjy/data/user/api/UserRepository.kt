package com.yjy.data.user.api

import com.yjy.common.network.NetworkResult
import com.yjy.model.common.AccountType
import com.yjy.model.common.Ban
import com.yjy.model.common.Version
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface UserRepository {
    val timeDiff: Flow<Long>
    val isPremium: Flow<Boolean>
    val isDarkTheme: Flow<Boolean?>
    val remoteAppVersion: Flow<Version>
    val maintenanceEndTime: Flow<LocalDateTime?>
    val premiumDialogLastShown: Flow<Long>
    suspend fun syncTime(): NetworkResult<Unit>
    suspend fun getUserName(): NetworkResult<String>
    suspend fun getAccountType(): NetworkResult<AccountType>
    suspend fun checkBan(identifier: String): NetworkResult<Ban?>
    suspend fun getRemainSecondsForChangeName(): NetworkResult<Long>
    suspend fun changeUserName(name: String): NetworkResult<Unit>
    suspend fun linkAccount(kakaoId: String, googleId: String, naverId: String): NetworkResult<Unit>
    suspend fun upgradeToPremium(purchaseToken: String): NetworkResult<Unit>
    suspend fun registerFcmToken()
    suspend fun checkPremium()
    suspend fun clearLocalData()
    suspend fun setPremiumDialogLastShown(timeStamp: Long)
    suspend fun setDarkTheme(isDarkTheme: Boolean?)
}
