package com.yjy.common.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.yjy.model.challenge.ChallengeRank
import com.yjy.model.challenge.core.TargetDays
import com.yjy.model.common.Tier
import com.yjy.model.common.User
import java.time.LocalDateTime

class ChallengeRankPreviewParameterProvider : PreviewParameterProvider<List<ChallengeRank>> {
    override val values = sequenceOf(
        listOf(
            ChallengeRank(
                rank = 1,
                scoreRank = 3,
                user = User(
                    name = "골드러너",
                    tier = Tier.GOLD,
                ),
                memberId = 2,
                currentRecordInSeconds = 86400L * 3,
                recoveryScore = 300,
                recentResetDateTime = LocalDateTime.now(),
                targetDays = TargetDays.Infinite,
                isInActive = true,
                isComplete = false,
                isMine = false,
            ),
            ChallengeRank(
                rank = 1,
                scoreRank = 4,
                user = User(
                    name = "브론즈김",
                    tier = Tier.BRONZE,
                ),
                memberId = 3,
                currentRecordInSeconds = 86400L * 3,
                recoveryScore = 30,
                recentResetDateTime = LocalDateTime.now(),
                targetDays = TargetDays.Infinite,
                isInActive = false,
                isComplete = false,
                isMine = false,
            ),
            ChallengeRank(
                rank = 3,
                scoreRank = 2,
                user = User(
                    name = "실버 도전자",
                    tier = Tier.SILVER,
                ),
                memberId = 1,
                currentRecordInSeconds = 86400L,
                recoveryScore = 450,
                recentResetDateTime = LocalDateTime.now(),
                targetDays = TargetDays.Infinite,
                isInActive = false,
                isComplete = false,
                isMine = true,
            ),
            ChallengeRank(
                rank = 4,
                scoreRank = 1,
                user = User(
                    name = "완료한 다이아",
                    tier = Tier.DIAMOND,
                ),
                memberId = 1,
                currentRecordInSeconds = 86400L,
                recoveryScore = 1588,
                recentResetDateTime = LocalDateTime.now(),
                targetDays = TargetDays.Fixed(1),
                isInActive = false,
                isComplete = true,
                isMine = true,
            ),
        ),
    )
}
