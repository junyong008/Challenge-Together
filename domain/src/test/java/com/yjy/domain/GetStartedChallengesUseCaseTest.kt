package com.yjy.domain

import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.TargetDays
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class GetStartedChallengesUseCaseTest {

    private lateinit var challengeRepository: ChallengeRepository
    private lateinit var userRepository: UserRepository
    private lateinit var getStartedChallengesUseCase: GetStartedChallengesUseCase

    @Before
    fun setUp() {
        challengeRepository = mockk()
        userRepository = mockk()
        getStartedChallengesUseCase = GetStartedChallengesUseCase(
            challengeRepository = challengeRepository,
            userRepository = userRepository,
        )
    }

    @Test
    fun `invoke should return started challenges with time diff applied`() = runTest {
        // Given
        val initialTime = LocalDateTime.of(2023, 10, 13, 10, 0)
        val currentRecordInSeconds = 7000L
        val timeDiff = 3600L

        val startedChallenges = listOf(
            SimpleStartedChallenge(
                id = "1",
                title = "Challenge 1",
                description = "A test challenge",
                category = Category.QUIT_SMOKING,
                targetDays = TargetDays.Fixed(30),
                mode = Mode.CHALLENGE,
                recentResetDateTime = initialTime,
                currentRecordInSeconds = currentRecordInSeconds,
                isCompleted = false,
            ),
            SimpleStartedChallenge(
                id = "2",
                title = "Challenge 2",
                description = "Another test challenge",
                category = Category.QUIT_DRINKING,
                targetDays = TargetDays.Fixed(60),
                mode = Mode.CHALLENGE,
                recentResetDateTime = initialTime.plusDays(1),
                currentRecordInSeconds = currentRecordInSeconds,
                isCompleted = false,
            ),
        )

        every { challengeRepository.startedChallenges } returns flowOf(startedChallenges)
        every { userRepository.timeDiff } returns flowOf(timeDiff)

        // When
        val result: List<SimpleStartedChallenge> = getStartedChallengesUseCase().first()

        // Then
        val expectedChallenges = startedChallenges.map { challenge ->
            challenge.copy(
                recentResetDateTime = challenge.recentResetDateTime.plusSeconds(timeDiff),
                currentRecordInSeconds = challenge.currentRecordInSeconds.minus(timeDiff),
            )
        }
        assertEquals(expectedChallenges, result)
    }
}
