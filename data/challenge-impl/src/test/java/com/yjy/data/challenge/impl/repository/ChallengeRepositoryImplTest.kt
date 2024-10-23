package com.yjy.data.challenge.impl.repository

import com.yjy.common.network.NetworkResult
import com.yjy.data.challenge.impl.mapper.toRequestString
import com.yjy.data.network.datasource.ChallengeDataSource
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.data.network.response.AddChallengeResponse
import com.yjy.model.challenge.Category
import com.yjy.model.challenge.TargetDays
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
    private lateinit var challengeRepository: ChallengeRepositoryImpl

    @Before
    fun setup() {
        challengeDataSource = mockk()
        challengeRepository = ChallengeRepositoryImpl(challengeDataSource)
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
    fun `toRequestString should return correct values for Category`() {
        assertEquals("ic_smoke", Category.QUIT_SMOKING.toRequestString())
        assertEquals("ic_drink", Category.QUIT_DRINKING.toRequestString())
        assertEquals("ic_game", Category.QUIT_GAMING.toRequestString())
    }

    @Test
    fun `toRequestString should return correct values for TargetDays`() {
        assertEquals("30", TargetDays.Fixed(30).toRequestString())
        assertEquals("36500", TargetDays.Infinite.toRequestString())
    }

    @Test
    fun `toRequestString should return correct formatted date for LocalDateTime`() {
        val dateTime = LocalDateTime.of(2023, 10, 13, 10, 0)
        val expectedFormat = "2023-10-13 10:00:00"
        assertEquals(expectedFormat, dateTime.toRequestString())
    }
}
