package com.yjy.data.challenge.impl.di

import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.challenge.impl.repository.ChallengeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    abstract fun bindChallengeRepository(
        impl: ChallengeRepositoryImpl,
    ): ChallengeRepository
}