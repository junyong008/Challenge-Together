package com.yjy.challengetogether.di

import android.content.Context
import com.yjy.challengetogether.navigation.NavigatorImpl
import com.yjy.common.navigation.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NavigatorModule {

    @Provides
    @Singleton
    fun provideNavigator(
        @ApplicationContext context: Context,
    ): Navigator = NavigatorImpl(context)
}