package com.yjy.feature.home.model

import com.yjy.model.challenge.StartedChallenge
import com.yjy.model.challenge.WaitingChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.SortOrder
import com.yjy.model.common.Tier

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
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category = Category.ALL,
    val unViewedNotificationCount: Int = 0,
    val currentBestRecordInSeconds: Long = 0,
    val recentCompletedChallengeTitles: List<String> = emptyList(),
    val startedChallenges: List<StartedChallenge> = emptyList(),
    val waitingChallenges: List<WaitingChallenge> = emptyList(),
)
