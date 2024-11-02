package com.yjy.data.datastore.impl.di

import com.yjy.data.datastore.api.ChallengePreferencesDataSource
import com.yjy.data.datastore.api.SessionDataSource
import com.yjy.data.datastore.api.UserPreferencesDataSource
import com.yjy.data.datastore.impl.ChallengePreferencesDataSourceImpl
import com.yjy.data.datastore.impl.SessionDataSourceImpl
import com.yjy.data.datastore.impl.UserPreferencesDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataSourceModule {

    @Binds
    abstract fun bindSessionDataSource(impl: SessionDataSourceImpl): SessionDataSource

    @Binds
    abstract fun bindUserPreferencesDataSource(
        impl: UserPreferencesDataSourceImpl,
    ): UserPreferencesDataSource

    @Binds
    abstract fun bindChallengePreferencesDataSource(
        impl: ChallengePreferencesDataSourceImpl,
    ): ChallengePreferencesDataSource
}
