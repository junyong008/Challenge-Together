package com.yjy.data.datastore.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import com.yjy.data.datastore.api.UserPreferencesDataSource
import javax.inject.Inject

internal class UserPreferencesDataSourceImpl @Inject constructor(
    @UserPreferencesDataStore private val dataStore: DataStore<Preferences>,
) : UserPreferencesDataSource {

    override suspend fun getTimeDiff(): Long = dataStore.readValue(KEY_TIME_DIFF, 0)

    override suspend fun setTimeDiff(diff: Long) {
        dataStore.storeValue(KEY_TIME_DIFF, diff)
    }

    companion object {
        private val KEY_TIME_DIFF = longPreferencesKey("time_difference")
    }
}
