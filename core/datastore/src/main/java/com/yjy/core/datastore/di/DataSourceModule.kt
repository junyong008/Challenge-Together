package com.yjy.core.datastore.di

import com.yjy.core.datastore.SessionDataSource
import com.yjy.core.datastore.SessionDataSourceImpl
import com.yjy.core.datastore.UserPreferencesDataSource
import com.yjy.core.datastore.UserPreferencesDataSourceImpl
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
    abstract fun bindUserPreferencesDataSource(impl: UserPreferencesDataSourceImpl): UserPreferencesDataSource
}
