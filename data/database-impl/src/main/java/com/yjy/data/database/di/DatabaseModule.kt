package com.yjy.data.database.di

import android.content.Context
import androidx.room.Room
import com.yjy.data.database.ChallengeTogetherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun provideChallengeTogetherDatabase(
        @ApplicationContext context: Context,
    ): ChallengeTogetherDatabase = Room.databaseBuilder(
        context,
        ChallengeTogetherDatabase::class.java,
        "challenge-together-database",
    ).build()
}
