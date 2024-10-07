package com.yjy.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject

internal class SessionDataSourceImpl @Inject constructor(
    @SessionDataStore private val dataStore: DataStore<Preferences>,
) : SessionDataSource {

    override suspend fun getToken(): String? = dataStore.readValue(KEY_TOKEN)

    override suspend fun setToken(token: String?) {
        dataStore.storeValue(KEY_TOKEN, token)
    }

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("session_token")
    }
}
