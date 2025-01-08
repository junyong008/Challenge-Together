package com.yjy.data.community.impl.di

import com.yjy.data.community.api.CommunityRepository
import com.yjy.data.community.impl.repository.CommunityRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    abstract fun bindCommunityRepository(
        impl: CommunityRepositoryImpl,
    ): CommunityRepository
}
