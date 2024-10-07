package com.yjy.core.network.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.yjy.core.network.BuildConfig
import com.yjy.core.network.BuildConfig.BASE_URL
import com.yjy.core.network.ChallengeTogetherApi
import com.yjy.core.network.ChallengeTogetherCallFactory
import com.yjy.core.network.adapter.NetworkResultCallAdapterFactory
import com.yjy.core.network.interceptor.SessionInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val MAX_TIME_OUT = 10_000L

private val json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    prettyPrint = true
}

private val jsonConverterFactory: Converter.Factory by lazy {
    json.asConverterFactory("application/json".toMediaType())
}

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    @ChallengeTogetherCallFactory
    fun provideOkHttpCallFactory(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        sessionInterceptor: SessionInterceptor,
    ): Call.Factory {
        return OkHttpClient.Builder()
            .connectTimeout(MAX_TIME_OUT, TimeUnit.MILLISECONDS)
            .readTimeout(MAX_TIME_OUT, TimeUnit.MILLISECONDS)
            .writeTimeout(MAX_TIME_OUT, TimeUnit.MILLISECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(sessionInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @ChallengeTogetherApi
    fun provideRetrofit(
        @ChallengeTogetherCallFactory okhttpCallFactory: dagger.Lazy<Call.Factory>,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .callFactory { request -> okhttpCallFactory.get().newCall(request) }
            .addConverterFactory(jsonConverterFactory)
            .addCallAdapterFactory(NetworkResultCallAdapterFactory())
            .build()
    }
}