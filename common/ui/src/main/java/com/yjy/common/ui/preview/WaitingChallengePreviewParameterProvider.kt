package com.yjy.common.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.yjy.model.challenge.SimpleWaitingChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.TargetDays

class WaitingChallengePreviewParameterProvider : PreviewParameterProvider<List<SimpleWaitingChallenge>> {
    override val values = sequenceOf(
        listOf(
            SimpleWaitingChallenge(
                id = 1,
                title = "SNS 끊기",
                description = "SNS 끊기 설명",
                category = Category.QUIT_SNS,
                targetDays = TargetDays.Fixed(20),
                currentParticipantCounts = 3,
                maxParticipantCounts = 10,
                isPrivate = true,
            ),
            SimpleWaitingChallenge(
                id = 2,
                title = "게임 그만하기",
                description = "하루 30분 이상 하지 않기.",
                category = Category.QUIT_GAMING,
                targetDays = TargetDays.Infinite,
                currentParticipantCounts = 5,
                maxParticipantCounts = 10,
                isPrivate = false,
            ),
        ),
    )
}
