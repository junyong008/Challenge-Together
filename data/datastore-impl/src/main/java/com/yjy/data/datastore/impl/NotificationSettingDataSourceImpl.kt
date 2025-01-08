package com.yjy.data.datastore.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yjy.data.datastore.api.NotificationSettingDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

internal class NotificationSettingDataSourceImpl @Inject constructor(
    @NotificationDataStore private val dataStore: DataStore<Preferences>,
) : NotificationSettingDataSource {

    override val notificationSettings: Flow<Int> = dataStore.data
        .catch { recoverOrThrow(it) }
        .map { preferences ->
            preferences[KEY_NOTIFICATION_SETTINGS] ?: ALL_NOTIFICATIONS_ENABLED
        }

    override val mutedChallengeBoards: Flow<List<Int>> = dataStore.data
        .catch { recoverOrThrow(it) }
        .map { preferences ->
            val jsonString = preferences[KEY_MUTED_CHALLENGE_BOARDS]
            if (jsonString.isNullOrEmpty()) {
                emptyList()
            } else {
                runCatching {
                    Json.decodeFromString<List<Int>>(jsonString)
                }.getOrDefault(emptyList())
            }
        }

    override val mutedCommunityPosts: Flow<List<Int>> = dataStore.data
        .catch { recoverOrThrow(it) }
        .map { preferences ->
            val jsonString = preferences[KEY_MUTED_COMMUNITY_POSTS]
            if (jsonString.isNullOrEmpty()) {
                emptyList()
            } else {
                runCatching {
                    Json.decodeFromString<List<Int>>(jsonString)
                }.getOrDefault(emptyList())
            }
        }

    override suspend fun setNotificationSetting(flag: Int, enabled: Boolean) {
        dataStore.edit { preferences ->
            val current = preferences[KEY_NOTIFICATION_SETTINGS] ?: ALL_NOTIFICATIONS_ENABLED
            val new = if (enabled) {
                current or flag
            } else {
                current and (flag.inv())
            }
            preferences[KEY_NOTIFICATION_SETTINGS] = new
        }
    }

    override suspend fun addMutedChallengeBoard(challengeId: Int) {
        val currentList = mutedChallengeBoards.first()
        if (!currentList.contains(challengeId)) {
            val updatedList = currentList + challengeId
            val json = Json.encodeToString(updatedList)
            dataStore.storeValue(KEY_MUTED_CHALLENGE_BOARDS, json)
        }
    }

    override suspend fun addMutedCommunityPost(postId: Int) {
        val currentList = mutedCommunityPosts.first()
        if (!currentList.contains(postId)) {
            val updatedList = currentList + postId
            val json = Json.encodeToString(updatedList)
            dataStore.storeValue(KEY_MUTED_COMMUNITY_POSTS, json)
        }
    }

    override suspend fun removeMutedChallengeBoard(challengeId: Int) {
        val currentList = mutedChallengeBoards.first()
        if (currentList.contains(challengeId)) {
            val updatedList = currentList - challengeId
            val json = Json.encodeToString(updatedList)
            dataStore.storeValue(KEY_MUTED_CHALLENGE_BOARDS, json)
        }
    }

    override suspend fun removeMutedCommunityPost(postId: Int) {
        val currentList = mutedCommunityPosts.first()
        if (currentList.contains(postId)) {
            val updatedList = currentList - postId
            val json = Json.encodeToString(updatedList)
            dataStore.storeValue(KEY_MUTED_COMMUNITY_POSTS, json)
        }
    }

    override suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private const val ALL_NOTIFICATIONS_ENABLED = 0xFF
        private val KEY_NOTIFICATION_SETTINGS = intPreferencesKey("notification_settings")
        private val KEY_MUTED_CHALLENGE_BOARDS = stringPreferencesKey("muted_challenge_boards")
        private val KEY_MUTED_COMMUNITY_POSTS = stringPreferencesKey("muted_community_posts")
    }
}
