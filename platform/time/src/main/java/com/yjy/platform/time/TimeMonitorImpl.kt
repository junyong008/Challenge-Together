package com.yjy.platform.time

import android.content.Context
import android.provider.Settings
import com.yjy.common.core.coroutines.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

internal class TimeMonitorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : TimeMonitor {
    override val isAutoTime: Flow<Boolean> = flow {
        val contentResolver = context.contentResolver
        while (true) {
            val isAutoTimeEnabled = Settings.Global.getInt(contentResolver, Settings.Global.AUTO_TIME, 0) == 1
            emit(isAutoTimeEnabled)
            delay(AUTO_TIME_CHECK_INTERVAL)
        }
    }
        .flowOn(ioDispatcher)
        .distinctUntilChanged()
        .conflate()

    companion object {
        private const val AUTO_TIME_CHECK_INTERVAL = 1000L
    }
}
