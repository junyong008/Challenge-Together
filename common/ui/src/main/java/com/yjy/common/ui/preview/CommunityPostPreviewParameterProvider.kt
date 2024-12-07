package com.yjy.common.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.yjy.model.common.Tier
import com.yjy.model.common.User
import com.yjy.model.common.UserStatus
import com.yjy.model.community.SimpleCommunityPost
import java.time.LocalDateTime

class CommunityPostPreviewParameterProvider : PreviewParameterProvider<List<SimpleCommunityPost>> {
    override val values = sequenceOf(
        listOf(
            SimpleCommunityPost(
                postId = 1,
                writer = User(
                    name = "닉네임",
                    tier = Tier.BRONZE,
                ),
                content = "게시글 작성 내용",
                commentCount = 10,
                likeCount = 5,
                writtenDateTime = LocalDateTime.now(),
                modifiedDateTime = LocalDateTime.now().minusDays(1),
            ),
            SimpleCommunityPost(
                postId = 2,
                writer = User(
                    name = "사람",
                    tier = Tier.BRONZE,
                    status = UserStatus.WITHDRAWN,
                ),
                content = "탈퇴한 회원의 작성 게시글",
                commentCount = 10,
                likeCount = 5,
                writtenDateTime = LocalDateTime.now(),
                modifiedDateTime = LocalDateTime.now().plusDays(3),
            ),
        ),
    )
}
