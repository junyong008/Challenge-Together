package com.yjy.common.core.util

import android.os.SystemClock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import kotlin.math.abs

interface TimeProvider {
    fun getCurrentTime(): LocalDateTime
    fun getBootTime(): Long
}

interface TimeManager {
    val timeChangedFlow: SharedFlow<Unit>
    val tickerFlow: Flow<LocalDateTime>
}

internal class DefaultTimeProvider @Inject constructor() : TimeProvider {
    override fun getCurrentTime(): LocalDateTime = LocalDateTime.now()
    override fun getBootTime(): Long = SystemClock.elapsedRealtime()
}

internal class DefaultTimeManager @Inject constructor(
    private val timeProvider: TimeProvider,
    private val updateInterval: Long,
    private val timeDiffThreshold: Long,
    dispatcher: CoroutineDispatcher,
) : TimeManager {
    private var latestDiffWithBootTime: Long? = null

    private val _timeChangedFlow = MutableSharedFlow<Unit>()
    override val timeChangedFlow = _timeChangedFlow.asSharedFlow()

    override val tickerFlow = flow {
        while (true) {
            val currentTime = timeProvider.getCurrentTime()
            val diffWithBootTime = calculateDiffWithBootTime(currentTime)
            detectTimeChange(diffWithBootTime)
            emit(currentTime)
            delay(updateInterval)
        }
    }.flowOn(dispatcher)

    private fun calculateDiffWithBootTime(currentDateTime: LocalDateTime): Long {
        val currentDateTimeMillis = currentDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
        val currentBootTime = timeProvider.getBootTime()
        return currentDateTimeMillis - currentBootTime
    }

    private suspend fun detectTimeChange(diffWithBootTime: Long) {
        val previousDiff = latestDiffWithBootTime ?: run {
            latestDiffWithBootTime = diffWithBootTime
            return
        }

        val timeDifference = abs(previousDiff - diffWithBootTime)
        if (timeDifference > timeDiffThreshold) {
            _timeChangedFlow.emit(Unit)
        }

        latestDiffWithBootTime = diffWithBootTime
    }
}
