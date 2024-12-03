package com.yjy.data.challenge.impl.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.yjy.common.core.extensions.mapToUnit
import com.yjy.common.network.NetworkResult
import com.yjy.common.network.map
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.challenge.impl.mapper.toDetailedStartedChallengeModel
import com.yjy.data.challenge.impl.mapper.toEntity
import com.yjy.data.challenge.impl.mapper.toModel
import com.yjy.data.challenge.impl.mapper.toProto
import com.yjy.data.challenge.impl.mapper.toRequestString
import com.yjy.data.challenge.impl.mapper.toSimpleStartedChallengeModel
import com.yjy.data.challenge.impl.mapper.toSimpleWaitingChallengeModel
import com.yjy.data.challenge.impl.mediator.ChallengePostRemoteMediator
import com.yjy.data.challenge.impl.mediator.TogetherChallengeRemoteMediator
import com.yjy.data.challenge.impl.util.TimeManager
import com.yjy.data.database.dao.ChallengeDao
import com.yjy.data.database.dao.ChallengePostDao
import com.yjy.data.database.dao.TogetherChallengeDao
import com.yjy.data.database.model.ChallengeEntity
import com.yjy.data.database.model.ChallengePostEntity
import com.yjy.data.datastore.api.ChallengePreferencesDataSource
import com.yjy.data.network.datasource.ChallengeDataSource
import com.yjy.data.network.request.AddChallengePostRequest
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.data.network.request.EditChallengeTitleDescriptionRequest
import com.yjy.data.network.request.ReportChallengePostRequest
import com.yjy.data.network.request.ResetChallengeRequest
import com.yjy.model.challenge.ChallengePost
import com.yjy.model.challenge.ChallengeRank
import com.yjy.model.challenge.DetailedStartedChallenge
import com.yjy.model.challenge.ResetRecord
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.SimpleWaitingChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.SortOrder
import com.yjy.model.challenge.core.TargetDays
import com.yjy.model.common.ReportReason
import com.yjy.model.common.Tier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

internal class ChallengeRepositoryImpl @Inject constructor(
    private val challengePreferencesDataSource: ChallengePreferencesDataSource,
    private val challengeDataSource: ChallengeDataSource,
    private val togetherChallengeDao: TogetherChallengeDao,
    private val challengePostDao: ChallengePostDao,
    private val challengeDao: ChallengeDao,
    timeManager: TimeManager,
) : ChallengeRepository {

    private val tickerFlow: Flow<LocalDateTime> = timeManager.tickerFlow
    private val challenges: Flow<List<ChallengeEntity>> = challengeDao.getAll()

    override val timeChangedFlow: Flow<Unit> = timeManager.timeChangedFlow

    override val localTier: Flow<Tier> =
        challengePreferencesDataSource.currentTier.map { it.toModel() }

    override val sortOrder: Flow<SortOrder> =
        challengePreferencesDataSource.sortOrder.map { it.toModel() }

    override val recentCompletedChallengeTitles: Flow<List<String>> =
        challengePreferencesDataSource.completedChallengeTitles

    override val startedChallenges: Flow<List<SimpleStartedChallenge>> = challenges
        .map { entities ->
            entities
                .filter { it.isStarted && !it.isCompleted }
                .map { it.toSimpleStartedChallengeModel() }
        }
        .combine(tickerFlow) { challenges, currentTime ->
            challenges.map { challenge -> challenge.updateCurrentRecord(currentTime) }
        }
        .combine(sortOrder) { challenges, order ->
            challenges.sortBy(order)
        }

    override val completedChallenges: Flow<List<SimpleStartedChallenge>> = challenges
        .map { entities ->
            entities
                .filter { it.isStarted && it.isCompleted }
                .map { it.toSimpleStartedChallengeModel() }
        }

    private fun SimpleStartedChallenge.updateCurrentRecord(currentTime: LocalDateTime) = copy(
        currentRecordInSeconds = ChronoUnit.SECONDS.between(recentResetDateTime, currentTime),
    )

    private fun List<SimpleStartedChallenge>.sortBy(sortOrder: SortOrder): List<SimpleStartedChallenge> {
        return when (sortOrder) {
            SortOrder.LATEST -> this.sortedByDescending { it.id }
            SortOrder.OLDEST -> this.sortedBy { it.id }
            SortOrder.TITLE -> this.sortedBy { it.title }
            SortOrder.HIGHEST_RECORD -> this.sortedByDescending { it.currentRecordInSeconds }
            SortOrder.LOWEST_RECORD -> this.sortedBy { it.currentRecordInSeconds }
        }
    }

    override val waitingChallenges: Flow<List<SimpleWaitingChallenge>> = challenges.map { challenges ->
        challenges
            .filter { !it.isStarted }
            .map { it.toSimpleWaitingChallengeModel() }
    }

    override suspend fun addChallenge(
        category: Category,
        title: String,
        description: String,
        targetDays: TargetDays,
        startDateTime: LocalDateTime?,
        maxParticipants: Int,
        roomPassword: String,
    ): NetworkResult<Int> {
        return challengeDataSource.addChallenge(
            AddChallengeRequest(
                category = category.toRequestString(),
                title = title,
                description = description,
                targetDays = targetDays.toRequestString(),
                startDateTime = startDateTime?.toRequestString() ?: "",
                maxParticipants = maxParticipants.toString(),
                password = roomPassword,
            ),
        ).map { it.challengeId }
    }

    override suspend fun editChallengeTitleDescription(
        challengeId: Int,
        title: String,
        description: String,
    ): NetworkResult<Unit> {
        return challengeDataSource.editChallengeTitleDescription(
            EditChallengeTitleDescriptionRequest(
                challengeId = challengeId,
                title = title,
                description = description,
            ),
        )
    }

    override suspend fun editChallengeCategory(
        challengeId: Int,
        category: Category,
    ): NetworkResult<Unit> {
        return challengeDataSource.editChallengeCategory(
            challengeId = challengeId,
            category = category.toRequestString(),
        )
    }

    override suspend fun editChallengeTargetDays(
        challengeId: Int,
        targetDays: TargetDays,
    ): NetworkResult<Unit> {
        return challengeDataSource.editChallengeTargetDays(
            challengeId = challengeId,
            targetDays = targetDays.toRequestString(),
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getTogetherChallenges(
        query: String,
        category: Category,
    ): Flow<PagingData<SimpleWaitingChallenge>> {
        val pager = Pager(
            config = PagingConfig(
                pageSize = PAGING_PAGE_SIZE,
                initialLoadSize = PAGING_INITIAL_LOAD_SIZE,
                prefetchDistance = PAGING_PREFETCH_DISTANCE,
                enablePlaceholders = true,
            ),
            remoteMediator = TogetherChallengeRemoteMediator(
                query = query,
                category = category,
                togetherChallengeDao = togetherChallengeDao,
                challengeDataSource = challengeDataSource,
            ),
            pagingSourceFactory = {
                if (category == Category.ALL) {
                    togetherChallengeDao.pagingSource()
                } else {
                    togetherChallengeDao.pagingSource(category.toRequestString())
                }
            },
        )

        return pager.flow.map { pagingData ->
            pagingData.map { it.toModel() }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getChallengePosts(challengeId: Int): Flow<PagingData<ChallengePost>> {
        val pager = Pager(
            config = PagingConfig(
                pageSize = PAGING_PAGE_SIZE,
                initialLoadSize = PAGING_INITIAL_LOAD_SIZE,
                prefetchDistance = PAGING_PREFETCH_DISTANCE,
                enablePlaceholders = true,
            ),
            remoteMediator = ChallengePostRemoteMediator(
                challengeId = challengeId,
                challengePostDao = challengePostDao,
                challengeDataSource = challengeDataSource,
            ),
            pagingSourceFactory = {
                challengePostDao.pagingSource(challengeId)
            },
        )

        return pager.flow.map { pagingData ->
            pagingData.map { it.toModel() }
        }
    }

    override fun getLatestChallengePost(challengeId: Int): Flow<ChallengePost?> =
        challengePostDao.getLatestPost(challengeId).map { it?.toModel() }

    override fun observeChallengePostUpdates(challengeId: Int): Flow<Unit> =
        challengeDataSource.observeChallengePostUpdates(challengeId)
            .onEach {
                challengePostDao.replaceByTempId(it.toEntity())
            }
            .mapToUnit()
            .onStart {
                challengePostDao.deleteUnSynced()
                challengePostDao.getLatestPostId(challengeId)?.let {
                    syncNewlyAddedChallengePosts(challengeId, it)
                }
                emit(Unit)
            }
            .retry(CHALLENGE_POST_UPDATE_RETRY_COUNT) { cause ->
                cause !is CancellationException
            }

    private suspend fun syncNewlyAddedChallengePosts(challengeId: Int, lastPostId: Int) {
        challengeDataSource.getChallengePostsUpdated(challengeId, lastPostId)
            .onSuccess { response ->
                val entities = response.map { it.toEntity(challengeId) }
                challengePostDao.insertAll(entities)
            }
            .onFailure { throw it.safeThrowable() }
    }

    override suspend fun reportChallengePost(
        postId: Int,
        reportReason: ReportReason,
    ): NetworkResult<Unit> {
        return challengeDataSource.reportChallengePost(
            ReportChallengePostRequest(
                postId = postId,
                reason = reportReason.name,
            ),
        )
    }

    override suspend fun addChallengePost(
        challengeId: Int,
        content: String,
        tempWrittenDateTime: LocalDateTime,
    ) {
        val tempId = -(System.nanoTime().toInt())
        challengePostDao.insert(
            ChallengePostEntity(
                id = tempId,
                tempId = tempId,
                challengeId = challengeId,
                writerName = "",
                writerBestRecordInSeconds = 0,
                content = content,
                writtenDateTime = tempWrittenDateTime,
                isAuthor = true,
                isSynced = false,
            ),
        )

        challengeDataSource.addChallengePost(
            AddChallengePostRequest(
                content = content,
                tempId = tempId,
            ),
        )
    }

    override suspend fun resetStartedChallenge(challengeId: Int, memo: String): NetworkResult<Unit> {
        return challengeDataSource.resetStartedChallenge(
            ResetChallengeRequest(
                challengeId = challengeId,
                resetMemo = memo,
            ),
        )
    }

    override suspend fun deleteStartedChallenge(challengeId: Int): NetworkResult<Unit> =
        challengeDataSource.deleteStartedChallenge(challengeId)
            .onSuccess { challengeDao.deleteById(challengeId) }

    override suspend fun deleteChallengePost(postId: Int): NetworkResult<Unit> =
        challengeDataSource.deleteChallengePost(postId)
            .onSuccess { challengePostDao.deleteById(postId) }

    override suspend fun forceRemoveStartedChallengeMember(memberId: Int): NetworkResult<Unit> =
        challengeDataSource.forceRemoveFromStartedChallenge(memberId)

    override suspend fun getStartedChallengeDetail(
        challengeId: Int,
    ): Flow<NetworkResult<DetailedStartedChallenge>> = when (
        val response = challengeDataSource.getStartedChallengeDetail(challengeId)
    ) {
        is NetworkResult.Success -> {
            val result = response.data
            val challenge = result.toDetailedStartedChallengeModel()

            // 리셋 등 이벤트에 대응하여 즉각적으로 데이터를 최신화 하기 위한 동기화
            challengeDao.insert(result.toEntity())

            tickerFlow.map { currentTime ->
                NetworkResult.Success(challenge.updateCurrentRecord(currentTime))
            }
        }
        is NetworkResult.Failure -> flowOf(response)
    }

    private fun DetailedStartedChallenge.updateCurrentRecord(currentTime: LocalDateTime) = copy(
        currentRecordInSeconds = ChronoUnit.SECONDS.between(recentResetDateTime, currentTime),
    )

    override suspend fun getResetRecords(challengeId: Int): NetworkResult<List<ResetRecord>> =
        challengeDataSource.getResetRecords(challengeId).map { response ->
            response.map { it.toModel() }
        }

    override suspend fun getChallengeRanking(challengeId: Int): Flow<NetworkResult<List<ChallengeRank>>> =
        when (val response = challengeDataSource.getChallengeRanking(challengeId)) {
            is NetworkResult.Success -> {
                val result = response.data
                val challengeRanks = result.map { it.toModel() }

                tickerFlow.map { currentTime ->
                    NetworkResult.Success(challengeRanks.map { it.updateCurrentRecord(currentTime) })
                }
            }
            is NetworkResult.Failure -> flowOf(response)
        }

    private fun ChallengeRank.updateCurrentRecord(currentTime: LocalDateTime) = copy(
        currentRecordInSeconds = ChronoUnit.SECONDS.between(recentResetDateTime, currentTime),
    )

    override suspend fun getRemoteTier(): NetworkResult<Tier> =
        challengeDataSource.getRecords().map {
            Tier.getCurrentTier(it.recordInSeconds)
        }

    override suspend fun setLocalTier(tier: Tier) =
        challengePreferencesDataSource.setCurrentTier(tier.toProto())

    override suspend fun setSortOrder(order: SortOrder) =
        challengePreferencesDataSource.setSortOrder(order.toProto())

    override suspend fun clearRecentCompletedChallenges() =
        challengePreferencesDataSource.setCompletedChallengeTitles(emptyList())

    override suspend fun syncChallenges(): NetworkResult<List<String>> {
        return challengeDataSource.getMyChallenges()
            .onSuccess { response ->
                val challenges = response.challenges.map { it.toEntity() }
                challengeDao.syncChallenges(challenges)
                challengePreferencesDataSource.addCompletedChallengeTitles(response.newlyCompletedTitles)
            }
            .map { response ->
                response.newlyCompletedTitles
            }
    }

    private companion object {
        const val PAGING_PAGE_SIZE = 30
        const val PAGING_INITIAL_LOAD_SIZE = 50
        const val PAGING_PREFETCH_DISTANCE = 90
        const val CHALLENGE_POST_UPDATE_RETRY_COUNT = 3L
    }
}
