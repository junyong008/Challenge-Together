package com.yjy.data.network.di

import com.yjy.data.network.datasource.AuthDataSource
import com.yjy.data.network.datasource.AuthDataSourceImpl
import com.yjy.data.network.datasource.ChallengeDataSource
import com.yjy.data.network.datasource.ChallengeDataSourceImpl
import com.yjy.data.network.datasource.ChallengePostDataSource
import com.yjy.data.network.datasource.ChallengePostDataSourceImpl
import com.yjy.data.network.datasource.StartedChallengeDataSource
import com.yjy.data.network.datasource.StartedChallengeDataSourceImpl
import com.yjy.data.network.datasource.UserDataSource
import com.yjy.data.network.datasource.UserDataSourceImpl
import com.yjy.data.network.datasource.WaitingChallengeDataSource
import com.yjy.data.network.datasource.WaitingChallengeDataSourceImpl
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

    @Binds
    abstract fun bindChallengePostDataSource(impl: ChallengePostDataSourceImpl): ChallengePostDataSource

    @Binds
    abstract fun bindStartedChallengeDataSource(impl: StartedChallengeDataSourceImpl): StartedChallengeDataSource

    @Binds
    abstract fun bindWaitingChallengeDataSource(impl: WaitingChallengeDataSourceImpl): WaitingChallengeDataSource

    @Binds
    abstract fun bindUserDataSource(impl: UserDataSourceImpl): UserDataSource
}
