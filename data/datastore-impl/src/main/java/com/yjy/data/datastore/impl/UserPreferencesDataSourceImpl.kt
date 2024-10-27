package com.yjy.data.datastore.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
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

    override val userName: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[KEY_USER_NAME] ?: ""
        }

    override val unViewedNotificationCount: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[KEY_UN_VIEWED_NOTIFICATION_COUNT] ?: 0
        }

    override val completedChallengeTitles: Flow<List<String>> = dataStore.data
        .map { preferences ->
            preferences[KEY_COMPLETED_CHALLENGE_TITLES]
                ?.split(",")
                ?.filter { it.isNotBlank() }
                ?: emptyList()
        }

    override suspend fun setTimeDiff(diff: Long) {
        dataStore.storeValue(KEY_TIME_DIFF, diff)
    }

    override suspend fun setUserName(name: String?) {
        dataStore.storeValue(KEY_USER_NAME, name)
    }

    override suspend fun setUnViewedNotificationCount(count: Int?) {
        dataStore.storeValue(KEY_UN_VIEWED_NOTIFICATION_COUNT, count)
    }

    override suspend fun setCompletedChallengeTitles(titles: List<String>) {
        dataStore.storeValue(KEY_COMPLETED_CHALLENGE_TITLES, titles.joinToString(","))
    }

    override suspend fun addCompletedChallengeTitles(newTitles: List<String>) {
        val existingTitles = dataStore.readValue(KEY_COMPLETED_CHALLENGE_TITLES)
            ?.split(",")
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        val updatedTitles = (existingTitles + newTitles)
        dataStore.storeValue(KEY_COMPLETED_CHALLENGE_TITLES, updatedTitles.joinToString(","))
    }

    companion object {
        private val KEY_TIME_DIFF = longPreferencesKey("time_difference")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_UN_VIEWED_NOTIFICATION_COUNT = intPreferencesKey("un_viewed_notification_count")
        private val KEY_COMPLETED_CHALLENGE_TITLES = stringPreferencesKey("completed_challenge_titles")
    }
}
