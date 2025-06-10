package com.yjy.platform.widget.configures

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yjy.common.designsystem.component.ChallengeTogetherBackground
import com.yjy.common.designsystem.component.ChallengeTogetherTopAppBar
import com.yjy.common.designsystem.component.ClickableText
import com.yjy.common.designsystem.component.PremiumDialog
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.platform.widget.R
import com.yjy.platform.widget.configures.preview.ChallengeSimpleWidgetPreview
import com.yjy.platform.widget.configures.screen.ChallengeSelectScreen
import com.yjy.platform.widget.configures.screen.HiddenScreen
import com.yjy.platform.widget.configures.screen.SettingScreen
import com.yjy.platform.widget.model.ThemeType
import com.yjy.platform.widget.util.DeepLinkUtils
import com.yjy.platform.widget.widgets.ChallengeSimpleWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ChallengeSimpleWidgetConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        setResult(
            RESULT_CANCELED,
            Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            },
        )

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            var themeType by remember { mutableIntStateOf(0) }
            val isDarkTheme = when (themeType) {
                ThemeType.LIGHT.value -> false
                ThemeType.DARK.value -> true
                else -> isSystemInDarkTheme()
            }

            ChallengeTogetherTheme(isDarkTheme = isDarkTheme) {
                ChallengeTogetherBackground {
                    ChallengeSimpleWidgetConfigContent(
                        themeType = themeType,
                        onThemeTypeChange = { themeType = it.value },
                        appWidgetId = appWidgetId,
                        onFinish = { result ->
                            setResult(
                                result,
                                Intent().apply {
                                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                                },
                            )
                            finish()
                        },
                    )
                }
            }
        }
    }

    @Composable
    fun ChallengeSimpleWidgetConfigContent(
        appWidgetId: Int,
        themeType: Int,
        onThemeTypeChange: (ThemeType) -> Unit,
        onFinish: (result: Int) -> Unit,
        context: Context = LocalContext.current,
        viewModel: WidgetConfigViewModel = hiltViewModel(),
    ) {
        val challenges by viewModel.challenges.collectAsStateWithLifecycle()
        val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()
        var shouldShowPremiumDialog by rememberSaveable { mutableStateOf(false) }
        val shouldHideWidgetContents by viewModel.shouldHideWidgetContents.collectAsStateWithLifecycle()

        val scope = rememberCoroutineScope()
        var isSettingScreen by remember { mutableStateOf(false) }
        var selectedChallenge by remember { mutableStateOf<SimpleStartedChallenge?>(null) }
        var backgroundAlpha by remember { mutableFloatStateOf(1f) }

        val onBack: () -> Unit = {
            if (isSettingScreen) {
                isSettingScreen = false
            } else {
                onFinish(RESULT_CANCELED)
            }
        }

        if (shouldShowPremiumDialog) {
            PremiumDialog(
                onExploreClick = {
                    shouldShowPremiumDialog = false
                    DeepLinkUtils.navigateToPremium(context)
                    onFinish(RESULT_CANCELED)
                },
                onDismiss = { shouldShowPremiumDialog = false },
            )
        }

        BackHandler(onBack = onBack)

        LaunchedEffect(challenges) {
            if (challenges.isEmpty() || selectedChallenge == null) {
                val glanceId = try {
                    GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to get GlanceId")
                    onFinish(RESULT_CANCELED)
                    return@LaunchedEffect
                }

                val prefs = getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)
                val selectedChallengeId = prefs[intPreferencesKey(ChallengeSimpleWidget.CHALLENGE_ID_KEY)]

                selectedChallenge = challenges.find { it.id == selectedChallengeId }
                backgroundAlpha = prefs[floatPreferencesKey(ChallengeSimpleWidget.BACKGROUND_ALPHA_KEY)] ?: 1f
                onThemeTypeChange(ThemeType.from(prefs[intPreferencesKey(ChallengeSimpleWidget.THEME_TYPE_KEY)] ?: 0))
            }
        }

        Scaffold(
            topBar = {
                ChallengeTogetherTopAppBar(
                    onNavigationClick = onBack,
                    titleRes = R.string.platform_widget_settings,
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ClickableText(
                        text = stringResource(id = R.string.platform_widget_cancel),
                        onClick = onBack,
                        color = CustomColorProvider.colorScheme.onBackgroundMuted,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        textDecoration = TextDecoration.None,
                    )
                    ClickableText(
                        text = stringResource(
                            id = if (isSettingScreen) {
                                R.string.platform_widget_confirm
                            } else {
                                R.string.platform_widget_select
                            },
                        ),
                        onClick = {
                            if (isSettingScreen) {
                                if (isPremium) {
                                    scope.launch {
                                        ChallengeSimpleWidget.updateWidgetConfig(
                                            context = context,
                                            challengeId = selectedChallenge?.id ?: 0,
                                            themeType = themeType,
                                            alpha = backgroundAlpha,
                                            appWidgetId = appWidgetId,
                                        )

                                        onFinish(RESULT_OK)
                                    }
                                } else {
                                    shouldShowPremiumDialog = true
                                }
                            } else {
                                isSettingScreen = true
                            }
                        },
                        enabled = (isSettingScreen || selectedChallenge != null) && !shouldHideWidgetContents,
                        color = CustomColorProvider.colorScheme.brand,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        textDecoration = TextDecoration.None,
                    )
                }
            },
            containerColor = CustomColorProvider.colorScheme.background,
        ) { padding ->
            when {
                shouldHideWidgetContents -> HiddenScreen()
                isSettingScreen -> {
                    SettingScreen(
                        themeType = ThemeType.from(themeType),
                        onThemeTypeChange = onThemeTypeChange,
                        backgroundAlpha = backgroundAlpha,
                        onBackgroundAlphaChange = { backgroundAlpha = it },
                        modifier = Modifier.padding(padding),
                    ) {
                        ChallengeSimpleWidgetPreview(
                            challenge = selectedChallenge,
                            shouldHideContent = shouldHideWidgetContents,
                            modifier = Modifier
                                .width(270.dp)
                                .padding(32.dp),
                            backgroundAlpha = backgroundAlpha,
                        )
                    }
                }

                else -> {
                    ChallengeSelectScreen(
                        challenges = challenges,
                        selectedChallenge = selectedChallenge,
                        onChallengeSelect = { selectedChallenge = it },
                        modifier = Modifier.padding(padding),
                    )
                }
            }
        }
    }
}
