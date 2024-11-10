package com.yjy.data.challenge.impl.di

import com.yjy.common.core.coroutines.DefaultDispatcher
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.challenge.impl.repository.ChallengeRepositoryImpl
import com.yjy.data.challenge.impl.util.DefaultTimeManager
import com.yjy.data.challenge.impl.util.DefaultTimeProvider
import com.yjy.data.challenge.impl.util.TimeManager
import com.yjy.data.challenge.impl.util.TimeProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    abstract fun bindChallengeRepository(
        impl: ChallengeRepositoryImpl,
    ): ChallengeRepository

    @Binds
    abstract fun bindTimeProvider(
        impl: DefaultTimeProvider,
    ): TimeProvider

    companion object {
        @Provides
        @Singleton
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
}
