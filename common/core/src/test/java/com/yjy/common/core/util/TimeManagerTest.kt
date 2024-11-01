package com.yjy.common.core.util

// TimeManagerTest.kt

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TimeManagerTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var timeManager: TimeManager
    private lateinit var timeProvider: FakeTimeProvider

    companion object {
        private const val DEFAULT_UPDATE_INTERVAL = 1000L
        private const val DEFAULT_TIME_DIFF_THRESHOLD = 5000L
        private val INITIAL_TIME = LocalDateTime.of(2024, 1, 1, 12, 0)
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        timeProvider = FakeTimeProvider()
        timeManager = DefaultTimeManager(
            timeProvider = timeProvider,
            updateInterval = DEFAULT_UPDATE_INTERVAL,
            timeDiffThreshold = DEFAULT_TIME_DIFF_THRESHOLD,
            dispatcher = testDispatcher,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `timeChangedFlow should emit event when time changed`() = runTest {
        // Given
        val tickerJob = launch { timeManager.tickerFlow.collect { } }

        var timeChangedEventEmitted = false
        val timeChangedJob = launch {
            timeManager.timeChangedFlow.collect {
                timeChangedEventEmitted = true
            }
        }

        // When: 부팅 시간의 흐름과 달리 현재 시간은 1분이 증가한 상황을 테스트
        timeProvider.setCurrentTime(INITIAL_TIME)
        timeProvider.setBootTime(0L)
        advanceTimeBy(DEFAULT_UPDATE_INTERVAL)

        timeProvider.setCurrentTime(INITIAL_TIME.plusMinutes(1))
        timeProvider.setBootTime(1000L)
        advanceTimeBy(DEFAULT_UPDATE_INTERVAL)

        // Then: 시간 변경 이벤트가 발생해야 함
        assertTrue(timeChangedEventEmitted)

        timeChangedJob.cancel()
        tickerJob.cancel()
    }

    @Test
    fun `timeChangedFlow should not emit event when time difference is within threshold`() = runTest {
        // Given
        val tickerJob = launch { timeManager.tickerFlow.collect { } }

        var timeChangedEventEmitted = false
        val timeChangedJob = launch {
            timeManager.timeChangedFlow.collect {
                timeChangedEventEmitted = true
            }
        }

        // When: 시간이 임계값 이내로 변경
        timeProvider.setCurrentTime(INITIAL_TIME)
        timeProvider.setBootTime(0L)
        advanceTimeBy(DEFAULT_UPDATE_INTERVAL)

        timeProvider.setCurrentTime(INITIAL_TIME.plusSeconds(1))
        timeProvider.setBootTime(1000L)
        advanceTimeBy(DEFAULT_UPDATE_INTERVAL)

        // Then: 시간 변경 이벤트가 발생하지 않아야 함
        assertFalse(timeChangedEventEmitted)

        timeChangedJob.cancel()
        tickerJob.cancel()
    }

    @Test
    fun `tickerFlow emits current time at regular intervals`() = runTest {
        // Given
        val emittedTimes = mutableListOf<LocalDateTime>()
        timeProvider.setCurrentTime(INITIAL_TIME)

        val tickerJob = launch {
            timeManager.tickerFlow.collect {
                emittedTimes.add(it)
            }
        }

        // When & Then: interval 간격으로 emit되어야 함
        advanceTimeBy(DEFAULT_UPDATE_INTERVAL)
        assertEquals(1, emittedTimes.size)
        assertEquals(INITIAL_TIME, emittedTimes[0])

        advanceTimeBy(DEFAULT_UPDATE_INTERVAL)
        assertEquals(2, emittedTimes.size)

        advanceTimeBy(DEFAULT_UPDATE_INTERVAL)
        assertEquals(3, emittedTimes.size)

        tickerJob.cancel()
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
