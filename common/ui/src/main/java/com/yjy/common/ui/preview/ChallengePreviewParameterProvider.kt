package com.yjy.common.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.SimpleWaitingChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.TargetDays
import java.time.LocalDateTime

class ChallengePreviewParameterProvider :
    PreviewParameterProvider<Pair<List<SimpleStartedChallenge>, List<SimpleWaitingChallenge>>> {
    override val values = sequenceOf(
        createSampleChallenges(),
    )

    private fun createSampleChallenges() = Pair(
        // Started Challenges
        listOf(
            SimpleStartedChallenge(
                id = 1,
                title = "SNS 끊기",
                description = "SNS 끊기 설명",
                category = Category.QUIT_SNS,
                targetDays = TargetDays.Fixed(30),
                currentRecordInSeconds = 86400 * 14L,
                recentResetDateTime = LocalDateTime.now().minusDays(1),
                mode = Mode.CHALLENGE,
                isCompleted = false,
            ),
            SimpleStartedChallenge(
                id = 2,
                title = "게임 그만하기",
                description = "하루 30분 이상 하지 않기.",
                category = Category.QUIT_GAMING,
                targetDays = TargetDays.Fixed(60),
                currentRecordInSeconds = 72000L,
                recentResetDateTime = LocalDateTime.now(),
                mode = Mode.FREE,
                isCompleted = false,
            ),
        ),
        // Waiting Challenges
        listOf(
            SimpleWaitingChallenge(
                id = 3,
                title = "같이 배달 음식 끊기",
                description = "같이 배달 음식 끊기 설명.\n배달 음식을 같이 끊는 도전입니다.",
                category = Category.QUIT_EATING_OUT,
                targetDays = TargetDays.Fixed(30),
                isPrivate = false,
                currentParticipantCounts = 3,
                maxParticipantCounts = 5,
            ),
            SimpleWaitingChallenge(
                id = 4,
                title = "글루텐 끊기",
                description = "하루 물 2L 마시기",
                category = Category.QUIT_GLUTEN,
                targetDays = TargetDays.Infinite,
                isPrivate = true,
                currentParticipantCounts = 2,
                maxParticipantCounts = 4,
            ),
        ),
    )
}
