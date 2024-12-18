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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.yjy.common.designsystem.theme.ChallengeTogetherTheme
import com.yjy.common.designsystem.theme.CustomColorProvider
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.platform.widget.R
import com.yjy.platform.widget.configures.preview.ChallengeWideWidgetPreview
import com.yjy.platform.widget.configures.screen.ChallengeSelectScreen
import com.yjy.platform.widget.configures.screen.HiddenScreen
import com.yjy.platform.widget.configures.screen.SettingScreen
import com.yjy.platform.widget.widgets.ChallengeWideWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChallengeWideWidgetConfigActivity : ComponentActivity() {
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

        setContent {
            val isDarkTheme = isSystemInDarkTheme()

            ChallengeTogetherTheme(isDarkTheme = isDarkTheme) {
                ChallengeTogetherBackground {
                    ChallengeWideWidgetConfigContent(
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
    fun ChallengeWideWidgetConfigContent(
        appWidgetId: Int,
        onFinish: (result: Int) -> Unit,
        context: Context = LocalContext.current,
        viewModel: WidgetConfigViewModel = hiltViewModel(),
    ) {
        val challenges by viewModel.challenges.collectAsStateWithLifecycle()
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

        BackHandler(onBack = onBack)

        LaunchedEffect(challenges) {
            if (challenges.isEmpty() || selectedChallenge == null) {
                val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
                val prefs = getAppWidgetState(
                    context,
                    PreferencesGlanceStateDefinition,
                    glanceId,
                )
                val selectedChallengeId = prefs[intPreferencesKey(ChallengeWideWidget.CHALLENGE_ID_KEY)]

                selectedChallenge = challenges.find { it.id == selectedChallengeId }
                backgroundAlpha = prefs[floatPreferencesKey(ChallengeWideWidget.BACKGROUND_ALPHA_KEY)] ?: 1f
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
                                scope.launch {
                                    ChallengeWideWidget.updateWidgetConfig(
                                        context = context,
                                        challengeId = selectedChallenge?.id ?: 0,
                                        alpha = backgroundAlpha,
                                        appWidgetId = appWidgetId,
                                    )

                                    onFinish(RESULT_OK)
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
                        backgroundAlpha = backgroundAlpha,
                        onBackgroundAlphaChange = { backgroundAlpha = it },
                        modifier = Modifier.padding(padding),
                    ) {
                        ChallengeWideWidgetPreview(
                            challenge = selectedChallenge,
                            shouldHideContent = shouldHideWidgetContents,
                            modifier = Modifier
                                .heightIn(min = 160.dp)
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
