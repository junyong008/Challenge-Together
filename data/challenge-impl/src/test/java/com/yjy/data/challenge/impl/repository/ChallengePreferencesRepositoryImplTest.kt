package com.yjy.data.challenge.impl.repository

import com.yjy.data.challenge.impl.mapper.toProto
import com.yjy.data.challenge.impl.util.TimeManager
import com.yjy.data.datastore.api.ChallengePreferencesDataSource
import com.yjy.data.network.datasource.ChallengeDataSource
import com.yjy.model.challenge.core.SortOrder
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import java.time.LocalDateTime
import kotlin.test.Test

class ChallengePreferencesRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var challengeDataSource: ChallengeDataSource
    private lateinit var challengePreferencesDataSource: ChallengePreferencesDataSource
    private lateinit var timeManager: TimeManager
    private lateinit var challengePreferencesRepositoryImpl: ChallengePreferencesRepositoryImpl

    private val sortOrderFlow = MutableStateFlow(SortOrder.LATEST.toProto())
    private val tickerFlow = MutableStateFlow(LocalDateTime.now())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        challengeDataSource = mockk(relaxed = true)
        challengePreferencesDataSource = mockk(relaxed = true)
        timeManager = mockk(relaxed = true)

        coEvery { challengePreferencesDataSource.sortOrder } returns sortOrderFlow
        coEvery { timeManager.tickerFlow } returns tickerFlow

        challengePreferencesRepositoryImpl = ChallengePreferencesRepositoryImpl(
            challengePreferencesDataSource = challengePreferencesDataSource,
            challengeDataSource = challengeDataSource,
            timeManager = timeManager,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setSortOrder should call ChallengePreferencesDataSource with correct order`() = runTest {
        // Given
        val order = SortOrder.LATEST

        // When
        challengePreferencesRepositoryImpl.setSortOrder(order)

        // Then
        coVerify { challengePreferencesDataSource.setSortOrder(order.toProto()) }
    }

    @Test
    fun `clearRecentCompletedChallenges should clear recent challenges in preferences`() = runTest {
        // When
        challengePreferencesRepositoryImpl.clearRecentCompletedChallenges()

        // Then
        coVerify { challengePreferencesDataSource.setCompletedChallengeTitles(emptyList()) }
    }
}
