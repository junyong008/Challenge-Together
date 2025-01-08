package com.yjy.data.network.di

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yjy.data.network.service.DefaultFirebaseService
import com.yjy.data.network.service.FirebaseService
import com.yjy.data.network_impl.BuildConfig.FIREBASE_DB_URL
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object FirebaseModule {

    @Singleton
    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return Firebase.database(FIREBASE_DB_URL)
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FirebaseServiceModule {

    @Binds
    abstract fun bindFirebaseService(impl: DefaultFirebaseService): FirebaseService
}
