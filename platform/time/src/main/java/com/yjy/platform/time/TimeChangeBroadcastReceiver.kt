package com.yjy.platform.time

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yjy.platform.worker.manager.WorkerManager
import dagger.hilt.EntryPoints
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class TimeChangeBroadcastReceiver : BroadcastReceiver() {

    private var workerManager: WorkerManager? = null

    private fun getWorkerManager(context: Context): WorkerManager {
        if (workerManager == null) {
            val hiltEntryPoint = EntryPoints.get(
                context.applicationContext,
                TimeChangeBroadcastReceiverEntryPoint::class.java,
            )

            workerManager = hiltEntryPoint.workerManager()
        }
        return workerManager!!
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_TIME_CHANGED ||
            intent.action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            getWorkerManager(context).startTimeSync()
        }
    }
}
