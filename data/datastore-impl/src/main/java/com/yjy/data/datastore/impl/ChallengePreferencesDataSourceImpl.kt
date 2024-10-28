package com.yjy.data.datastore.impl

import androidx.datastore.core.DataStore
import com.yjy.data.datastore.api.ChallengePreferencesDataSource
import com.yjy.data.datastore.proto.ChallengePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ChallengePreferencesDataSourceImpl @Inject constructor(
    @ChallengePreferencesDataStore private val dataStore: DataStore<ChallengePreferences>,
) : ChallengePreferencesDataSource {

    override val timeDiff: Flow<Long> = dataStore.data
        .map { preferences ->
            preferences.timeDiff
        }

    override val completedChallengeTitles: Flow<List<String>> = dataStore.data
        .map { preferences ->
            preferences.completedChallengeTitlesList
        }

    override suspend fun setTimeDiff(diff: Long) {
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setTimeDiff(diff)
                .build()
        }
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
}
