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
import com.yjy.feature.community.model.CommunityUiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun AddPostRoute(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CommunityViewModel = hiltViewModel(),
) {
    val isAddingPost by viewModel.isAddingPost.collectAsStateWithLifecycle()

    AddPostScreen(
        modifier = modifier,
        isAddingPost = isAddingPost,
        uiEvent = viewModel.uiEvent,
        onAddPostClick = viewModel::addPost,
        onBackClick = onBackClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun AddPostScreen(
    modifier: Modifier = Modifier,
    isAddingPost: Boolean = false,
    uiEvent: Flow<CommunityUiEvent> = flowOf(),
    onAddPostClick: (content: String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val addSuccessMessage = stringResource(id = R.string.feature_community_post_create_success)
    val addFailureMessage = stringResource(id = R.string.feature_community_post_create_failure)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            CommunityUiEvent.AddSuccess -> {
                onShowSnackbar(SnackbarType.SUCCESS, addSuccessMessage)
                onBackClick()
            }

            CommunityUiEvent.AddFailure ->
                onShowSnackbar(SnackbarType.ERROR, addFailureMessage)
        }
    }

    var content by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                onNavigationClick = onBackClick,
                titleRes = R.string.feature_community_post_create,
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
                value = content,
                onValueChange = { content = it.take(MAX_POST_CONTENT_LENGTH) },
                placeholderText = stringResource(
                    id = R.string.feature_community_post_create_content_placeholder,
                ),
                shape = RectangleShape,
                enabled = !isAddingPost,
                contentAlignment = Alignment.TopStart,
                modifier = Modifier.heightIn(min = 180.dp, max = 250.dp),
            )
            Guidelines()
            ChallengeTogetherButton(
                onClick = {
                    if (content.isNotBlank()) {
                        onAddPostClick(content)
                    }
                },
                enabled = !isAddingPost && content.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                if (isAddingPost) {
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

@Composable
private fun Guidelines() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.feature_community_rules_point_1_title),
            style = MaterialTheme.typography.bodySmall,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.feature_community_rules_point_1_description),
            style = MaterialTheme.typography.labelSmall,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.feature_community_rules_point_2_title),
            style = MaterialTheme.typography.bodySmall,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.feature_community_rules_point_2_description),
            style = MaterialTheme.typography.labelSmall,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.feature_community_rules_point_3_title),
            style = MaterialTheme.typography.bodySmall,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.feature_community_rules_point_3_description),
            style = MaterialTheme.typography.labelSmall,
            color = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
    }
}

@DevicePreviews
@Composable
fun AddPostScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            AddPostScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
