package com.yjy.common.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.yjy.model.challenge.ResetRecord
import java.time.LocalDateTime

class ResetRecordPreviewParameterProvider : PreviewParameterProvider<List<ResetRecord>> {
    override val values = sequenceOf(
        listOf(
            ResetRecord(
                id = 1,
                recordInSeconds = 86400L * 2,
                resetDateTime = LocalDateTime.now().minusDays(1),
                content = "",
                isCurrent = false,
            ),
            ResetRecord(
                id = 2,
                recordInSeconds = 86400L,
                resetDateTime = LocalDateTime.now().minusDays(3),
                content = "예시 리셋 내용",
                isCurrent = false,
            ),
            ResetRecord(
                id = 3,
                recordInSeconds = 86400L * 5,
                resetDateTime = LocalDateTime.now().minusDays(7),
                content = "예시 리셋 내용\n2번째 줄 내용\n3번째 줄 내용",
                isCurrent = false,
            ),
        ),
    )
}
