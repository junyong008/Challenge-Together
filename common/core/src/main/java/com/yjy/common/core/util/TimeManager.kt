package com.yjy.common.core.util

import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import kotlin.math.abs

interface TimeProvider {
    fun getCurrentTime(): LocalDateTime
    fun getBootTime(): Long
}

class DefaultTimeProvider @Inject constructor() : TimeProvider {
    override fun getCurrentTime(): LocalDateTime = LocalDateTime.now()
    override fun getBootTime(): Long = SystemClock.elapsedRealtime()
}

/**
 * [TimeManager]
 *
 * - 시간 변경을 실시간으로 감지하며 현재 시간을 주기적으로 방출하는 클래스
 * - 부팅 시간과의 차이를 지속적으로 측정하여 시간이 변경되었는지 확인
 *
 * @param timeProvider 테스트를 위해 별도 주입
 * @param updateInterval 시간 감지 및 현재 시간 방출 주기
 * @param timeDiffThreshold 부팅 시간과의 차이가 이 값보다 커지면 시간이 변경됐음을 감지
 */
class TimeManager @Inject constructor(
    private val timeProvider: TimeProvider,
    private val updateInterval: Long,
    private val timeDiffThreshold: Long,
) {
    private var latestDiffWithBootTime: Long? = null
    private var onTimeChanged: (() -> Unit)? = null

    private val _timeFlow = MutableSharedFlow<LocalDateTime>()
    val tickerFlow: Flow<LocalDateTime> = _timeFlow.asSharedFlow()

    fun startTicking(scope: CoroutineScope) {
        scope.launch {
            while (true) {
                emitCurrentTime()
                delay(updateInterval)
            }
        }
    }

    suspend fun emitCurrentTime() {
        val currentTime = timeProvider.getCurrentTime()
        val diffWithBootTime = calculateDiffWithBootTime(currentTime)
        detectTimeChange(diffWithBootTime)
        _timeFlow.emit(currentTime)
    }

    fun setOnTimeChanged(callback: () -> Unit) {
        onTimeChanged = callback
    }

    private fun calculateDiffWithBootTime(currentDateTime: LocalDateTime): Long {
        val currentDateTimeMillis = currentDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
        val currentBootTime = timeProvider.getBootTime()
        return currentDateTimeMillis - currentBootTime
    }

    private fun detectTimeChange(diffWithBootTime: Long) {
        val previousDiff = latestDiffWithBootTime ?: run {
            latestDiffWithBootTime = diffWithBootTime
            return
        }

        val timeDifference = abs(previousDiff - diffWithBootTime)
        if (timeDifference > timeDiffThreshold) { onTimeChanged?.invoke() }

        latestDiffWithBootTime = diffWithBootTime
    }
}
