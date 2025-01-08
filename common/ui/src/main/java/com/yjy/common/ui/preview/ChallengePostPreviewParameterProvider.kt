package com.yjy.common.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.yjy.model.challenge.ChallengePost
import com.yjy.model.common.Tier
import com.yjy.model.common.User
import java.time.LocalDateTime

class ChallengePostPreviewParameterProvider : PreviewParameterProvider<List<ChallengePost>> {
    override val values = sequenceOf(
        listOf(
            ChallengePost(
                postId = 1,
                writer = User(
                    name = "다이아몬드 챌린저",
                    tier = Tier.DIAMOND,
                ),
                content = "오늘도 열심히 운동했습니다! 💪",
                writtenDateTime = LocalDateTime.now().minusHours(1),
                isAuthor = true,
                isSynced = false,
            ),
            ChallengePost(
                postId = 2,
                writer = User(
                    name = "실버 도전자",
                    tier = Tier.SILVER,
                ),
                content = "날씨가 좋아서 달리기하기 좋았어요",
                writtenDateTime = LocalDateTime.now().minusHours(3),
                isAuthor = false,
                isSynced = true,
            ),
            ChallengePost(
                postId = 3,
                writer = User(
                    name = "골드러너",
                    tier = Tier.GOLD,
                ),
                content = "100일 동안 꾸준히 해보겠습니다",
                writtenDateTime = LocalDateTime.now().minusDays(1),
                isAuthor = false,
                isSynced = true,
            ),
            ChallengePost(
                postId = 4,
                writer = User(
                    name = "브론즈김",
                    tier = Tier.BRONZE,
                ),
                content = "처음이라 많이 서툴지만 열심히 하겠습니다!",
                writtenDateTime = LocalDateTime.now().minusDays(2),
                isAuthor = false,
                isSynced = true,
            ),
        ),
    )
}
