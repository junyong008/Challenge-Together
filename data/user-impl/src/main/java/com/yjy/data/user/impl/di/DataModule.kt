package com.yjy.data.user.impl.di

import com.yjy.data.network.util.TimeDiffManager
import com.yjy.data.user.api.UserRepository
import com.yjy.data.user.impl.repository.UserRepositoryImpl
import com.yjy.data.user.impl.util.TimeDiffManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl,
    ): UserRepository

    @Binds
    abstract fun bindTimeDiffManager(
        impl: TimeDiffManagerImpl,
    ): TimeDiffManager
}
