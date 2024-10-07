package com.yjy.core.data.di

import com.yjy.core.data.repository.AuthRepository
import com.yjy.core.data.repository.AuthRepositoryImpl
import com.yjy.core.data.util.SessionManagerImpl
import com.yjy.core.network.util.SessionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl,
    ): AuthRepository

    @Binds
    abstract fun bindSessionManager(
        impl: SessionManagerImpl,
    ): SessionManager
}
