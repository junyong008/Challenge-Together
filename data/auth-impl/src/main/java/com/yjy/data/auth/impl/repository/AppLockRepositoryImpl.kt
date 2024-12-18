package com.yjy.data.auth.impl.repository

import com.yjy.data.auth.api.AppLockRepository
import com.yjy.data.datastore.api.AppLockDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class AppLockRepositoryImpl @Inject constructor(
    private val appLockDataSource: AppLockDataSource,
) : AppLockRepository {

    override val isPinSet: Flow<Boolean> = appLockDataSource.isPinSet
    override val isBiometricEnabled: Flow<Boolean> = appLockDataSource.isBiometricEnabled
    override val shouldHideWidgetContents: Flow<Boolean> = appLockDataSource.shouldHideWidgetContents
    override val shouldHideNotificationContents: Flow<Boolean> = appLockDataSource.shouldHideNotificationContents

    override suspend fun savePin(pin: String) {
        appLockDataSource.savePin(pin)
    }

    override suspend fun removePin() {
        appLockDataSource.removePin()
    }

    override suspend fun validatePin(inputPin: String): Boolean =
        appLockDataSource.validatePin(inputPin)

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        appLockDataSource.setBiometricEnabled(enabled)
    }

    override suspend fun setHideWidgetContents(enabled: Boolean) {
        appLockDataSource.setHideWidgetContents(enabled)
    }

    override suspend fun setHideNotificationContents(enabled: Boolean) {
        appLockDataSource.setHideNotificationContents(enabled)
    }
}
