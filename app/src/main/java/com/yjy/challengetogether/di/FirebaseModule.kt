package com.yjy.challengetogether.di

import com.yjy.challengetogether.fcm.DefaultFcmTokenProvider
import com.yjy.data.user.api.FcmTokenProvider
import dagger.Binds
import dagger.Module
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
