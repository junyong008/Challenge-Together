package com.yjy.data.auth.impl.di

import com.yjy.data.auth.api.AppLockRepository
import com.yjy.data.auth.api.AuthRepository
import com.yjy.data.auth.impl.repository.AppLockRepositoryImpl
import com.yjy.data.auth.impl.repository.AuthRepositoryImpl
import com.yjy.data.auth.impl.util.SessionManagerImpl
import com.yjy.data.network.util.SessionManager
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
    abstract fun bindAppLockRepository(
        impl: AppLockRepositoryImpl,
    ): AppLockRepository

    @Binds
    abstract fun bindSessionManager(
        impl: SessionManagerImpl,
    ): SessionManager
}
