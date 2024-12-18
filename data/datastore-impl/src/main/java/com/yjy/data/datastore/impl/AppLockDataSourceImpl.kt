package com.yjy.data.datastore.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yjy.data.datastore.api.AppLockDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import javax.inject.Inject

internal class AppLockDataSourceImpl @Inject constructor(
    @AppLockDataStore private val dataStore: DataStore<Preferences>,
) : AppLockDataSource {

    override val isPinSet: Flow<Boolean> = dataStore.data
        .map { preferences ->
            !preferences[KEY_PIN].isNullOrEmpty()
        }

    override val isBiometricEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[KEY_BIOMETRIC_ENABLED] ?: false
        }

    override val shouldHideWidgetContents: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[KEY_HIDE_WIDGET] ?: false
        }

    override val shouldHideNotificationContents: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[KEY_HIDE_NOTIFICATION] ?: false
        }

    override suspend fun savePin(pin: String) {
        dataStore.storeValue(KEY_PIN, hashPin(pin))
    }

    override suspend fun removePin() {
        dataStore.storeValue(KEY_PIN, null)
        dataStore.storeValue(KEY_BIOMETRIC_ENABLED, false)
        dataStore.storeValue(KEY_HIDE_WIDGET, false)
        dataStore.storeValue(KEY_HIDE_NOTIFICATION, false)
    }

    override suspend fun validatePin(inputPin: String): Boolean =
        hashPin(inputPin) == dataStore.readValue(KEY_PIN)

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.storeValue(KEY_BIOMETRIC_ENABLED, enabled)
    }

    override suspend fun setHideWidgetContents(enabled: Boolean) {
        dataStore.storeValue(KEY_HIDE_WIDGET, enabled)
    }

    override suspend fun setHideNotificationContents(enabled: Boolean) {
        dataStore.storeValue(KEY_HIDE_NOTIFICATION, enabled)
    }

    private fun hashPin(pin: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(pin.toByteArray())
            .fold("") { str, byte -> str + "%02x".format(byte) }
    }

    companion object {
        private val KEY_PIN = stringPreferencesKey("pin")
        private val KEY_BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        private val KEY_HIDE_WIDGET = booleanPreferencesKey("hide_widget_contents")
        private val KEY_HIDE_NOTIFICATION = booleanPreferencesKey("hide_notification_contents")
    }
}
