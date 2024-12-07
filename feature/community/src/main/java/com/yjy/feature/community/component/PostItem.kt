package com.yjy.feature.community.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.yjy.common.core.extensions.clickableSingle
import com.yjy.common.core.util.toDisplayTimeFormat
import com.yjy.common.designsystem.ComponentPreviews
import com.yjy.common.designsystem.component.CircleMedal
import com.yjy.common.designsystem.extensions.getDisplayColor
import com.yjy.common.designsystem.extensions.getDisplayName
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.feature.community.R
import com.yjy.model.common.Tier
import com.yjy.model.common.User
import com.yjy.model.community.SimpleCommunityPost
import java.time.LocalDateTime

@Composable
internal fun PostItem(
    post: SimpleCommunityPost,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    searchKeyword: String = "",
) {
    val contentAnnotatedString = buildAnnotatedString {
        val startIndex = post.content.indexOf(searchKeyword, ignoreCase = true)
        if (searchKeyword.isNotEmpty() && startIndex != -1) {
            append(post.content.substring(0, startIndex))
            withStyle(
                style = SpanStyle(
                    background = CustomColorProvider.colorScheme.brandBright,
                    color = CustomColorProvider.colorScheme.onBrandBright,
                ),
            ) {
                append(post.content.substring(startIndex, startIndex + searchKeyword.length))
            }
            append(post.content.substring(startIndex + searchKeyword.length))
        } else {
            append(post.content)
        }
    }

    Column(
        modifier = modifier
            .clickableSingle { onClick() }
            .background(CustomColorProvider.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserProfile(tier = post.writer.tier)
            Text(
                text = post.writer.getDisplayName(),
                color = post.writer.getDisplayColor(),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
            Text(
                text = if (post.modifiedDateTime > post.writtenDateTime) {
                    stringResource(
                        R.string.feature_community_post_edited,
                        post.modifiedDateTime.toDisplayTimeFormat(),
                    )
                } else {
                    post.writtenDateTime.toDisplayTimeFormat()
                },
                textAlign = TextAlign.End,
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = contentAnnotatedString,
            color = CustomColorProvider.colorScheme.onBackground,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                ImageVector.vectorResource(id = ChallengeTogetherIcons.Like),
                contentDescription = stringResource(id = R.string.feature_community_cheer),
                tint = CustomColorProvider.colorScheme.onBackgroundMuted,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = post.likeCount.toString(),
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                ImageVector.vectorResource(id = ChallengeTogetherIcons.Comment),
                contentDescription = stringResource(id = R.string.feature_community_comment),
                tint = CustomColorProvider.colorScheme.onBackgroundMuted,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = post.commentCount.toString(),
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}

@Composable
private fun UserProfile(
    tier: Tier,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.width(34.dp)) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(CustomColorProvider.colorScheme.surface)
                .padding(6.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircleMedal(tier = tier)
        }
    }
}

@ComponentPreviews
@Composable
fun PostItemPreview() {
    ChallengeTogetherTheme {
        PostItem(
            post = SimpleCommunityPost(
                postId = 1,
                writer = User(
                    name = "닉네임",
                    tier = Tier.BRONZE,
                ),
                content = "게시글 작성 내용",
                commentCount = 10,
                likeCount = 5,
                writtenDateTime = LocalDateTime.now().minusDays(3),
                modifiedDateTime = LocalDateTime.now().minusDays(1),
            ),
            onClick = {},
        )
    }
}
