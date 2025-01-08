package com.yjy.common.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.yjy.model.common.notification.Notification
import com.yjy.model.common.notification.NotificationType
import java.time.LocalDateTime

class NotificationPreviewParameterProvider : PreviewParameterProvider<List<Notification>> {
    override val values = sequenceOf(
        listOf(
            Notification(
                id = 1,
                header = "챌린지 제목",
                body = "리셋터",
                createdDateTime = LocalDateTime.now(),
                type = NotificationType.CHALLENGE_RESET,
                linkId = 0,
            ),
            Notification(
                id = 2,
                header = "챌린지 제목",
                body = "게으르미",
                createdDateTime = LocalDateTime.now(),
                type = NotificationType.CHALLENGE_FORCE_REMOVE,
                linkId = 0,
            ),
            Notification(
                id = 3,
                header = "챌린지 제목",
                body = "수다쟁이: 가나다라마바사",
                createdDateTime = LocalDateTime.now(),
                type = NotificationType.CHALLENGE_STARTED_POST,
                linkId = 0,
            ),
        ),
    )
}
