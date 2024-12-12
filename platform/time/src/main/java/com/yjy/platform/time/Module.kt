package com.yjy.platform.time

import com.yjy.platform.worker.manager.WorkerManager
import dagger.Binds
import dagger.Module
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class Module {

    @Binds
    abstract fun bindsTimeMonitor(impl: TimeMonitorImpl): TimeMonitor
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface TimeChangeBroadcastReceiverEntryPoint {
    fun workerManager(): WorkerManager
}
