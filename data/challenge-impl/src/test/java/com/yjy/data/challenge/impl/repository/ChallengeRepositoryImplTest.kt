package com.yjy.data.challenge.impl.repository

import com.yjy.common.network.NetworkResult
import com.yjy.data.challenge.impl.mapper.toProto
import com.yjy.data.challenge.impl.util.TimeManager
import com.yjy.data.database.dao.ChallengeDao
import com.yjy.data.database.model.ChallengeEntity
import com.yjy.data.datastore.api.ChallengePreferencesDataSource
import com.yjy.data.network.datasource.ChallengeDataSource
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.data.network.response.AddChallengeResponse
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.SortOrder
import com.yjy.model.challenge.core.TargetDays
import com.yjy.model.common.Tier
import io.mockk.coEvery
import io.mockk.coVerify
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

class ChallengeRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var challengeDataSource: ChallengeDataSource
    private lateinit var challengePreferencesDataSource: ChallengePreferencesDataSource
    private lateinit var challengeDao: ChallengeDao
    private lateinit var timeManager: TimeManager
    private lateinit var challengeRepository: ChallengeRepositoryImpl

    private val challengesFlow = MutableStateFlow(emptyList<ChallengeEntity>())
    private val sortOrderFlow = MutableStateFlow(SortOrder.LATEST.toProto())
    private val tickerFlow = MutableStateFlow(LocalDateTime.now())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        challengeDataSource = mockk(relaxed = true)
        challengePreferencesDataSource = mockk(relaxed = true)
        challengeDao = mockk(relaxed = true)
        timeManager = mockk(relaxed = true)

        coEvery { challengeDao.getAll() } returns challengesFlow
        coEvery { challengePreferencesDataSource.sortOrder } returns sortOrderFlow
        coEvery { timeManager.tickerFlow } returns tickerFlow

        challengeRepository = ChallengeRepositoryImpl(
            challengePreferencesDataSource = challengePreferencesDataSource,
            challengeDataSource = challengeDataSource,
            challengeDao = challengeDao,
            timeManager = timeManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createChallengeEntity(
        id: String = "1",
        title: String = "Test Challenge",
        isStarted: Boolean = true,
        resetDateTime: LocalDateTime = LocalDateTime.now().minusDays(3)
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
        val challengeEntity = createChallengeEntity(id = "1", resetDateTime = now.minusDays(5))
        challengesFlow.emit(listOf(challengeEntity))

        var startedChallenges: List<SimpleStartedChallenge>? = null

        // When
        val job = launch {
            challengeRepository.startedChallenges.collect { startedChallenges = it }
        }

        // Then
        advanceUntilIdle()
        assertNotNull(startedChallenges)
        assertEquals(1, startedChallenges?.size)
        assertEquals("1", startedChallenges?.first()?.id)

        job.cancel()
    }

    @Test
    fun `sortOrder should apply correct sorting order`() = runTest {
        // Given
        val challenge1 = createChallengeEntity(
            id = "1",
            title = "Z Challenge",
            resetDateTime = LocalDateTime.now().minusDays(3),
        )
        val challenge2 = createChallengeEntity(
            id = "2",
            title = "X Challenge",
            resetDateTime = LocalDateTime.now().minusDays(10),
        )

        challengesFlow.emit(listOf(challenge1, challenge2))

        var startedChallenges: List<SimpleStartedChallenge>? = null

        // When
        val job = launch {
            challengeRepository.startedChallenges.collect { startedChallenges = it }
        }

        advanceUntilIdle()
        assertEquals(listOf("2", "1"), startedChallenges?.map { it.id })

        // Then: 과거 순으로 정렬
        sortOrderFlow.emit(SortOrder.OLDEST.toProto())
        advanceUntilIdle()
        assertEquals(listOf("1", "2"), startedChallenges?.map { it.id })

        // Then: 제목 순으로 정렬
        sortOrderFlow.emit(SortOrder.TITLE.toProto())
        advanceUntilIdle()
        assertEquals(listOf("2", "1"), startedChallenges?.map { it.id })

        job.cancel()
    }

    @Test
    fun `syncChallenges should update challenges and completed titles`() = runTest {
        // Given
        val expectedNewTitles = listOf("Challenge 1", "Challenge 2")
        coEvery { challengeDataSource.getMyChallenges() } returns NetworkResult.Success(
            mockk {
                coEvery { challenges } returns listOf()
                coEvery { newlyCompletedTitles } returns expectedNewTitles
            }
        )

        // When
        val result = challengeRepository.syncChallenges()

        // Then
        coVerify { challengeDataSource.getMyChallenges() }
        coVerify { challengePreferencesDataSource.addCompletedChallengeTitles(expectedNewTitles) }
        assertEquals(NetworkResult.Success(expectedNewTitles), result)
    }

    @Test
    fun `addChallenge should call data source with correct parameters and return challengeId`() = runTest {
        // Given
        val category = Category.QUIT_SMOKING
        val title = "Quit Smoking"
        val description = "Quit Smoking Description."
        val targetDays = TargetDays.Fixed(30)
        val startDateTime = LocalDateTime.of(2023, 10, 13, 10, 0)
        val maxParticipants = 10
        val roomPassword = "password123"
        val expectedChallengeId = "777"

        coEvery {
            challengeDataSource.addChallenge(
                AddChallengeRequest(
                    category = "ic_smoke",
                    title = title,
                    description = description,
                    targetDays = "30",
                    startDateTime = "2023-10-13 10:00:00",
                    maxParticipants = "10",
                    password = roomPassword,
                ),
            )
        } returns NetworkResult.Success(AddChallengeResponse(expectedChallengeId))

        // When
        val result = challengeRepository.addChallenge(
            category = category,
            title = title,
            description = description,
            targetDays = targetDays,
            startDateTime = startDateTime,
            maxParticipants = maxParticipants,
            roomPassword = roomPassword,
        )

        // Then
        coVerify {
            challengeDataSource.addChallenge(
                AddChallengeRequest(
                    category = "ic_smoke",
                    title = title,
                    description = description,
                    targetDays = "30",
                    startDateTime = "2023-10-13 10:00:00",
                    maxParticipants = "10",
                    password = roomPassword,
                ),
            )
        }

        assertEquals(NetworkResult.Success(expectedChallengeId), result)
    }

    @Test
    fun `setCurrentTier should call ChallengePreferencesDataSource with correct tier`() = runTest {
        // Given
        val tier = Tier.BRONZE

        // When
        challengeRepository.setCurrentTier(tier)

        // Then
        coVerify { challengePreferencesDataSource.setCurrentTier(tier.toProto()) }
    }

    @Test
    fun `setSortOrder should call ChallengePreferencesDataSource with correct order`() = runTest {
        // Given
        val order = SortOrder.LATEST

        // When
        challengeRepository.setSortOrder(order)

        // Then
        coVerify { challengePreferencesDataSource.setSortOrder(order.toProto()) }
    }

    @Test
    fun `clearRecentCompletedChallenges should clear recent challenges in preferences`() = runTest {
        // When
        challengeRepository.clearRecentCompletedChallenges()

        // Then
        coVerify { challengePreferencesDataSource.setCompletedChallengeTitles(emptyList()) }
    }

    @Test
    fun `syncChallenges should sync challenges and return newly completed titles`() = runTest {
        // Given
        val expectedNewTitles = listOf("Challenge 1", "Challenge 2")
        coEvery { challengeDataSource.getMyChallenges() } returns NetworkResult.Success(
            mockk {
                coEvery { challenges } returns listOf()
                coEvery { newlyCompletedTitles } returns expectedNewTitles
            },
        )

        // When
        val result = challengeRepository.syncChallenges()

        // Then
        coVerify { challengeDataSource.getMyChallenges() }
        coVerify { challengePreferencesDataSource.addCompletedChallengeTitles(expectedNewTitles) }
        assertEquals(NetworkResult.Success(expectedNewTitles), result)
    }
}
