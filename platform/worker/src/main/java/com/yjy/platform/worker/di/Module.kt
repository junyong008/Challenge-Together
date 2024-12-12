package com.yjy.platform.worker.di

import android.content.Context
import androidx.work.WorkManager
import com.yjy.platform.worker.manager.WorkerManager
import com.yjy.platform.worker.manager.WorkerManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object Module {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideWorkerManager(workManager: WorkManager): WorkerManager {
        return WorkerManagerImpl(workManager)
    }
}
