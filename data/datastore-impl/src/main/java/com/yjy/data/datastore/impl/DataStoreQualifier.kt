package com.yjy.data.datastore.impl

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SessionDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserPreferencesDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NotificationDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ChallengePreferencesDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppLockDataStore
