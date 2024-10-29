package com.yjy.data.challenge.impl.repository

import com.yjy.common.network.NetworkResult
import com.yjy.data.challenge.impl.mapper.toProto
import com.yjy.data.database.dao.ChallengeDao
import com.yjy.data.datastore.api.ChallengePreferencesDataSource
import com.yjy.data.network.datasource.ChallengeDataSource
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.data.network.response.AddChallengeResponse
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.SortOrder
import com.yjy.model.challenge.core.TargetDays
import com.yjy.model.common.Tier
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class ChallengeRepositoryImplTest {

    private lateinit var challengeDataSource: ChallengeDataSource
    private lateinit var challengePreferencesDataSource: ChallengePreferencesDataSource
    private lateinit var challengeDao: ChallengeDao
    private lateinit var challengeRepository: ChallengeRepositoryImpl

    @Before
    fun setup() {
        challengeDataSource = mockk()
        challengePreferencesDataSource = mockk(relaxed = true)
        challengeDao = mockk(relaxed = true)

        challengeRepository = ChallengeRepositoryImpl(
            challengePreferencesDataSource = challengePreferencesDataSource,
            challengeDataSource = challengeDataSource,
            challengeDao = challengeDao,
        )
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
                    password = roomPassword
                )
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
            roomPassword = roomPassword
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
                    password = roomPassword
                )
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
            }
        )

        // When
        val result = challengeRepository.syncChallenges()

        // Then
        coVerify { challengeDataSource.getMyChallenges() }
        coVerify { challengePreferencesDataSource.addCompletedChallengeTitles(expectedNewTitles) }
        assertEquals(NetworkResult.Success(expectedNewTitles), result)
    }
}
