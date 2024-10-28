package com.yjy.feature.home.model

import com.yjy.model.Tier
import com.yjy.model.challenge.SortOrder
import com.yjy.model.challenge.StartedChallenge
import com.yjy.model.challenge.WaitingChallenge

data class TierUpAnimationState(
    val from: Tier,
    val to: Tier,
)

data class HomeUiState(
    val hasError: Boolean = false,
    val isLoading: Boolean = true,
    val userName: String = "",
    val remainDayForNextTier: Int = 0,
    val tierProgress: Float = 0f,
    val currentTier: Tier = Tier.UNSPECIFIED,
    val tierUpAnimation: TierUpAnimationState? = null,
    val sortOrder: SortOrder = SortOrder.LATEST,
    val unViewedNotificationCount: Int = 0,
    val currentBestRecordInSeconds: Long = 0,
    val recentCompletedChallengeTitles: List<String> = emptyList(),
    val startedChallenges: List<StartedChallenge> = emptyList(),
    val waitingChallenges: List<WaitingChallenge> = emptyList(),
)
