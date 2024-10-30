package com.yjy.common.core.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TimeManagerTest {

    private lateinit var timeManager: TimeManager
    private lateinit var timeProvider: FakeTimeProvider

    @Before
    fun setup() {
        timeProvider = FakeTimeProvider()
        timeManager = TimeManager(
            timeProvider = timeProvider,
            updateInterval = 1000L,
            timeDiffThreshold = 5000L
        )
    }

    @Test
    fun `detectTimeChange calls callback when time difference exceeds threshold`() = runTest {
        // Given
        var callbackCalled = false
        timeManager.setOnTimeChanged { callbackCalled = true }

        // 초기 시간 설정
        val initialTime = LocalDateTime.of(2024, 1, 1, 12, 0)
        timeProvider.setCurrentTime(initialTime)
        timeProvider.setBootTime(0L)

        // 첫 번째 시간 체크 (기준점 설정)
        timeManager.emitCurrentTime()
        assertFalse(callbackCalled)

        // When: 시간이 변경됨 (부팅 시간과 현재 시간의 차이가 이전과 달리 벌어짐)
        timeProvider.setCurrentTime(initialTime.plusMinutes(1)) // 현재 시간이 1분 추가됨
        timeProvider.setBootTime(1000L)  // 부팅 시간은 1초만 증가

        // Then
        timeManager.emitCurrentTime()
        assertTrue(callbackCalled)
    }

    @Test
    fun `detectTimeChange does not call callback when time difference is within threshold`() = runTest {
        // Given
        var callbackCalled = false
        timeManager.setOnTimeChanged { callbackCalled = true }

        // 초기 시간 설정
        val initialTime = LocalDateTime.of(2024, 1, 1, 12, 0)
        timeProvider.setCurrentTime(initialTime)
        timeProvider.setBootTime(0L)

        // 첫 번째 시간 체크 (기준점 설정)
        timeManager.emitCurrentTime()
        assertFalse(callbackCalled)

        // When: 시간이 변경 되지 않음 (부팅 시간과 현재 시간의 차이가 동일함)
        timeProvider.setCurrentTime(initialTime.plusSeconds(3))
        timeProvider.setBootTime(3000L)

        // Then
        timeManager.emitCurrentTime()
        assertFalse(callbackCalled)
    }

    @Test
    fun `startTicking emits current time at regular intervals`() = runTest {
        // Given
        val scope = TestScope(testScheduler)
        val updateInterval = 500L
        timeManager = TimeManager(
            timeProvider = timeProvider,
            updateInterval = updateInterval,
            timeDiffThreshold = 5000L,
        )

        // 초기 시간 설정
        val initialTime = LocalDateTime.of(2024, 1, 1, 12, 0)
        timeProvider.setCurrentTime(initialTime)

        // 방출 되는 시간을 모아 추후 계산
        val emittedTimes = mutableListOf<LocalDateTime>()
        val collectJob = launch {
            timeManager.tickerFlow.collect { emittedTimes.add(it) }
        }

        // When
        timeManager.startTicking(scope)
        runCurrent()
        emittedTimes.clear() // 생성 시 방출 되는 부분을 지워 계산 혼동 방지

        // 3초 동안 실제 시간의 흐름을 모의 하여 시간 변경
        repeat(3) { second ->
            timeProvider.setCurrentTime(initialTime.plusSeconds(second + 1L))
            advanceTimeBy(1000L)
            runCurrent()
        }

        // Then: 시간 방출 주기가 500ms 이므로, 3초간 6번의 방출이 이뤄 져야 함
        assertEquals(6, emittedTimes.size)

        collectJob.cancelAndJoin()
        scope.cancel()
    }

    private class FakeTimeProvider : TimeProvider {
        private var currentDateTime: LocalDateTime = LocalDateTime.now()
        private var bootTimeMillis: Long = 0L

        override fun getCurrentTime(): LocalDateTime = currentDateTime
        override fun getBootTime(): Long = bootTimeMillis

        fun setCurrentTime(time: LocalDateTime) {
            currentDateTime = time
        }

        fun setBootTime(time: Long) {
            bootTimeMillis = time
        }
    }
}
