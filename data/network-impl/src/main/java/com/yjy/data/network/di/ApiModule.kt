package com.yjy.data.network.di

import com.yjy.data.network.ChallengeTogetherApi
import com.yjy.data.network.service.ChallengeTogetherService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ApiModule {

    @Provides
    @Singleton
    fun provideChallengeTogetherApi(
        @ChallengeTogetherApi retrofit: Retrofit,
    ): ChallengeTogetherService = retrofit.create(ChallengeTogetherService::class.java)
}
