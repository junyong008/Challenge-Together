package com.yjy.platform.widget.configures

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.data.auth.api.AppLockRepository
import com.yjy.data.challenge.api.ChallengePreferencesRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.domain.GetStartedChallengesUseCase
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.core.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WidgetConfigViewModel @Inject constructor(
    userRepository: UserRepository,
    appLockRepository: AppLockRepository,
    getStartedChallengesUseCase: GetStartedChallengesUseCase,
    challengePreferencesRepository: ChallengePreferencesRepository,
) : ViewModel() {

    val isPremium = userRepository.isPremium
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    val shouldHideWidgetContents = appLockRepository.shouldHideWidgetContents
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    val challenges = getStartedChallengesUseCase()
        .combine(challengePreferencesRepository.sortOrder) { challenges, order ->
            challenges.sortBy(order)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    private fun List<SimpleStartedChallenge>.sortBy(sortOrder: SortOrder): List<SimpleStartedChallenge> {
        return when (sortOrder) {
            SortOrder.LATEST -> this.sortedByDescending { it.id }
            SortOrder.OLDEST -> this.sortedBy { it.id }
            SortOrder.TITLE -> this.sortedBy { it.title }
            SortOrder.HIGHEST_RECORD -> this.sortedByDescending { it.currentRecordInSeconds }
            SortOrder.LOWEST_RECORD -> this.sortedBy { it.currentRecordInSeconds }
        }
    }
}
