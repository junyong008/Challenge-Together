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
import androidx.glance.appwidget.lazy.LazyColumn
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
import com.yjy.data.auth.api.AppLockRepository
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.core.SortOrder
import com.yjy.platform.widget.R
import com.yjy.platform.widget.components.GlanceCircularProgressBar
import com.yjy.platform.widget.di.WidgetEntryPoint
import com.yjy.platform.widget.theme.WidgetColorScheme
import com.yjy.platform.widget.theme.WidgetRadius
import com.yjy.platform.widget.theme.WidgetTypography
import com.yjy.platform.widget.util.WidgetClickAction
import com.yjy.platform.widget.util.calculateProgressPercentage
import com.yjy.platform.widget.util.formatGlanceSimpleTimeDuration
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import timber.log.Timber

class ChallengeListWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val lastUpdate = prefs[longPreferencesKey(LAST_UPDATE_KEY)] ?: 0L
            val backgroundAlpha = prefs[floatPreferencesKey(BACKGROUND_ALPHA_KEY)] ?: DEFAULT_BACKGROUND_ALPHA

            val localContext = LocalContext.current
            var shouldHideContent by remember { mutableStateOf(false) }
            var challenges by remember { mutableStateOf<List<SimpleStartedChallenge>>(emptyList()) }

            LaunchedEffect(lastUpdate) {
                val entryPoint = EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    WidgetEntryPoint::class.java,
                )

                val useCase = entryPoint.getStartedChallengesUseCase()
                val appLockRepository = appLockRepository(entryPoint)
                val challengePreferencesRepository = entryPoint.challengePreferencesRepository()

                shouldHideContent = appLockRepository.shouldHideWidgetContents.first()
                challenges = useCase().first().sortBy(challengePreferencesRepository.sortOrder.first())
            }

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(WidgetColorScheme.surface(alpha = backgroundAlpha))
                    .cornerRadius(WidgetRadius.large)
                    .padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    shouldHideContent -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = GlanceModifier.padding(16.dp),
                        ) {
                            Image(
                                provider = ImageProvider(ChallengeTogetherIcons.Hide),
                                contentDescription = context.getString(
                                    R.string.platform_widget_hidden_due_to_app_lock,
                                ),
                                contentScale = ContentScale.Fit,
                                colorFilter = ColorFilter.tint(WidgetColorScheme.onSurface()),
                            )
                            Spacer(modifier = GlanceModifier.height(8.dp))
                            Text(
                                text = localContext.getString(R.string.platform_widget_hidden_due_to_app_lock),
                                style = WidgetTypography.labelSmall.copy(
                                    color = WidgetColorScheme.onSurfaceMuted(),
                                    textAlign = TextAlign.Center,
                                ),
                            )
                        }
                    }

                    challenges.isEmpty() -> {
                        Text(
                            text = localContext.getString(R.string.platform_widget_no_challenges),
                            style = WidgetTypography.bodyLarge.copy(
                                color = WidgetColorScheme.onSurface(),
                                textAlign = TextAlign.Center,
                            ),
                        )
                    }

                    else -> {
                        LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                            items(
                                count = challenges.size,
                                itemId = { index -> challenges[index].id.toLong() },
                            ) { index ->
                                val challenge = challenges[index]
                                ChallengeItem(
                                    challenge = challenge,
                                    backgroundAlpha = backgroundAlpha,
                                    onClick = {
                                        actionRunCallback<WidgetClickAction>(
                                            actionParametersOf(
                                                WidgetClickAction.CHALLENGE_ID to challenge.id.toString(),
                                            ),
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun appLockRepository(entryPoint: WidgetEntryPoint): AppLockRepository {
        val appLockRepository = entryPoint.appLockRepository()
        return appLockRepository
    }

    private fun List<SimpleStartedChallenge>.sortBy(sortOrder: SortOrder): List<SimpleStartedChallenge> {
        return when (sortOrder) {
            SortOrder.LATEST -> this.sortedByDescending { it.id }
            SortOrder.OLDEST -> this.sortedBy { it.id }
            SortOrder.TITLE -> this.sortedBy { it.title }
            SortOrder.HIGHEST_RECORD -> this.sortedByDescending { it.currentRecordInSeconds }
            SortOrder.LOWEST_RECORD -> this.sortedBy { it.currentRecordInSeconds }
        }
    }

    @Composable
    @GlanceComposable
    private fun ChallengeItem(
        challenge: SimpleStartedChallenge,
        backgroundAlpha: Float,
        onClick: () -> Action,
    ) {
        val context = LocalContext.current

        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .cornerRadius(WidgetRadius.medium)
                .padding(8.dp)
                .clickable(onClick = onClick()),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlanceCircularProgressBar(
                percentage = challenge.calculateProgressPercentage(),
                iconProvider = ImageProvider(challenge.category.getIconResId()),
                iconColor = WidgetColorScheme.onBackgroundMuted(),
                progressColor = WidgetColorScheme.brand(),
                backgroundColor = WidgetColorScheme.background(alpha = backgroundAlpha),
                size = 32,
                thickness = 2.5f,
            )
            Spacer(modifier = GlanceModifier.width(12.dp))
            Text(
                text = challenge.title,
                style = WidgetTypography.bodySmall.copy(
                    color = WidgetColorScheme.onSurface(),
                ),
                modifier = GlanceModifier
                    .padding(bottom = 2.dp)
                    .defaultWeight(),
            )
            Text(
                text = formatGlanceSimpleTimeDuration(
                    seconds = challenge.currentRecordInSeconds,
                    context = context,
                ),
                style = WidgetTypography.bodySmall.copy(
                    color = WidgetColorScheme.onBackground(),
                    textAlign = TextAlign.Center,
                ),
                modifier = GlanceModifier
                    .width(110.dp)
                    .background(WidgetColorScheme.background())
                    .cornerRadius(WidgetRadius.full)
                    .padding(vertical = 8.dp, horizontal = 12.dp),
            )
        }
    }

    companion object {
        const val BACKGROUND_ALPHA_KEY = "backgroundAlpha"
        private const val LAST_UPDATE_KEY = "lastUpdate"
        private const val DEFAULT_BACKGROUND_ALPHA = 1f

        suspend fun updateWidget(context: Context) {
            try {
                val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(ChallengeListWidget::class.java)
                glanceIds.forEach { glanceId ->
                    updateAppWidgetState(context, glanceId) { prefs ->
                        prefs[longPreferencesKey(LAST_UPDATE_KEY)] = System.currentTimeMillis()
                    }

                    ChallengeListWidget().update(context, glanceId)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to update widget")
            }
        }

        suspend fun updateWidgetConfig(context: Context, alpha: Float, appWidgetId: Int) {
            try {
                val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)

                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[floatPreferencesKey(BACKGROUND_ALPHA_KEY)] = alpha
                }
                ChallengeListWidget().update(context, glanceId)
            } catch (e: Exception) {
                Timber.e(e, "Failed to update widget config")
            }
        }
    }
}
