package com.yjy.platform.widget.configures

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.yjy.platform.widget.R
import com.yjy.platform.widget.configures.preview.ChallengeListWidgetPreview
import com.yjy.platform.widget.configures.screen.SettingScreen
import com.yjy.platform.widget.model.ThemeType
import com.yjy.platform.widget.widgets.ChallengeListWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ChallengeListWidgetConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        setResult(
            RESULT_OK,
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
                    ChallengeListWidgetConfigContent(
                        themeType = themeType,
                        onThemeTypeChange = { themeType = it.value },
                        appWidgetId = appWidgetId,
                        onFinish = {
                            finish()
                        },
                    )
                }
            }
        }
    }

    @Composable
    fun ChallengeListWidgetConfigContent(
        appWidgetId: Int,
        themeType: Int,
        onThemeTypeChange: (ThemeType) -> Unit,
        onFinish: () -> Unit,
        context: Context = LocalContext.current,
        viewModel: WidgetConfigViewModel = hiltViewModel(),
    ) {
        val scope = rememberCoroutineScope()
        var backgroundAlpha by remember { mutableFloatStateOf(1f) }

        val shouldHideWidgetContents by viewModel.shouldHideWidgetContents.collectAsStateWithLifecycle()
        val challenges by viewModel.challenges.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            val glanceId = try {
                GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
            } catch (e: Exception) {
                Timber.e(e, "Failed to get GlanceId")
                onFinish()
                return@LaunchedEffect
            }

            val prefs = getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)
            backgroundAlpha = prefs[floatPreferencesKey(ChallengeListWidget.BACKGROUND_ALPHA_KEY)] ?: 1f
            onThemeTypeChange(ThemeType.from(prefs[intPreferencesKey(ChallengeListWidget.THEME_TYPE_KEY)] ?: 0))
        }

        Scaffold(
            topBar = {
                ChallengeTogetherTopAppBar(
                    onNavigationClick = onFinish,
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
                        onClick = onFinish,
                        color = CustomColorProvider.colorScheme.onBackgroundMuted,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        textDecoration = TextDecoration.None,
                    )
                    ClickableText(
                        text = stringResource(id = R.string.platform_widget_confirm),
                        onClick = {
                            scope.launch {
                                ChallengeListWidget.updateWidgetConfig(
                                    context = context,
                                    themeType = themeType,
                                    alpha = backgroundAlpha,
                                    appWidgetId = appWidgetId,
                                )
                                onFinish()
                            }
                        },
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
            SettingScreen(
                themeType = ThemeType.from(themeType),
                backgroundAlpha = backgroundAlpha,
                onThemeTypeChange = onThemeTypeChange,
                onBackgroundAlphaChange = { backgroundAlpha = it },
                modifier = Modifier.padding(padding),
            ) {
                ChallengeListWidgetPreview(
                    challenges = challenges,
                    shouldHideContent = shouldHideWidgetContents,
                    modifier = Modifier.padding(32.dp),
                    backgroundAlpha = backgroundAlpha,
                )
            }
        }
    }
}
