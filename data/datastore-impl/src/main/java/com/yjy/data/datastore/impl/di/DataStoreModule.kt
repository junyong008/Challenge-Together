package com.yjy.data.datastore.impl.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yjy.data.datastore.api.ChallengePreferences
import com.yjy.data.datastore.impl.ChallengePreferencesDataStore
import com.yjy.data.datastore.impl.ChallengePreferencesSerializer
import com.yjy.data.datastore.impl.NotificationDataStore
import com.yjy.data.datastore.impl.SessionDataStore
import com.yjy.data.datastore.impl.UserPreferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val SESSION_DATASTORE = "session_datastore"
private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = SESSION_DATASTORE,
)

private const val USER_PREFERENCES_DATASTORE = "user_datastore"
private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES_DATASTORE,
)

private const val NOTIFICATION_DATASTORE = "notification_datastore"
private val Context.notificationDataStore: DataStore<Preferences> by preferencesDataStore(
    name = NOTIFICATION_DATASTORE,
)

@Module
@InstallIn(SingletonComponent::class)
internal object DataStoreModule {

    @Provides
    @Singleton
    @SessionDataStore
    fun provideSessionDataStore(@ApplicationContext context: Context) =
        context.sessionDataStore

    @Provides
    @Singleton
    @UserPreferencesDataStore
    fun provideUserPreferencesDataStore(@ApplicationContext context: Context) =
        context.userPreferencesDataStore

    @Provides
    @Singleton
    @NotificationDataStore
    fun provideNotificationDataStore(@ApplicationContext context: Context) =
        context.notificationDataStore

    @Provides
    @Singleton
    @ChallengePreferencesDataStore
    fun provideChallengePreferencesDataStore(
        @ApplicationContext context: Context,
        challengePreferencesSerializer: ChallengePreferencesSerializer,
    ): DataStore<ChallengePreferences> {
        return DataStoreFactory.create(
            serializer = challengePreferencesSerializer,
            produceFile = { context.dataStoreFile("challenge_preferences.pb") },
        )
    }
}
