package com.yjy.common.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.TargetDays
import java.time.LocalDateTime

class CompletedChallengePreviewParameterProvider : PreviewParameterProvider<List<SimpleStartedChallenge>> {
    override val values = sequenceOf(
        listOf(
            SimpleStartedChallenge(
                id = 1,
                title = "SNS 끊기",
                description = "SNS 끊기 설명",
                category = Category.QUIT_SNS,
                targetDays = TargetDays.Fixed(20),
                currentRecordInSeconds = 86400 * 14L,
                recentResetDateTime = LocalDateTime.now().minusDays(1),
                mode = Mode.CHALLENGE,
                isCompleted = true,
            ),
            SimpleStartedChallenge(
                id = 2,
                title = "게임 그만하기",
                description = "하루 30분 이상 하지 않기.",
                category = Category.QUIT_GAMING,
                targetDays = TargetDays.Fixed(15),
                currentRecordInSeconds = 86400 * 15L,
                recentResetDateTime = LocalDateTime.now(),
                mode = Mode.FREE,
                isCompleted = true,
            ),
        ),
    )
}
