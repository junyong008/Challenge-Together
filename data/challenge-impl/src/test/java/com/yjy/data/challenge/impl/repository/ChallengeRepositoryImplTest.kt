package com.yjy.data.challenge.impl.repository

import com.yjy.common.network.NetworkResult
import com.yjy.data.challenge.impl.mapper.toProto
import com.yjy.data.database.dao.ChallengeDao
import com.yjy.data.database.model.ChallengeEntity
import com.yjy.data.datastore.api.ChallengePreferencesDataSource
import com.yjy.data.network.datasource.ChallengeDataSource
import com.yjy.data.network.request.challenge.AddChallengeRequest
import com.yjy.data.network.response.challenge.AddChallengeResponse
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.SortOrder
import com.yjy.model.challenge.core.TargetDays
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
import kotlin.test.assertEquals

class ChallengeRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var challengeDataSource: ChallengeDataSource
    private lateinit var challengePreferencesDataSource: ChallengePreferencesDataSource
    private lateinit var challengeDao: ChallengeDao
    private lateinit var challengeRepository: ChallengeRepositoryImpl

    private val challengesFlow = MutableStateFlow(emptyList<ChallengeEntity>())
    private val sortOrderFlow = MutableStateFlow(SortOrder.LATEST.toProto())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        challengeDataSource = mockk(relaxed = true)
        challengePreferencesDataSource = mockk(relaxed = true)
        challengeDao = mockk(relaxed = true)

        coEvery { challengeDao.getAll() } returns challengesFlow
        coEvery { challengePreferencesDataSource.sortOrder } returns sortOrderFlow

        challengeRepository = ChallengeRepositoryImpl(
            challengePreferencesDataSource = challengePreferencesDataSource,
            challengeDataSource = challengeDataSource,
            challengeDao = challengeDao,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `syncChallenges should update challenges and completed titles`() = runTest {
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
        assertEquals(NetworkResult.Success(Unit), result)
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
        val expectedChallengeId = 777

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
                    languageCode = "ko",
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
            languageCode = "ko",
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
                    languageCode = "ko",
                ),
            )
        }

        assertEquals(NetworkResult.Success(expectedChallengeId), result)
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
        assertEquals(NetworkResult.Success(Unit), result)
    }
}
