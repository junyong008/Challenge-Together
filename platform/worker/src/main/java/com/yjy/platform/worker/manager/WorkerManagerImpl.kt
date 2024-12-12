package com.yjy.platform.worker.manager

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.yjy.platform.worker.workers.ChallengeCompletionWorker
import com.yjy.platform.worker.workers.TimeSyncWorker
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class WorkerManagerImpl @Inject constructor(
    private val workManager: WorkManager,
) : WorkerManager {

    override fun startPeriodicCheck() {
        workManager.enqueueUniquePeriodicWork(
            ChallengeCompletionWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            ChallengeCompletionWorker.createRequest(),
        )

        workManager.getWorkInfosForUniqueWork(ChallengeCompletionWorker.WORK_NAME).get().forEach {
            Timber.d("Work ID: ${it.id}")
            Timber.d("Work State: ${it.state}")
            Timber.d("Work Tags: ${it.tags}")
        }
    }

    override fun stopPeriodicCheck() {
        workManager.cancelUniqueWork(ChallengeCompletionWorker.WORK_NAME)
    }

    override fun startTimeSync() {
        workManager.enqueueUniqueWork(
            TimeSyncWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            TimeSyncWorker.createRequest(),
        )
    }
}
