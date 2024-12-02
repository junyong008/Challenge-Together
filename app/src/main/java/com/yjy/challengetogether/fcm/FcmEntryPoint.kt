package com.yjy.challengetogether.fcm

import com.yjy.data.user.api.UserRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@EntryPoint
interface FcmEntryPoint {
    fun userRepository(): UserRepository
    fun notificationMapper(): NotificationMapper
}
