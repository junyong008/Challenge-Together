package com.yjy.data.datastore.impl

import androidx.datastore.core.DataStore
import com.yjy.data.datastore.api.ChallengePreferences
import com.yjy.data.datastore.api.ChallengePreferencesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ChallengePreferencesDataSourceImpl @Inject constructor(
    @ChallengePreferencesDataStore private val dataStore: DataStore<ChallengePreferences>,
) : ChallengePreferencesDataSource {

    override val completedChallengeTitles: Flow<List<String>> = dataStore.data
        .map { preferences ->
            preferences.completedChallengeTitlesList
        }

    override val currentTier: Flow<ChallengePreferences.Tier> = dataStore.data
        .map { preferences ->
            preferences.tier
        }

    override val sortOrder: Flow<ChallengePreferences.SortOrder> = dataStore.data
        .map { preferences ->
            preferences.sortOrder
        }

    override suspend fun setCompletedChallengeTitles(titles: List<String>) {
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .clearCompletedChallengeTitles()
                .addAllCompletedChallengeTitles(titles)
                .build()
        }
    }

    override suspend fun addCompletedChallengeTitles(newTitles: List<String>) {
        dataStore.updateData { preferences ->
            val existingTitles = preferences.completedChallengeTitlesList
            preferences.toBuilder()
                .clearCompletedChallengeTitles()
                .addAllCompletedChallengeTitles(existingTitles + newTitles)
                .build()
        }
    }

    override suspend fun setCurrentTier(tier: ChallengePreferences.Tier) {
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setTier(tier)
                .build()
        }
    }

    override suspend fun setSortOrder(order: ChallengePreferences.SortOrder) {
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setSortOrder(order)
                .build()
        }
    }
}
