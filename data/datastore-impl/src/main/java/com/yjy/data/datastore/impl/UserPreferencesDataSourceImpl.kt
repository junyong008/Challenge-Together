package com.yjy.data.datastore.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.yjy.data.datastore.api.UserPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserPreferencesDataSourceImpl @Inject constructor(
    @UserPreferencesDataStore private val dataStore: DataStore<Preferences>,
) : UserPreferencesDataSource {

    override val isLoggedIn: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[KEY_LOGIN_STATUS] ?: false
        }

    override suspend fun getLoggedIn(): Boolean =
        dataStore.readValue(KEY_LOGIN_STATUS, false)

    override suspend fun setLoggedIn(loggedIn: Boolean) {
        dataStore.storeValue(KEY_LOGIN_STATUS, loggedIn)
    }

    companion object {
        private val KEY_LOGIN_STATUS = booleanPreferencesKey("login_status")
    }
}
