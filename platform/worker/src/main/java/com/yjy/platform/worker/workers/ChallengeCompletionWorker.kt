package com.yjy.platform.worker.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.yjy.common.core.constants.TimeConst.SECONDS_PER_DAY
import com.yjy.common.core.coroutines.IoDispatcher
import com.yjy.common.network.fold
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.domain.GetStartedChallengesUseCase
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.core.TargetDays
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
internal class ChallengeCompletionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val getStartedChallengesUseCase: GetStartedChallengesUseCase,
    private val challengeRepository: ChallengeRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        try {
            Timber.d("Checking challenges for completion")
            val isNewlyCompletedChallengeAvailable = getStartedChallengesUseCase()
                .first()
                .any { it.isNewlyCompleted() }

            if (isNewlyCompletedChallengeAvailable) {
                challengeRepository.syncChallenges().fold(
                    onSuccess = { Result.success() },
                    onFailure = { Result.retry() },
                )
            } else {
                Result.success()
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to check challenges due to unexpected error")
            Result.failure()
        }
    }

    private fun SimpleStartedChallenge.isNewlyCompleted(): Boolean {
        val targetDaysInSeconds = when (val targetDays = this.targetDays) {
            is TargetDays.Fixed -> targetDays.days * SECONDS_PER_DAY
            TargetDays.Infinite -> return false
        }

        return this.currentRecordInSeconds >= targetDaysInSeconds
    }

    companion object {
        const val WORK_NAME = "challenge_completion_worker"
        private const val CHECK_INTERVAL_MINUTES = 15L

        fun createRequest() = PeriodicWorkRequestBuilder<ChallengeCompletionWorker>(
            CHECK_INTERVAL_MINUTES,
            TimeUnit.MINUTES,
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build(),
        ).build()
    }
}
