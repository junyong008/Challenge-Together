package com.yjy.feature.home.model

import com.yjy.model.challenge.StartedChallenge
import com.yjy.model.challenge.WaitingChallenge

data class HomeUiState(
    val hasError: Boolean = false,
    val isLoading: Boolean = true,
    val userName: String = "",
    val unViewedNotificationCount: Int = 0,
    val currentBestRecordInSeconds: Long = 0,
    val recentCompletedChallengeTitles: List<String> = emptyList(),
    val startedChallenges: List<StartedChallenge> = emptyList(),
    val waitingChallenges: List<WaitingChallenge> = emptyList(),
)
