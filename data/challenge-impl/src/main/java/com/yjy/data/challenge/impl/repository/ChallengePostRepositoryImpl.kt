package com.yjy.data.challenge.impl.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.yjy.common.core.extensions.mapToUnit
import com.yjy.common.network.NetworkResult
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.challenge.api.ChallengePostRepository
import com.yjy.data.challenge.impl.mapper.toEntity
import com.yjy.data.challenge.impl.mapper.toModel
import com.yjy.data.challenge.impl.mediator.ChallengePostRemoteMediator
import com.yjy.data.database.dao.ChallengePostDao
import com.yjy.data.database.model.ChallengePostEntity
import com.yjy.data.network.datasource.ChallengePostDataSource
import com.yjy.data.network.request.challenge.AddChallengePostRequest
import com.yjy.data.network.request.challenge.ReportChallengePostRequest
import com.yjy.model.challenge.ChallengePost
import com.yjy.model.common.ReportReason
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

internal class ChallengePostRepositoryImpl @Inject constructor(
    private val challengePostDataSource: ChallengePostDataSource,
    private val challengePostDao: ChallengePostDao,
) : ChallengePostRepository {

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
                challengePostDataSource = challengePostDataSource,
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
        challengePostDataSource.observeChallengePostUpdates(challengeId)
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
        challengePostDataSource.getChallengePostsUpdated(challengeId, lastPostId)
            .onSuccess { response ->
                val entities = response.map { it.toEntity(challengeId) }
                challengePostDao.insertAll(entities)
            }
            .onFailure { throw it.safeThrowable() }
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

        challengePostDataSource.addChallengePost(
            AddChallengePostRequest(
                content = content,
                tempId = tempId,
            ),
        )
    }

    override suspend fun deleteChallengePost(postId: Int): NetworkResult<Unit> =
        challengePostDataSource.deleteChallengePost(postId)
            .onSuccess { challengePostDao.deleteById(postId) }

    override suspend fun reportChallengePost(
        postId: Int,
        reportReason: ReportReason,
    ): NetworkResult<Unit> = challengePostDataSource.reportChallengePost(
        request = ReportChallengePostRequest(
            postId = postId,
            reason = reportReason.name,
        ),
    )

    override suspend fun clearLocalData() {
        challengePostDao.deleteAll()
    }

    private companion object {
        const val PAGING_PAGE_SIZE = 30
        const val PAGING_INITIAL_LOAD_SIZE = 50
        const val PAGING_PREFETCH_DISTANCE = 90
        const val CHALLENGE_POST_UPDATE_RETRY_COUNT = 3L
    }
}
