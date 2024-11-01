package com.yjy.common.core.util

import com.yjy.common.core.coroutines.DefaultDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(ViewModelComponent::class)
object TimeManagerModule {

    @Provides
    fun provideTimeProvider(): TimeProvider = DefaultTimeProvider()

    @Provides
    @ViewModelScoped
    fun provideTimeManager(
        timeProvider: TimeProvider,
        @DefaultDispatcher dispatcher: CoroutineDispatcher,
    ): TimeManager = DefaultTimeManager(
        timeProvider = timeProvider,
        updateInterval = TIME_UPDATE_INTERVAL_MS,
        timeDiffThreshold = TIME_DIFF_THRESHOLD_MS,
        dispatcher = dispatcher,
    )

    private const val TIME_UPDATE_INTERVAL_MS = 500L
    private const val TIME_DIFF_THRESHOLD_MS = 5000L
}
