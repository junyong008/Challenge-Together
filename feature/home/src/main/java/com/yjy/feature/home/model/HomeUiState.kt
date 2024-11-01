package com.yjy.feature.home.model

import com.yjy.model.challenge.core.Category
import com.yjy.model.common.Tier

data class HomeUiState(
    val remainDayForNextTier: Int = 0,
    val tierProgress: Float = 0f,
    val tierUpAnimation: TierUpAnimationState? = null,
    val shouldShowSortOrderBottomSheet: Boolean = false,
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category = Category.ALL,
    val currentBestRecordInSeconds: Long = 0,
)

data class TierUpAnimationState(
    val from: Tier,
    val to: Tier,
)
