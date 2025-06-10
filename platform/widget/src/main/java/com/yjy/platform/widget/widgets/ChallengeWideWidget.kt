package com.yjy.platform.widget.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceComposable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import com.yjy.common.designsystem.extensions.getIconResId
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.platform.widget.R
import com.yjy.platform.widget.components.GlanceCircularProgressBar
import com.yjy.platform.widget.di.WidgetEntryPoint
import com.yjy.platform.widget.model.ThemeType
import com.yjy.platform.widget.theme.WidgetColorScheme
import com.yjy.platform.widget.theme.WidgetRadius
import com.yjy.platform.widget.theme.WidgetTypography
import com.yjy.platform.widget.util.WidgetClickAction
import com.yjy.platform.widget.util.calculateProgressPercentage
import com.yjy.platform.widget.util.formatGlanceSimpleTimeDuration
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import timber.log.Timber

class ChallengeWideWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val lastUpdate = prefs[longPreferencesKey(LAST_UPDATE_KEY)] ?: 0L
            val challengeId = prefs[intPreferencesKey(CHALLENGE_ID_KEY)]
            val backgroundAlpha = prefs[floatPreferencesKey(BACKGROUND_ALPHA_KEY)] ?: DEFAULT_BACKGROUND_ALPHA
            val themeType = ThemeType.from(prefs[intPreferencesKey(THEME_TYPE_KEY)])

            val localContext = LocalContext.current
            var shouldHideContent by remember { mutableStateOf(false) }
            var challenge by remember { mutableStateOf<SimpleStartedChallenge?>(null) }

            LaunchedEffect(lastUpdate, challengeId) {
                val entryPoint = EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    WidgetEntryPoint::class.java,
                )

                val useCase = entryPoint.getStartedChallengesUseCase()
                val appLockRepository = entryPoint.appLockRepository()

                shouldHideContent = appLockRepository.shouldHideWidgetContents.first()
                challenge = useCase().first().find { it.id == challengeId }
            }

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(WidgetColorScheme.surface(theme = themeType, alpha = backgroundAlpha))
                    .cornerRadius(WidgetRadius.large),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    shouldHideContent -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = GlanceModifier.padding(16.dp),
                        ) {
                            Image(
                                provider = ImageProvider(ChallengeTogetherIcons.Hide),
                                contentDescription = context.getString(
                                    R.string.platform_widget_hidden_due_to_app_lock,
                                ),
                                contentScale = ContentScale.Fit,
                                colorFilter = ColorFilter.tint(WidgetColorScheme.onSurface(theme = themeType)),
                            )
                            Spacer(modifier = GlanceModifier.width(16.dp))
                            Text(
                                text = localContext.getString(R.string.platform_widget_hidden_due_to_app_lock),
                                style = WidgetTypography.labelSmall.copy(
                                    color = WidgetColorScheme.onSurfaceMuted(theme = themeType),
                                ),
                            )
                        }
                    }

                    challenge == null -> {
                        Text(
                            text = localContext.getString(R.string.platform_widget_no_challenge),
                            style = WidgetTypography.bodyLarge.copy(
                                color = WidgetColorScheme.onSurface(theme = themeType),
                                textAlign = TextAlign.Center,
                            ),
                        )
                    }

                    else -> {
                        ChallengeContent(
                            challenge = challenge!!,
                            themeType = themeType,
                            backgroundAlpha = backgroundAlpha,
                            onClick = {
                                actionRunCallback<WidgetClickAction>(
                                    actionParametersOf(
                                        WidgetClickAction.CHALLENGE_ID to challenge!!.id.toString(),
                                    ),
                                )
                            },
                        )
                    }
                }
            }
        }
    }

    @Composable
    @GlanceComposable
    private fun ChallengeContent(
        challenge: SimpleStartedChallenge,
        themeType: ThemeType,
        backgroundAlpha: Float,
        onClick: () -> Action,
    ) {
        val context = LocalContext.current

        Row(
            modifier = GlanceModifier
                .clickable(onClick = onClick())
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlanceCircularProgressBar(
                percentage = challenge.calculateProgressPercentage(),
                iconProvider = ImageProvider(challenge.category.getIconResId()),
                iconColor = WidgetColorScheme.onBackgroundMuted(theme = themeType),
                progressColor = WidgetColorScheme.brand(theme = themeType),
                backgroundColor = WidgetColorScheme.background(theme = themeType, alpha = backgroundAlpha),
                size = 50,
                thickness = 3f,
            )
            Spacer(modifier = GlanceModifier.width(12.dp))
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = challenge.title,
                    style = WidgetTypography.bodySmall.copy(
                        color = WidgetColorScheme.onSurfaceMuted(theme = themeType),
                    ),
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = formatGlanceSimpleTimeDuration(
                        seconds = challenge.currentRecordInSeconds,
                        context = context,
                    ),
                    style = WidgetTypography.bodyLarge.copy(
                        color = WidgetColorScheme.onSurface(theme = themeType),
                        textAlign = TextAlign.End,
                    ),
                    modifier = GlanceModifier.fillMaxWidth(),
                )
            }
        }
    }

    companion object {
        const val CHALLENGE_ID_KEY = "challengeId"
        const val THEME_TYPE_KEY = "themeType"
        const val BACKGROUND_ALPHA_KEY = "backgroundAlpha"
        private const val LAST_UPDATE_KEY = "lastUpdate"
        private const val DEFAULT_BACKGROUND_ALPHA = 1f

        suspend fun updateWidget(context: Context) {
            try {
                val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(ChallengeWideWidget::class.java)
                glanceIds.forEach { glanceId ->
                    updateAppWidgetState(context, glanceId) { prefs ->
                        prefs[longPreferencesKey(LAST_UPDATE_KEY)] = System.currentTimeMillis()
                    }

                    ChallengeWideWidget().update(context, glanceId)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to update widget")
            }
        }

        suspend fun updateWidgetConfig(
            context: Context,
            challengeId: Int,
            themeType: Int,
            alpha: Float,
            appWidgetId: Int,
        ) {
            try {
                val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)

                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[floatPreferencesKey(BACKGROUND_ALPHA_KEY)] = alpha
                    prefs[intPreferencesKey(THEME_TYPE_KEY)] = themeType
                    prefs[intPreferencesKey(CHALLENGE_ID_KEY)] = challengeId
                }
                ChallengeWideWidget().update(context, glanceId)
            } catch (e: Exception) {
                Timber.e(e, "Failed to update widget config")
            }
        }
    }
}
