package com.yjy.platform.worker.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import com.yjy.common.core.coroutines.IoDispatcher
import com.yjy.common.network.fold
import com.yjy.data.user.api.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
internal class TimeSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val userRepository: UserRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        try {
            userRepository.syncTime().fold(
                onSuccess = { Result.success() },
                onFailure = { Result.retry() },
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync time due to unexpected error")
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "time_sync_worker"

        fun createRequest() = OneTimeWorkRequestBuilder<TimeSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
            )
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS,
            )
            .build()
    }
}
