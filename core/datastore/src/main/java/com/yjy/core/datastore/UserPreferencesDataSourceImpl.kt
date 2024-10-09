package com.yjy.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import javax.inject.Inject

internal class UserPreferencesDataSourceImpl @Inject constructor(
    @UserPreferencesDataStore private val dataStore: DataStore<Preferences>,
) : UserPreferencesDataSource {

    override suspend fun isLoggedIn(): Boolean = dataStore.readValue(KEY_LOGIN_STATUS, false)

    override suspend fun setLoggedIn(loggedIn: Boolean) {
        dataStore.storeValue(KEY_LOGIN_STATUS, loggedIn)
    }

    companion object {
        private val KEY_LOGIN_STATUS = booleanPreferencesKey("login_status")
    }
}