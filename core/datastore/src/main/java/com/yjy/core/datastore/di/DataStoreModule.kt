package com.yjy.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yjy.core.datastore.SessionDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val SESSION_DATASTORE = "session_datastore"
private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = SESSION_DATASTORE)

@Module
@InstallIn(SingletonComponent::class)
internal object DataStoreModule {

    @Provides
    @Singleton
    @SessionDataStore
    fun provideOnboardingDataStore(@ApplicationContext context: Context) =
        context.sessionDataStore
}
