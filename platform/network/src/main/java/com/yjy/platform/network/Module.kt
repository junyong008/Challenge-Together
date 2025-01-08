package com.yjy.platform.network

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class Module {

    @Binds
    abstract fun bindsNetworkMonitor(impl: NetworkMonitorImpl): NetworkMonitor
}
