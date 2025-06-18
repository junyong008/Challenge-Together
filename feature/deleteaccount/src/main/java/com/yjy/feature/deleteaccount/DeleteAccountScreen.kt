package com.yjy.feature.deleteaccount

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.core.login.GoogleLoginManager
import com.yjy.common.core.login.KakaoLoginManager
import com.yjy.common.core.login.NaverLoginManager
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.designsystem.component.BulletText
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherButton
import com.yjy.common.designsystem.component.ChallengeTogetherDialog
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.component.StableImage
import com.yjy.common.designsystem.component.TitleWithDescription
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.feature.deleteaccount.model.DeleteAccountUiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun DeleteAccountRoute(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeleteAccountViewModel = hiltViewModel(),
) {
    val countDown by viewModel.countDown.collectAsStateWithLifecycle()
    val isDeletingAccount by viewModel.isDeletingAccount.collectAsStateWithLifecycle()

    DeleteAccountScreen(
        modifier = modifier,
        countDown = countDown,
        isDeletingAccount = isDeletingAccount,
        uiEvent = viewModel.uiEvent,
        deleteAccount = viewModel::deleteAccount,
        onBackClick = onBackClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@Composable
internal fun DeleteAccountScreen(
    modifier: Modifier = Modifier,
    countDown: Int? = null,
    isDeletingAccount: Boolean = false,
    uiEvent: Flow<DeleteAccountUiEvent> = flowOf(),
    deleteAccount: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    var shouldShowDeleteConfirmDialog by remember { mutableStateOf(false) }

    val deleteSuccessMessage = stringResource(id = R.string.feature_deleteaccount_success)
    val deleteFailedMessage = stringResource(id = R.string.feature_deleteaccount_error)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            DeleteAccountUiEvent.Success ->
                Toast.makeText(context, deleteSuccessMessage, Toast.LENGTH_SHORT).show()

            DeleteAccountUiEvent.Failure ->
                onShowSnackbar(SnackbarType.ERROR, deleteFailedMessage)
        }
    }

    if (shouldShowDeleteConfirmDialog) {
        ChallengeTogetherDialog(
            title = stringResource(id = R.string.feature_deleteaccount_confirmation),
            description = stringResource(id = R.string.feature_deleteaccount_irreversible_notice),
            positiveTextRes = R.string.feature_deleteaccount_delete,
            positiveTextColor = CustomColorProvider.colorScheme.red,
            onClickPositive = {
                shouldShowDeleteConfirmDialog = false
                KakaoLoginManager.unlink()
                NaverLoginManager.logout()
                GoogleLoginManager.logout(context)
                deleteAccount()
            },
            onClickNegative = { shouldShowDeleteConfirmDialog = false },
        )
    }

    Scaffold(
        topBar = {
            ChallengeTogetherTopAppBar(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                onNavigationClick = onBackClick,
            )
        },
        bottomBar = {
            ChallengeTogetherButton(
                onClick = { shouldShowDeleteConfirmDialog = true },
                enabled = !isDeletingAccount && countDown == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                containerColor = CustomColorProvider.colorScheme.red,
            ) {
                if (isDeletingAccount) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = CustomColorProvider.colorScheme.brand,
                    )
                } else {
                    Text(
                        text = countDown?.toString() ?: stringResource(id = R.string.feature_deleteaccount_delete),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        },
        containerColor = CustomColorProvider.colorScheme.background,
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            TitleWithDescription(
                titleRes = R.string.feature_deleteaccount_title,
                descriptionRes = R.string.feature_deleteaccount_warning_title,
            )
            Spacer(modifier = Modifier.height(70.dp))
            StableImage(
                drawableResId = R.drawable.image_trash,
                descriptionResId = R.string.feature_deleteaccount_info_graphic,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(130.dp),
            )
            Spacer(modifier = Modifier.height(70.dp))
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 8.dp),
                color = CustomColorProvider.colorScheme.divider,
            )
            InfoTexts(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(Alignment.Start),
            )
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 8.dp),
                color = CustomColorProvider.colorScheme.divider,
            )
        }
    }
}

@Composable
private fun InfoTexts(
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(24.dp))
        BulletText(
            text = stringResource(id = R.string.feature_deleteaccount_warning_challenges),
            style = MaterialTheme.typography.labelSmall,
            color = CustomColorProvider.colorScheme.onBackground,
            bulletColor = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Spacer(modifier = Modifier.height(24.dp))
        BulletText(
            text = stringResource(id = R.string.feature_deleteaccount_warning_community),
            style = MaterialTheme.typography.labelSmall,
            color = CustomColorProvider.colorScheme.onBackground,
            bulletColor = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Spacer(modifier = Modifier.height(24.dp))
        BulletText(
            text = stringResource(id = R.string.feature_deleteaccount_warning_irreversible),
            style = MaterialTheme.typography.labelSmall,
            color = CustomColorProvider.colorScheme.onBackground,
            bulletColor = CustomColorProvider.colorScheme.onBackgroundMuted,
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@DevicePreviews
@Composable
fun DeleteAccountScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            DeleteAccountScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
