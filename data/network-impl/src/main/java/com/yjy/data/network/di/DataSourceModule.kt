package com.yjy.data.network.di

import com.yjy.data.network.datasource.AuthDataSource
import com.yjy.data.network.datasource.AuthDataSourceImpl
import com.yjy.data.network.datasource.ChallengeDataSource
import com.yjy.data.network.datasource.ChallengeDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataSourceModule {

    @Binds
    abstract fun bindAuthDataSource(impl: AuthDataSourceImpl): AuthDataSource

    @Binds
    abstract fun bindChallengeDataSource(impl: ChallengeDataSourceImpl): ChallengeDataSource
}
