package com.yjy.data.datastore.impl.di

import com.yjy.data.datastore.api.AppLockDataSource
import com.yjy.data.datastore.api.ChallengePreferencesDataSource
import com.yjy.data.datastore.api.NotificationSettingDataSource
import com.yjy.data.datastore.api.SessionDataSource
import com.yjy.data.datastore.api.UserPreferencesDataSource
import com.yjy.data.datastore.impl.AppLockDataSourceImpl
import com.yjy.data.datastore.impl.ChallengePreferencesDataSourceImpl
import com.yjy.data.datastore.impl.NotificationSettingDataSourceImpl
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
    abstract fun bindNotificationDataSource(
        impl: NotificationSettingDataSourceImpl,
    ): NotificationSettingDataSource

    @Binds
    abstract fun bindChallengePreferencesDataSource(
        impl: ChallengePreferencesDataSourceImpl,
    ): ChallengePreferencesDataSource

    @Binds
    abstract fun bindAppLockDataSource(
        impl: AppLockDataSourceImpl,
    ): AppLockDataSource
}
