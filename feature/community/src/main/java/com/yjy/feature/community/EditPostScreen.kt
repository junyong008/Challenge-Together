package com.yjy.feature.community

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.constants.CommunityConst.MAX_POST_CONTENT_LENGTH
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherButton
import com.yjy.common.designsystem.component.ChallengeTogetherTextField
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.community.component.Guidelines
import com.yjy.feature.community.model.CommunityUiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun EditPostRoute(
    postId: Int,
    content: String,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CommunityViewModel = hiltViewModel(),
) {
    val isEditingPost by viewModel.isEditingPost.collectAsStateWithLifecycle()

    EditPostScreen(
        modifier = modifier,
        postId = postId,
        content = content,
        isEditingPost = isEditingPost,
        uiEvent = viewModel.uiEvent,
        onBackClick = onBackClick,
        onEditPostClick = viewModel::editPost,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun EditPostScreen(
    modifier: Modifier = Modifier,
    postId: Int = 0,
    content: String = "",
    isEditingPost: Boolean = false,
    uiEvent: Flow<CommunityUiEvent> = flowOf(),
    onBackClick: () -> Unit = {},
    onEditPostClick: (postId: Int, content: String) -> Unit = { _, _ -> },
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val editSuccessMessage = stringResource(id = R.string.feature_community_post_edit_success)
    val editFailureMessage = stringResource(id = R.string.feature_community_post_edit_failure)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            CommunityUiEvent.EditSuccess -> {
                onShowSnackbar(SnackbarType.SUCCESS, editSuccessMessage)
                onBackClick()
            }

            CommunityUiEvent.EditFailure ->
                onShowSnackbar(SnackbarType.ERROR, editFailureMessage)

            else -> Unit
        }
    }

    var currentContent by rememberSaveable { mutableStateOf(content) }

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                onNavigationClick = onBackClick,
                titleRes = R.string.feature_community_post_edit_title,
            )
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier.consumeWindowInsets(WindowInsets.navigationBars),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            ChallengeTogetherTextField(
                value = currentContent,
                onValueChange = { currentContent = it.take(MAX_POST_CONTENT_LENGTH) },
                placeholderText = stringResource(
                    id = R.string.feature_community_post_create_content_placeholder,
                ),
                shape = RectangleShape,
                enabled = !isEditingPost,
                contentAlignment = Alignment.TopStart,
                modifier = Modifier.heightIn(min = 180.dp, max = 250.dp),
            )
            Guidelines()
            ChallengeTogetherButton(
                onClick = {
                    if (currentContent.isNotBlank()) {
                        onEditPostClick(postId, currentContent)
                    }
                },
                enabled = !isEditingPost && currentContent.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                if (isEditingPost) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = CustomColorProvider.colorScheme.brand,
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.feature_community_write),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@DevicePreviews
@Composable
fun EditPostScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            EditPostScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
