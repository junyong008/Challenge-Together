package com.yjy.data.datastore.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yjy.data.datastore.api.UserPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserPreferencesDataSourceImpl @Inject constructor(
    @UserPreferencesDataStore private val dataStore: DataStore<Preferences>,
) : UserPreferencesDataSource {

    override val timeDiff: Flow<Long> = dataStore.data
        .map { preferences ->
            preferences[KEY_TIME_DIFF] ?: 0
        }

    override val isPremium: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[KEY_IS_PREMIUM] ?: false
        }

    override val premiumDialogLastShown: Flow<Long> = dataStore.data
        .map { preferences ->
            preferences[KEY_PREMIUM_DIALOG_LAST_SHOWN] ?: 0L
        }

    override val darkThemePreference: Flow<Boolean?> = dataStore.data
        .map { preferences ->
            preferences[KEY_DARK_THEME_PREFERENCE]
        }

    override suspend fun setTimeDiff(diff: Long) {
        dataStore.storeValue(KEY_TIME_DIFF, diff)
    }

    override suspend fun setIsPremium(isPremium: Boolean) {
        dataStore.storeValue(KEY_IS_PREMIUM, isPremium)
    }

    override suspend fun getFcmToken(): String? = dataStore.readValue(KEY_FCM_TOKEN)

    override suspend fun setFcmToken(token: String?) {
        dataStore.storeValue(KEY_FCM_TOKEN, token)
    }

    override suspend fun setPremiumDialogLastShown(timestamp: Long) {
        dataStore.storeValue(KEY_PREMIUM_DIALOG_LAST_SHOWN, timestamp)
    }

    override suspend fun setDarkThemePreference(value: Boolean?) {
        dataStore.storeValue(KEY_DARK_THEME_PREFERENCE, value)
    }

    companion object {
        private val KEY_TIME_DIFF = longPreferencesKey("time_diff")
        private val KEY_IS_PREMIUM = booleanPreferencesKey("is_premium")
        private val KEY_FCM_TOKEN = stringPreferencesKey("fcm_token")
        private val KEY_PREMIUM_DIALOG_LAST_SHOWN = longPreferencesKey("premium_dialog_last_shown")
        private val KEY_DARK_THEME_PREFERENCE = booleanPreferencesKey("dark_theme_preference")
    }
}
