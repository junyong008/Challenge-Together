package com.yjy.feature.my

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.yjy.common.core.constants.UrlConst.PRIVACY_POLICY
import com.yjy.common.core.extensions.clickableSingle
import com.yjy.common.core.login.GoogleLoginManager
import com.yjy.common.core.login.KakaoLoginManager
import com.yjy.common.core.login.NaverLoginManager
import com.yjy.common.core.util.ObserveAsEvents
import com.yjy.common.core.util.formatTimeDuration
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherDialog
import com.yjy.common.designsystem.component.LoadingWheel
import com.yjy.common.designsystem.component.RibbonMedal
import com.yjy.common.designsystem.component.RoundedLinearProgressBar
import com.yjy.common.designsystem.component.SnackbarType
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.common.ui.DevicePreviews
import com.yjy.common.ui.ErrorBody
import com.yjy.feature.my.model.AccountTypeUiState
import com.yjy.feature.my.model.MyUiAction
import com.yjy.feature.my.model.MyUiEvent
import com.yjy.feature.my.model.RecordUiState
import com.yjy.feature.my.model.UserNameUiState
import com.yjy.feature.my.model.getNameOrDefault
import com.yjy.feature.my.model.getRecordOrNull
import com.yjy.feature.my.model.getTypeOrNull
import com.yjy.feature.my.model.isError
import com.yjy.feature.my.model.isLoading
import com.yjy.model.challenge.UserRecord
import com.yjy.model.common.AccountType
import com.yjy.model.common.Tier
import com.yjy.model.common.TierProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber

@Composable
internal fun MyRoute(
    onNotificationSettingClick: () -> Unit,
    onAppLockSettingClick: () -> Unit,
    onAccountLinkClick: () -> Unit,
    onPremiumClick: () -> Unit,
    onChangeNicknameClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onShowSnackbar: suspend (SnackbarType, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = hiltViewModel(),
) {
    val currentTier by viewModel.currentTier.collectAsStateWithLifecycle()
    val tierProgress by viewModel.tierProgress.collectAsStateWithLifecycle()
    val accountType by viewModel.accountType.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val records by viewModel.records.collectAsStateWithLifecycle()

    MyScreen(
        modifier = modifier,
        currentTier = currentTier,
        tierProgress = tierProgress,
        accountType = accountType,
        userName = userName,
        records = records,
        uiEvent = viewModel.uiEvent,
        processAction = viewModel::processAction,
        onNotificationSettingClick = onNotificationSettingClick,
        onAppLockSettingClick = onAppLockSettingClick,
        onAccountLinkClick = onAccountLinkClick,
        onPremiumClick = onPremiumClick,
        onChangeNicknameClick = onChangeNicknameClick,
        onChangePasswordClick = onChangePasswordClick,
        onDeleteAccountClick = onDeleteAccountClick,
        onShowSnackbar = onShowSnackbar,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MyScreen(
    modifier: Modifier = Modifier,
    currentTier: Tier = Tier.UNSPECIFIED,
    tierProgress: TierProgress = TierProgress(0, 0f),
    accountType: AccountTypeUiState = AccountTypeUiState.Loading,
    userName: UserNameUiState = UserNameUiState.Loading,
    records: RecordUiState = RecordUiState.Loading,
    uiEvent: Flow<MyUiEvent> = flowOf(),
    processAction: (MyUiAction) -> Unit = {},
    onNotificationSettingClick: () -> Unit = {},
    onAppLockSettingClick: () -> Unit = {},
    onAccountLinkClick: () -> Unit = {},
    onPremiumClick: () -> Unit = {},
    onChangeNicknameClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onDeleteAccountClick: () -> Unit = {},
    onShowSnackbar: suspend (SnackbarType, String) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val hasError = accountType.isError() || userName.isError() || records.isError()
    val isLoading = accountType.isLoading() || userName.isLoading() || records.isLoading()
    var shouldShowLogoutConfirmDialog by remember { mutableStateOf(false) }

    val findEmailAppFailureMessage = stringResource(id = R.string.feature_my_email_app_not_found)
    val sendEmailFailureMessage = stringResource(id = R.string.feature_my_email_send_failed)
    val logoutSuccessMessage = stringResource(id = R.string.feature_my_logout_success)

    ObserveAsEvents(flow = uiEvent) {
        when (it) {
            MyUiEvent.FindEmailAppFailure -> onShowSnackbar(SnackbarType.ERROR, findEmailAppFailureMessage)
            MyUiEvent.SendEmailFailure -> onShowSnackbar(SnackbarType.ERROR, sendEmailFailureMessage)
            MyUiEvent.Logout -> Toast.makeText(context, logoutSuccessMessage, Toast.LENGTH_SHORT).show()
        }
    }

    if (shouldShowLogoutConfirmDialog) {
        ChallengeTogetherDialog(
            title = stringResource(id = R.string.feature_my_logout),
            description = stringResource(id = R.string.feature_my_logout_confirmation),
            positiveTextRes = R.string.feature_my_logout,
            onClickPositive = {
                shouldShowLogoutConfirmDialog = false
                KakaoLoginManager.logout()
                NaverLoginManager.logout()
                GoogleLoginManager.logout(context)
                processAction(MyUiAction.OnLogoutClick)
            },
            onClickNegative = { shouldShowLogoutConfirmDialog = false },
        )
    }

    when {
        isLoading -> LoadingWheel(modifier = modifier)
        hasError -> {
            ErrorBody(
                modifier = modifier,
                onClickRetry = { processAction(MyUiAction.OnRetryClick) },
            )
        }

        else -> {
            val userAccountType = accountType.getTypeOrNull() ?: return
            val userRecords = records.getRecordOrNull() ?: return

            CompositionLocalProvider(
                LocalOverscrollConfiguration provides null,
            ) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
                    Column(
                        modifier = Modifier
                            .clip(
                                MaterialTheme.shapes.large.copy(
                                    topStart = CornerSize(0),
                                    topEnd = CornerSize(0),
                                ),
                            )
                            .background(CustomColorProvider.colorScheme.surface),
                    ) {
                        ProfileSection(
                            userName = userName.getNameOrDefault(),
                            tier = currentTier,
                            tierProgress = tierProgress,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                        RecordSection(
                            userRecord = userRecords,
                            modifier = Modifier.padding(horizontal = 32.dp),
                        )
                    }
                    SettingSection(
                        onNotificationSettingClick = onNotificationSettingClick,
                        onAppLockSettingClick = onAppLockSettingClick,
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                    AccountSection(
                        accountType = userAccountType,
                        onAccountLinkClick = onAccountLinkClick,
                        onPremiumClick = onPremiumClick,
                        onChangeNicknameClick = onChangeNicknameClick,
                        onChangePasswordClick = onChangePasswordClick,
                        onLogoutClick = { shouldShowLogoutConfirmDialog = true },
                        onDeleteAccountClick = onDeleteAccountClick,
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                    AppInfoSection(
                        processAction = processAction,
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun AppInfoSection(
    processAction: (MyUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

    val developerEmail = "junyong008@gmail.com"
    val emailSubject = stringResource(id = R.string.feature_my_email_inquiry_title)
    val emailBody = """
        App Version: ${packageInfo.versionName}
        Device: ${Build.MANUFACTURER} ${Build.MODEL}
        Android Version: ${Build.VERSION.RELEASE}
        
        ${stringResource(id = R.string.feature_my_email_inquiry_content)}
        
    """.trimIndent()

    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.feature_my_app_info),
            color = CustomColorProvider.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(12.dp))
        MyCard(
            titleResId = R.string.feature_my_contact_support,
            iconResId = ChallengeTogetherIcons.Support,
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(developerEmail))
                        putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                        putExtra(Intent.EXTRA_TEXT, emailBody)
                    }

                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    } else {
                        processAction(MyUiAction.OnFindEmailAppFailure)
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to send email.")
                    processAction(MyUiAction.OnSendEmailFailure)
                }
            },
        )
        Spacer(modifier = Modifier.height(8.dp))
        MyCard(
            titleResId = R.string.feature_my_privacy_policy,
            iconResId = ChallengeTogetherIcons.Policy,
            onClick = {
                try {
                    uriHandler.openUri(PRIVACY_POLICY)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to open privacy policy.")
                }
            },
        )
        Spacer(modifier = Modifier.height(8.dp))
        MyCard(
            titleResId = R.string.feature_my_open_source_license,
            iconResId = ChallengeTogetherIcons.Source,
            onClick = {
                context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            },
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(CustomColorProvider.colorScheme.surface)
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.feature_my_app_version),
                color = CustomColorProvider.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = packageInfo.versionName.toString(),
                color = CustomColorProvider.colorScheme.brandDim,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun AccountSection(
    accountType: AccountType,
    onAccountLinkClick: () -> Unit,
    onPremiumClick: () -> Unit,
    onChangeNicknameClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.feature_my_account),
            color = CustomColorProvider.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (accountType == AccountType.EMAIL || accountType == AccountType.GUEST) {
            Spacer(modifier = Modifier.height(8.dp))
            MyCard(
                titleResId = R.string.feature_my_account_linking,
                iconResId = ChallengeTogetherIcons.Link,
                onClick = onAccountLinkClick,
            )
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            AccountTypeCard(accountType = accountType)
        }
        Spacer(modifier = Modifier.height(8.dp))
        MyCard(
            titleResId = R.string.feature_my_account_premium,
            iconResId = ChallengeTogetherIcons.Premium,
            onClick = onPremiumClick,
        )
        Spacer(modifier = Modifier.height(8.dp))
        MyCard(
            titleResId = R.string.feature_my_nickname_change,
            iconResId = ChallengeTogetherIcons.EditLined,
            onClick = onChangeNicknameClick,
        )
        if (accountType == AccountType.EMAIL) {
            Spacer(modifier = Modifier.height(8.dp))
            MyCard(
                titleResId = R.string.feature_my_password_change,
                iconResId = ChallengeTogetherIcons.Key,
                onClick = onChangePasswordClick,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        MyCard(
            titleResId = R.string.feature_my_logout,
            iconResId = ChallengeTogetherIcons.Logout,
            onClick = onLogoutClick,
        )
        Spacer(modifier = Modifier.height(8.dp))
        MyCard(
            titleResId = R.string.feature_my_delete_account,
            iconResId = ChallengeTogetherIcons.Trash,
            onClick = onDeleteAccountClick,
        )
    }
}

@Composable
private fun AccountTypeCard(
    accountType: AccountType,
    modifier: Modifier = Modifier,
) {
    val iconResId = when (accountType) {
        AccountType.KAKAO -> ChallengeTogetherIcons.Kakao
        AccountType.GOOGLE -> ChallengeTogetherIcons.Google
        AccountType.NAVER -> ChallengeTogetherIcons.Naver
        else -> return
    }

    val descriptionResId = when (accountType) {
        AccountType.KAKAO -> R.string.feature_my_linked_with_kakao
        AccountType.GOOGLE -> R.string.feature_my_linked_with_google
        AccountType.NAVER -> R.string.feature_my_linked_with_naver
        else -> return
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(CustomColorProvider.colorScheme.surface)
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = R.string.feature_my_linked_with),
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(CustomColorProvider.colorScheme.background)
                .padding(12.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = iconResId),
                contentDescription = stringResource(id = descriptionResId),
                tint = if (accountType == AccountType.NAVER) {
                    CustomColorProvider.colorScheme.naverBackground
                } else {
                    Color.Unspecified
                },
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun SettingSection(
    onNotificationSettingClick: () -> Unit,
    onAppLockSettingClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.feature_my_settings),
            color = CustomColorProvider.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(12.dp))
        MyCard(
            titleResId = R.string.feature_my_notification_settings,
            iconResId = ChallengeTogetherIcons.Bell,
            onClick = onNotificationSettingClick,
        )
        Spacer(modifier = Modifier.height(8.dp))
        MyCard(
            titleResId = R.string.feature_my_app_lock_settings,
            iconResId = ChallengeTogetherIcons.LockFilled,
            onClick = onAppLockSettingClick,
        )
    }
}

@Composable
private fun MyCard(
    @StringRes titleResId: Int,
    @DrawableRes iconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(CustomColorProvider.colorScheme.surface)
            .clickableSingle { onClick() }
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = iconResId),
            contentDescription = stringResource(id = titleResId),
            tint = CustomColorProvider.colorScheme.onSurfaceMuted,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(id = titleResId),
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun RecordSection(
    userRecord: UserRecord,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min),
        ) {
            RecordItem(
                title = stringResource(R.string.feature_my_attempt_count),
                count = userRecord.tryCount,
                modifier = Modifier.weight(1f),
            )
            VerticalDivider(
                thickness = 1.dp,
                color = CustomColorProvider.colorScheme.divider,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
            RecordItem(
                title = stringResource(R.string.feature_my_success_count),
                count = userRecord.successCount,
                modifier = Modifier.weight(1f),
            )
            VerticalDivider(
                thickness = 1.dp,
                color = CustomColorProvider.colorScheme.divider,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
            RecordItem(
                title = stringResource(R.string.feature_my_reset_count),
                count = userRecord.resetCount,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(CustomColorProvider.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.feature_my_best_record),
                color = CustomColorProvider.colorScheme.onBackgroundMuted,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatTimeDuration(userRecord.bestRecordInSeconds),
                color = CustomColorProvider.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun RecordItem(
    title: String,
    count: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = count.toString(),
            color = CustomColorProvider.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            color = CustomColorProvider.colorScheme.onSurfaceMuted,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ProfileSection(
    userName: String,
    tier: Tier,
    tierProgress: TierProgress,
    modifier: Modifier = Modifier,
) {
    val progressDescription = when {
        tier == Tier.highestTier -> stringResource(id = R.string.feature_my_highest_tier)
        tierProgress.progress == 0f -> stringResource(id = R.string.feature_my_no_active_challenge_mode)
        else -> stringResource(id = R.string.feature_my_remain_time_for_next_tier, tierProgress.remainingDays)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RibbonMedal(
            tier = tier,
            modifier = Modifier
                .padding(start = 20.dp, bottom = 34.dp, top = 34.dp)
                .size(150.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 32.dp, top = 32.dp, bottom = 32.dp),
        ) {
            Text(
                text = userName,
                color = CustomColorProvider.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = progressDescription,
                color = CustomColorProvider.colorScheme.onSurfaceMuted,
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(modifier = Modifier.height(4.dp))
            RoundedLinearProgressBar(
                progress = { tierProgress.progress },
                enabled = tier != Tier.highestTier,
                modifier = Modifier
                    .height(15.dp)
                    .fillMaxWidth(),
            )
        }
    }
}

@DevicePreviews
@Composable
fun MyScreenPreview() {
    ChallengeTogetherTheme {
        ChallengeTogetherBackground {
            MyScreen(
                modifier = Modifier.fillMaxSize(),
                currentTier = Tier.GOLD,
                tierProgress = TierProgress(10, 0.5f),
                userName = UserNameUiState.Success("닉네임"),
                records = RecordUiState.Success(
                    record = UserRecord(
                        tryCount = 30,
                        successCount = 10,
                        resetCount = 35,
                        bestRecordInSeconds = 86400L * 7,
                    ),
                ),
            )
        }
    }
}
