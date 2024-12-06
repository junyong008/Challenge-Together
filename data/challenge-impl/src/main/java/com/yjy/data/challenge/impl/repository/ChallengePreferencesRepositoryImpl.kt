package com.yjy.data.challenge.impl.repository

import com.yjy.common.network.NetworkResult
import com.yjy.common.network.map
import com.yjy.data.challenge.api.ChallengePreferencesRepository
import com.yjy.data.challenge.impl.mapper.toModel
import com.yjy.data.challenge.impl.mapper.toProto
import com.yjy.data.challenge.impl.util.TimeManager
import com.yjy.data.datastore.api.ChallengePreferencesDataSource
import com.yjy.data.network.datasource.ChallengeDataSource
import com.yjy.model.challenge.core.SortOrder
import com.yjy.model.common.Tier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ChallengePreferencesRepositoryImpl @Inject constructor(
    private val challengePreferencesDataSource: ChallengePreferencesDataSource,
    private val challengeDataSource: ChallengeDataSource,
    timeManager: TimeManager,
) : ChallengePreferencesRepository {

    override val timeChangedFlow: Flow<Unit> = timeManager.timeChangedFlow

    override val localTier: Flow<Tier> =
        challengePreferencesDataSource.currentTier.map { it.toModel() }

    override val sortOrder: Flow<SortOrder> =
        challengePreferencesDataSource.sortOrder.map { it.toModel() }

    override val recentCompletedChallengeTitles: Flow<List<String>> =
        challengePreferencesDataSource.completedChallengeTitles

    override suspend fun getRemoteTier(): NetworkResult<Tier> =
        challengeDataSource.getRecords().map { Tier.getCurrentTier(it.recordInSeconds) }

    override suspend fun setLocalTier(tier: Tier) =
        challengePreferencesDataSource.setCurrentTier(tier.toProto())

    override suspend fun setSortOrder(order: SortOrder) =
        challengePreferencesDataSource.setSortOrder(order.toProto())

    override suspend fun clearRecentCompletedChallenges() =
        challengePreferencesDataSource.setCompletedChallengeTitles(emptyList())

    override suspend fun clearLocalData() {
        challengePreferencesDataSource.setCurrentTier(Tier.UNSPECIFIED.toProto())
    }
}
