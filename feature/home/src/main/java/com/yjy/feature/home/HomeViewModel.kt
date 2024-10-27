package com.yjy.feature.home

import androidx.lifecycle.viewModelScope
import com.yjy.common.core.base.BaseViewModel
import com.yjy.common.core.util.TimeManager
import com.yjy.common.network.isSuccess
import com.yjy.common.network.onFailure
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.feature.home.model.HomeUiAction
import com.yjy.feature.home.model.HomeUiEvent
import com.yjy.feature.home.model.HomeUiState
import com.yjy.model.challenge.StartedChallenge
import com.yjy.model.challenge.TargetDays
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val timeManager: TimeManager,
    private val challengeRepository: ChallengeRepository,
) : BaseViewModel<HomeUiState, HomeUiEvent>(initialState = HomeUiState()) {

    private var syncTimeJob: Job? = null
    private val isSyncingTime: Boolean get() = syncTimeJob?.isActive == true

    init {
        initializeFlows()
        initData()
    }

    private fun initializeFlows() = with(viewModelScope) {
        launch { observeProfile() }
        launch { observeStartedChallenges() }
        launch { observeWaitingChallenges() }
        launch { observeRecentCompletedChallenges() }

        timeManager.setOnTimeChanged { handleTimeChange() }
        timeManager.startTicking(this)
    }

    private fun handleTimeChange() {
        syncTimeJob?.cancel()
        syncTimeJob = viewModelScope.launch {
            challengeRepository.syncTime().onFailure {
                updateState { copy(hasError = true) }
            }
        }
    }

    private fun observeProfile() {
        challengeRepository.profile
            .onEach { profile ->
                updateState {
                    copy(
                        userName = profile.name,
                        unViewedNotificationCount = profile.unViewedNotificationCount,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeStartedChallenges() {
        challengeRepository.startedChallenges
            .combine(timeManager.tickerFlow) { challenges, currentTime ->
                if (isSyncingTime || uiState.value.isLoading || uiState.value.hasError) return@combine
                handleStartedChallenges(challenges, currentTime)
            }
            .launchIn(viewModelScope)
    }

    private suspend fun handleStartedChallenges(
        challenges: List<StartedChallenge>,
        currentTime: LocalDateTime,
    ) {
        if (challenges.isEmpty()) {
            handleEmptyStartedChallenges()
            return
        }

        val recordCalculatedChallenges = calculateRecordOfChallenges(challenges, currentTime)

        checkNewlyCompletedChallenges(recordCalculatedChallenges)
        updateBestRecord(recordCalculatedChallenges)
        updateStartedChallenges(recordCalculatedChallenges)
    }

    private fun handleEmptyStartedChallenges() {
        updateState {
            copy(
                startedChallenges = emptyList(),
                currentBestRecordInSeconds = 0,
            )
        }
    }

    private fun calculateRecordOfChallenges(
        challenges: List<StartedChallenge>,
        currentTime: LocalDateTime
    ): List<StartedChallenge> = challenges.map { challenge ->

        val currentRecord = Duration.between(challenge.recentResetDateTime, currentTime).seconds
        challenge.copy(currentRecordInSeconds = currentRecord)
    }

    private suspend fun checkNewlyCompletedChallenges(challenges: List<StartedChallenge>) {
        if (challenges.any { it.isCompleted() }) {
            syncData()
        }
    }

    private fun StartedChallenge.isCompleted(): Boolean {
        return when (val targetDays = targetDays) {
            is TargetDays.Fixed -> (currentRecordInSeconds ?: 0) / SECONDS_PER_DAY >= targetDays.days
            is TargetDays.Infinite -> false
        }
    }

    private fun updateBestRecord(challenges: List<StartedChallenge>) {
        val bestRecord = challenges.maxOf { it.currentRecordInSeconds ?: 0 }
        updateState { copy(currentBestRecordInSeconds = bestRecord) }
    }

    private fun updateStartedChallenges(challenges: List<StartedChallenge>) {
        updateState { copy(startedChallenges = challenges) }
    }

    private fun observeWaitingChallenges() {
        challengeRepository.waitingChallenges
            .onEach { challenges ->
                updateState { copy(waitingChallenges = challenges) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeRecentCompletedChallenges() {
        challengeRepository.recentCompletedChallengeTitles
            .onEach { recentCompletedTitles ->
                updateState { copy(recentCompletedChallengeTitles = recentCompletedTitles) }
            }
            .launchIn(viewModelScope)
    }

    fun processAction(action: HomeUiAction) {
        when (action) {
            HomeUiAction.OnScreenLoad -> refreshData()
            HomeUiAction.OnRetryClick -> initData()
            HomeUiAction.OnCloseCompletedChallengeNotification -> clearRecentCompletedChallenges()
        }
    }

    private fun initData() = viewModelScope.launch {
        updateState { copy(isLoading = true) }
        syncData()
        updateState { copy(isLoading = false) }
    }

    private fun refreshData() = viewModelScope.launch {
        if (uiState.value.isLoading) return@launch
        syncData()
    }

    private suspend fun syncData() = supervisorScope {
        val (challengesResult, profileResult) = awaitAll(
            async { challengeRepository.syncChallenges() },
            async { challengeRepository.syncProfile() },
        )

        if (challengesResult.isSuccess && profileResult.isSuccess) {
            updateState { copy(hasError = false) }
        } else {
            updateState { copy(hasError = true) }
        }
    }

    private fun clearRecentCompletedChallenges() = viewModelScope.launch {
        challengeRepository.clearRecentCompletedChallenges()
    }

    companion object {
        private const val SECONDS_PER_DAY = 24 * 60 * 60
    }
}
