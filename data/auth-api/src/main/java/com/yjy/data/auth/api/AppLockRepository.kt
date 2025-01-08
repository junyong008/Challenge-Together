package com.yjy.data.auth.api

import kotlinx.coroutines.flow.Flow

interface AppLockRepository {
    val isPinSet: Flow<Boolean>
    val isBiometricEnabled: Flow<Boolean>
    val shouldHideWidgetContents: Flow<Boolean>
    val shouldHideNotificationContents: Flow<Boolean>
    suspend fun savePin(pin: String)
    suspend fun removePin()
    suspend fun validatePin(inputPin: String): Boolean
    suspend fun setBiometricEnabled(enabled: Boolean)
    suspend fun setHideWidgetContents(enabled: Boolean)
    suspend fun setHideNotificationContents(enabled: Boolean)
}
