package com.yjy.data.datastore.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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

    override suspend fun getToken(): String? = dataStore.readValue(KEY_TOKEN)

    override suspend fun setToken(token: String?) {
        dataStore.storeValue(KEY_TOKEN, token)
    }

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("session_token")
    }
}
