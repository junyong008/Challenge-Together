package com.yjy.data.challenge.impl.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.yjy.common.network.HttpStatusCodes
import com.yjy.common.network.NetworkResult
import com.yjy.common.network.map
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.challenge.api.WaitingChallengeRepository
import com.yjy.data.challenge.impl.mapper.toDetailedWaitingChallengeModel
import com.yjy.data.challenge.impl.mapper.toModel
import com.yjy.data.challenge.impl.mapper.toRequestString
import com.yjy.data.challenge.impl.mapper.toSimpleWaitingChallengeModel
import com.yjy.data.challenge.impl.mapper.toTogetherEntity
import com.yjy.data.challenge.impl.mediator.TogetherChallengeRemoteMediator
import com.yjy.data.database.dao.ChallengeDao
import com.yjy.data.database.dao.TogetherChallengeDao
import com.yjy.data.network.datasource.WaitingChallengeDataSource
import com.yjy.model.challenge.DetailedWaitingChallenge
import com.yjy.model.challenge.SimpleWaitingChallenge
import com.yjy.model.challenge.core.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class WaitingChallengeRepositoryImpl @Inject constructor(
    private val waitingChallengeDataSource: WaitingChallengeDataSource,
    private val togetherChallengeDao: TogetherChallengeDao,
    challengeDao: ChallengeDao,
) : WaitingChallengeRepository {

    override val waitingChallenges: Flow<List<SimpleWaitingChallenge>> = challengeDao.getAll().map { challenges ->
        challenges
            .filter { !it.isStarted }
            .map { it.toSimpleWaitingChallengeModel() }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getTogetherChallenges(
        query: String,
        languageCode: String,
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
                languageCode = languageCode,
                category = category,
                togetherChallengeDao = togetherChallengeDao,
                waitingChallengeDataSource = waitingChallengeDataSource,
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

    override suspend fun getWaitingChallengeDetail(
        challengeId: Int,
        password: String,
    ): NetworkResult<DetailedWaitingChallenge> =
        waitingChallengeDataSource.getWaitingChallengeDetail(challengeId, password)
            .onSuccess {
                togetherChallengeDao.update(it.toTogetherEntity())
            }
            .onFailure {
                if (it is NetworkResult.Failure.HttpError) {
                    when (it.code) {
                        HttpStatusCodes.NOT_FOUND,
                        HttpStatusCodes.CONFLICT,
                        -> togetherChallengeDao.deleteById(challengeId)
                    }
                }
            }
            .map {
                it.toDetailedWaitingChallengeModel()
            }

    override suspend fun deleteWaitingChallenge(challengeId: Int): NetworkResult<Unit> =
        waitingChallengeDataSource.deleteWaitingChallenge(challengeId)
            .onSuccess { togetherChallengeDao.deleteById(challengeId) }

    override suspend fun startWaitingChallenge(challengeId: Int): NetworkResult<Unit> =
        waitingChallengeDataSource.startWaitingChallenge(challengeId)
            .onSuccess { togetherChallengeDao.deleteById(challengeId) }

    override suspend fun joinWaitingChallenge(challengeId: Int): NetworkResult<Unit> =
        waitingChallengeDataSource.joinWaitingChallenge(challengeId)

    override suspend fun leaveWaitingChallenge(challengeId: Int): NetworkResult<Unit> =
        waitingChallengeDataSource.leaveWaitingChallenge(challengeId)

    private companion object {
        const val PAGING_PAGE_SIZE = 30
        const val PAGING_INITIAL_LOAD_SIZE = 50
        const val PAGING_PREFETCH_DISTANCE = 90
    }
}
