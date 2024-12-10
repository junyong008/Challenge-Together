package com.yjy.data.datastore.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
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

    override suspend fun getMutedChallengeBoards(): List<Int> =
        mutedChallengeBoards.first()

    override suspend fun getMutedCommunityPosts(): List<Int> =
        mutedCommunityPosts.first()

    override suspend fun addMutedChallengeBoard(challengeId: Int) {
        val currentList = getMutedChallengeBoards()
        if (!currentList.contains(challengeId)) {
            val updatedList = currentList + challengeId
            val json = Json.encodeToString(updatedList)
            dataStore.storeValue(KEY_MUTED_CHALLENGE_BOARDS, json)
        }
    }

    override suspend fun addMutedCommunityPost(postId: Int) {
        val currentList = getMutedCommunityPosts()
        if (!currentList.contains(postId)) {
            val updatedList = currentList + postId
            val json = Json.encodeToString(updatedList)
            dataStore.storeValue(KEY_MUTED_COMMUNITY_POSTS, json)
        }
    }

    override suspend fun removeMutedChallengeBoard(challengeId: Int) {
        val currentList = getMutedChallengeBoards()
        if (currentList.contains(challengeId)) {
            val updatedList = currentList - challengeId
            val json = Json.encodeToString(updatedList)
            dataStore.storeValue(KEY_MUTED_CHALLENGE_BOARDS, json)
        }
    }

    override suspend fun removeMutedCommunityPost(postId: Int) {
        val currentList = getMutedCommunityPosts()
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
        private val KEY_MUTED_CHALLENGE_BOARDS = stringPreferencesKey("muted_challenge_boards")
        private val KEY_MUTED_COMMUNITY_POSTS = stringPreferencesKey("muted_community_posts")
    }
}
