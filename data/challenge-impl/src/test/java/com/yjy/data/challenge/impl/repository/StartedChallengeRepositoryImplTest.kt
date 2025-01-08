package com.yjy.data.challenge.impl.repository

import com.yjy.data.challenge.impl.util.TimeManager
import com.yjy.data.database.dao.ChallengeDao
import com.yjy.data.database.model.ChallengeEntity
import com.yjy.data.network.datasource.StartedChallengeDataSource
import com.yjy.model.challenge.SimpleStartedChallenge
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class StartedChallengeRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var startedChallengeDataSource: StartedChallengeDataSource
    private lateinit var challengeDao: ChallengeDao
    private lateinit var timeManager: TimeManager
    private lateinit var startedChallengeRepository: StartedChallengeRepositoryImpl

    private val challengesFlow = MutableStateFlow(emptyList<ChallengeEntity>())
    private val tickerFlow = MutableStateFlow(LocalDateTime.now())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        startedChallengeDataSource = mockk(relaxed = true)
        challengeDao = mockk(relaxed = true)
        timeManager = mockk(relaxed = true)

        coEvery { challengeDao.getAll() } returns challengesFlow
        coEvery { timeManager.tickerFlow } returns tickerFlow

        startedChallengeRepository = StartedChallengeRepositoryImpl(
            startedChallengeDataSource = startedChallengeDataSource,
            challengeDao = challengeDao,
            timeManager = timeManager,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createChallengeEntity(
        id: Int = 1,
        title: String = "Test Challenge",
        isStarted: Boolean = true,
        resetDateTime: LocalDateTime = LocalDateTime.now().minusDays(3),
    ) = ChallengeEntity(
        id = id,
        title = title,
        description = "Description",
        category = "ic_smoke",
        targetDays = 30,
        currentParticipantCount = 1,
        maxParticipantCount = 10,
        recentResetDateTime = resetDateTime,
        isStarted = isStarted,
        isPrivate = false,
        isCompleted = false,
        mode = "CHALLENGE",
    )

    @Test
    fun `startedChallenges should filter and map challenges correctly`() = runTest {
        // Given
        val now = LocalDateTime.of(2024, 1, 1, 12, 0)
        val challengeEntity = createChallengeEntity(id = 1, resetDateTime = now.minusDays(5))
        challengesFlow.emit(listOf(challengeEntity))

        var startedChallenges: List<SimpleStartedChallenge>? = null

        // When
        val job = launch {
            startedChallengeRepository.startedChallenges.collect { startedChallenges = it }
        }

        // Then
        advanceUntilIdle()
        assertNotNull(startedChallenges)
        assertEquals(1, startedChallenges?.size)
        assertEquals(1, startedChallenges?.first()?.id)

        job.cancel()
    }
}
