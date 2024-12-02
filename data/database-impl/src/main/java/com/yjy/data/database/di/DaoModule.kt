package com.yjy.data.database.di

import com.yjy.data.database.ChallengeTogetherDatabase
import com.yjy.data.database.dao.ChallengeDao
import com.yjy.data.database.dao.ChallengePostDao
import com.yjy.data.database.dao.NotificationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {

    @Provides
    fun provideChallengeDao(
        database: ChallengeTogetherDatabase,
    ): ChallengeDao = database.challengeDao()

    @Provides
    fun provideChallengePostDao(
        database: ChallengeTogetherDatabase,
    ): ChallengePostDao = database.challengePostDao()

    @Provides
    fun provideNotificationDao(
        database: ChallengeTogetherDatabase,
    ): NotificationDao = database.notificationDao()
}
