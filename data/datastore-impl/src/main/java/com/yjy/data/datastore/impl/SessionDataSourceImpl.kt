package com.yjy.data.datastore.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yjy.data.datastore.api.SessionDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class SessionDataSourceImpl @Inject constructor(
    @SessionDataStore private val dataStore: DataStore<Preferences>,
) : SessionDataSource {

    override val isSessionTokenAvailable: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[KEY_TOKEN] != null
        }

    override val isLoggedIn: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[KEY_LOGIN_STATUS] ?: false
        }

    override suspend fun getToken(): String? = dataStore.readValue(KEY_TOKEN)

    override suspend fun setToken(token: String?) {
        dataStore.storeValue(KEY_TOKEN, token)
    }

    override suspend fun getLoggedIn(): Boolean =
        dataStore.readValue(KEY_LOGIN_STATUS, false)

    override suspend fun setLoggedIn(loggedIn: Boolean) {
        dataStore.storeValue(KEY_LOGIN_STATUS, loggedIn)
    }

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("session_token")
        private val KEY_LOGIN_STATUS = booleanPreferencesKey("login_status")
    }
}
