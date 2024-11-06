package com.yjy.platform.time

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class Module {

    @Binds
    abstract fun bindsTimeMonitor(impl: TimeMonitorImpl): TimeMonitor
}
