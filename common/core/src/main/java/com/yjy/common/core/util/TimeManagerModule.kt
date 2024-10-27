package com.yjy.common.core.util

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object TimeManagerModule {

    @Provides
    fun provideTimeProvider(): TimeProvider = DefaultTimeProvider()

    @Provides
    @ViewModelScoped
    fun provideTimeManager(
        timeProvider: TimeProvider
    ): TimeManager = TimeManager(
        timeProvider = timeProvider,
        updateInterval = TIME_UPDATE_INTERVAL_MS,
        timeDiffThreshold = TIME_DIFF_THRESHOLD_MS,
    )

    private const val TIME_UPDATE_INTERVAL_MS = 500L
    private const val TIME_DIFF_THRESHOLD_MS = 5000L
}
