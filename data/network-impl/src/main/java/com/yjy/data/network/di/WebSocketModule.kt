package com.yjy.data.network.di

import com.yjy.data.network.WebSocketClient
import com.yjy.data.network.interceptor.SessionInterceptor
import com.yjy.data.network.service.ChallengePostWebSocketService
import com.yjy.data.network.service.DefaultChallengePostWebSocketService
import com.yjy.data.network_impl.BuildConfig.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object WebSocketModule {
    private const val WEBSOCKET_CONNECT_TIMEOUT_SECONDS = 10L
    private const val WEBSOCKET_READ_TIMEOUT_MILLIS = 0L
    private const val WEBSOCKET_WRITE_TIMEOUT_MILLIS = 0L

    @Provides
    @Singleton
    @WebSocketClient
    fun provideWebSocketOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        sessionInterceptor: SessionInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(WEBSOCKET_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            .writeTimeout(WEBSOCKET_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            .connectTimeout(WEBSOCKET_CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(sessionInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideBaseUrl(): String = BASE_URL

    @Provides
    @Singleton
    fun provideChallengePostWebSocketService(
        @WebSocketClient okHttpClient: OkHttpClient,
        baseUrl: String,
    ): ChallengePostWebSocketService = DefaultChallengePostWebSocketService(okHttpClient, baseUrl)
}
