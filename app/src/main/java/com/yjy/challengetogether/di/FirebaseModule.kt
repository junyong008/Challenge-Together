package com.yjy.challengetogether.di

import com.yjy.challengetogether.fcm.DefaultFcmTokenProvider
import com.yjy.challengetogether.fcm.NotificationMapper
import com.yjy.data.auth.api.AppLockRepository
import com.yjy.data.user.api.FcmTokenProvider
import com.yjy.data.user.api.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FirebaseModule {

    @Binds
    abstract fun bindFcmTokenProvider(
        defaultFcmTokenProvider: DefaultFcmTokenProvider,
    ): FcmTokenProvider
}

@InstallIn(SingletonComponent::class)
@EntryPoint
interface FcmEntryPoint {
    fun userRepository(): UserRepository
    fun appLockRepository(): AppLockRepository
    fun notificationMapper(): NotificationMapper
}
