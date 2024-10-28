package com.yjy.feature.home

import androidx.lifecycle.viewModelScope
import com.yjy.common.core.base.BaseViewModel
import com.yjy.common.core.util.TimeManager
import com.yjy.common.network.NetworkResult
import com.yjy.common.network.isFailure
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.feature.home.model.HomeUiAction
import com.yjy.feature.home.model.HomeUiEvent
import com.yjy.feature.home.model.HomeUiState
import com.yjy.model.Tier
import com.yjy.model.challenge.Mode
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
    private val userRepository: UserRepository,
    private val challengeRepository: ChallengeRepository,
) : BaseViewModel<HomeUiState, HomeUiEvent>(initialState = HomeUiState()) {

    private var syncTimeJob: Job? = null
    private val isSyncingTime: Boolean get() = syncTimeJob?.isActive == true

    init {
        initFlows()
        initData()
    }

    private fun initFlows() = with(viewModelScope) {
        launch { observeStartedChallenges() }
        launch { observeWaitingChallenges() }
        launch { observeRecentCompletedChallenges() }

        timeManager.setOnTimeChanged { handleTimeChange() }
        timeManager.startTicking(this)
    }

    private fun initData() = viewModelScope.launch {
        updateState { copy(isLoading = true) }
        loadData()
        updateState { copy(isLoading = false) }
        timeManager.emitCurrentTime()
    }

    private suspend fun loadData() = supervisorScope {
        awaitAll(
            async { syncChallenges() },
            async { loadUserInfo() },
        )
    }

    private suspend fun syncChallenges() {
        challengeRepository.syncChallenges().onFailure {
            updateState { copy(hasError = true) }
        }
    }

    private suspend fun loadUserInfo() = supervisorScope {
        val (nameResult, notificationResult) = awaitAll(
            async { loadUserName() },
            async { loadNotificationCount() },
        )

        updateState {
            copy(hasError = nameResult.isFailure || notificationResult.isFailure)
        }
    }

    private suspend fun loadUserName(): NetworkResult<String> {
        return userRepository.getUserName().onSuccess { name ->
            updateState { copy(userName = name) }
        }
    }

    private suspend fun loadNotificationCount(): NetworkResult<Int> {
        return userRepository.getUnViewedNotificationCount().onSuccess { count ->
            updateState { copy(unViewedNotificationCount = count) }
        }
    }

    private fun handleTimeChange() {
        syncTimeJob?.cancel()
        syncTimeJob = viewModelScope.launch {
            challengeRepository.syncTime().onFailure {
                updateState { copy(hasError = true) }
            }
        }
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
        challenges.ifEmpty {
            handleEmptyStartedChallenges()
            return
        }

        val recordCalculatedChallenges = calculateRecordOfChallenges(challenges, currentTime)
        val unCompletedChallenges = recordCalculatedChallenges.filterNot { it.isCompleted }

        checkNewlyCompletedChallenges(unCompletedChallenges)
        updateTier(recordCalculatedChallenges)
        updateCurrentBestRecord(unCompletedChallenges)
        updateStartedChallenges(unCompletedChallenges)
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

        challenge.copy(
            currentRecordInSeconds = when (val targetDays = challenge.targetDays) {
                is TargetDays.Fixed -> currentRecord.coerceAtMost(
                    (targetDays.days * SECONDS_PER_DAY).toLong()
                )
                else -> currentRecord
            }
        )
    }

    private suspend fun checkNewlyCompletedChallenges(challenges: List<StartedChallenge>) {
        if (challenges.any { it.isCompleted() }) {
            syncChallenges()
        }
    }

    private fun StartedChallenge.isCompleted(): Boolean {
        return when (val targetDays = targetDays) {
            is TargetDays.Fixed -> (currentRecordInSeconds ?: 0) / SECONDS_PER_DAY >= targetDays.days
            is TargetDays.Infinite -> false
        }
    }

    private fun updateTier(challenges: List<StartedChallenge>) {
        val currentTierBestRecord = challenges
            .filter { it.mode == Mode.CHALLENGE }
            .getBestRecord()

        val currentTier = Tier.getCurrentTier(currentTierBestRecord)
        updateState { copy(currentTier = currentTier) }
    }

    private fun updateCurrentBestRecord(challenges: List<StartedChallenge>) {
        val currentBestRecord = challenges.getBestRecord()
        updateState { copy(currentBestRecordInSeconds = currentBestRecord) }
    }

    private fun List<StartedChallenge>.getBestRecord(): Long {
        return this.maxOfOrNull { it.currentRecordInSeconds ?: 0 } ?: 0
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

    private fun refreshData() = viewModelScope.launch {
        if (uiState.value.isLoading) return@launch
        loadData()
    }

    private fun clearRecentCompletedChallenges() = viewModelScope.launch {
        challengeRepository.clearRecentCompletedChallenges()
    }

    companion object {
        private const val SECONDS_PER_DAY = 24 * 60 * 60
    }
}
