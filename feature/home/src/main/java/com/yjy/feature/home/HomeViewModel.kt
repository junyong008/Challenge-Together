package com.yjy.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.core.constants.TimeConst.SECONDS_PER_DAY
import com.yjy.common.core.extensions.restartableStateIn
import com.yjy.common.network.fold
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.challenge.api.ChallengePreferencesRepository
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.challenge.api.WaitingChallengeRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.domain.GetStartedChallengesUseCase
import com.yjy.feature.home.model.ChallengeSyncUiState
import com.yjy.feature.home.model.HomeUiAction
import com.yjy.feature.home.model.HomeUiState
import com.yjy.feature.home.model.TierUiState
import com.yjy.feature.home.model.TierUpAnimationState
import com.yjy.feature.home.model.TimeSyncUiState
import com.yjy.feature.home.model.UnViewedNotificationUiState
import com.yjy.feature.home.model.UserNameUiState
import com.yjy.feature.home.model.getTierOrDefault
import com.yjy.feature.home.model.isError
import com.yjy.feature.home.model.isSuccess
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.SortOrder
import com.yjy.model.challenge.core.TargetDays
import com.yjy.model.common.Tier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getStartedChallengesUseCase: GetStartedChallengesUseCase,
    waitingChallengeRepository: WaitingChallengeRepository,
    private val userRepository: UserRepository,
    private val challengeRepository: ChallengeRepository,
    private val challengePreferencesRepository: ChallengePreferencesRepository,
) : ViewModel() {

    private val tierTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val timeSyncTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val challengeSyncTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private var remoteTier: Tier = Tier.UNSPECIFIED
    private var lastProcessedChallenges: List<SimpleStartedChallenge> = emptyList()

    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val timeSyncState = merge(
        challengePreferencesRepository.timeChangedFlow,
        timeSyncTrigger,
    ).flatMapLatest {
        flow {
            emit(TimeSyncUiState.Loading)
            userRepository.syncTime()
                .onSuccess { emit(TimeSyncUiState.Success) }
                .onFailure { emit(TimeSyncUiState.Error) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TimeSyncUiState.Success,
    )

    val challengeSyncState = challengeSyncTrigger
        .onStart { emit(Unit) }
        .flatMapConcat {
            flow {
                challengeRepository.syncChallenges()
                    .onSuccess { emit(ChallengeSyncUiState.Success) }
                    .onFailure { emit(ChallengeSyncUiState.Error) }
            }
        }
        .restartableStateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ChallengeSyncUiState.Loading,
        )

    val startedChallenges = combine(
        getStartedChallengesUseCase(),
        challengeSyncState,
        timeSyncState,
    ) { challenges, challengeSyncState, timeSyncState ->
        if (challengeSyncState.isSuccess() && timeSyncState.isSuccess()) {
            challenges
        } else {
            lastProcessedChallenges
        }
    }.onEach { challenges ->
        checkLocalUpdates(challenges)
        updateUiStates(challenges)

        lastProcessedChallenges = challenges
    }.combine(challengePreferencesRepository.sortOrder) { challenges, order ->
        challenges.sortBy(order)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
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

    val waitingChallenges = waitingChallengeRepository.waitingChallenges
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    val recentCompletedChallenges = challengePreferencesRepository.recentCompletedChallengeTitles
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    val sortOrder = challengePreferencesRepository.sortOrder
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SortOrder.LATEST,
        )

    val userName = flow {
        userRepository.getUserName()
            .onSuccess { emit(UserNameUiState.Success(it)) }
            .onFailure { emit(UserNameUiState.Error) }
    }.restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = UserNameUiState.Loading,
    )

    val unViewedNotificationState = flow {
        userRepository.getUnViewedNotificationCount()
            .onSuccess { emit(UnViewedNotificationUiState.Success(it)) }
            .onFailure { emit(UnViewedNotificationUiState.Error) }
    }.restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = UnViewedNotificationUiState.Loading,
    )

    val tierState = tierTrigger
        .onStart { emit(Unit) }
        .flatMapConcat {
            flow { emit(challengePreferencesRepository.getRemoteTier()) }
        }.combine(challengePreferencesRepository.localTier) { remoteResult, localTier ->
            remoteResult.fold(
                onSuccess = {
                    remoteTier = it
                    handleTier(new = it, old = localTier)
                    TierUiState.Success(localTier)
                },
                onFailure = { TierUiState.Error },
            )
        }.restartableStateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = TierUiState.Loading,
        )

    private suspend fun handleTier(old: Tier, new: Tier) {
        if (old == Tier.UNSPECIFIED || old > new) {
            challengePreferencesRepository.setLocalTier(new)
        } else if (new > old) {
            val animation = TierUpAnimationState(from = old, to = Tier.getNextTier(old))
            _uiState.update { it.copy(tierUpAnimation = animation) }
        }
    }

    private suspend fun checkLocalUpdates(challenges: List<SimpleStartedChallenge>) {
        checkTierPromotion(challenges)
        checkNewlyCompletedChallenges(challenges)
    }

    private fun updateUiStates(challenges: List<SimpleStartedChallenge>) {
        updateTierProgress(challenges)
        updateCurrentBestRecord(challenges)
        updateCategoryList(challenges)
    }

    private suspend fun checkTierPromotion(challenges: List<SimpleStartedChallenge>) {
        if (uiState.value.tierUpAnimation != null || !tierState.value.isSuccess()) return
        val currentTierBestRecord = challenges
            .filter { it.mode == Mode.CHALLENGE }
            .getBestRecord()

        val calculatedTier = Tier.getCurrentTier(currentTierBestRecord)

        if (remoteTier != Tier.UNSPECIFIED && calculatedTier > remoteTier) {
            delay(SYNC_AFTER_LOCAL_UPDATE_DELAY)
            tierTrigger.tryEmit(Unit)
        }
    }

    private suspend fun checkNewlyCompletedChallenges(challenges: List<SimpleStartedChallenge>) {
        if (challenges.any { it.isCompleted() }) {
            delay(SYNC_AFTER_LOCAL_UPDATE_DELAY)
            challengeSyncTrigger.tryEmit(Unit)
        }
    }

    private fun SimpleStartedChallenge.isCompleted(): Boolean {
        return when (val targetDays = targetDays) {
            is TargetDays.Fixed -> currentRecordInSeconds / SECONDS_PER_DAY >= targetDays.days
            is TargetDays.Infinite -> false
        }
    }

    private fun updateTierProgress(challenges: List<SimpleStartedChallenge>) {
        val currentTierBestRecord = challenges
            .filter { it.mode == Mode.CHALLENGE }
            .getBestRecord()

        val tierProgress = Tier.getTierProgress(
            currentTier = tierState.value.getTierOrDefault(),
            recordInSeconds = currentTierBestRecord,
        )

        _uiState.update {
            it.copy(
                tierProgress = tierProgress.progress,
                remainDayForNextTier = tierProgress.remainingDays,
            )
        }
    }

    private fun updateCurrentBestRecord(challenges: List<SimpleStartedChallenge>) {
        val currentBestRecord = challenges.getBestRecord()
        _uiState.update { it.copy(currentBestRecordInSeconds = currentBestRecord) }
    }

    private fun List<SimpleStartedChallenge>.getBestRecord(): Long {
        return this.maxOfOrNull { it.currentRecordInSeconds } ?: 0
    }

    private fun updateCategoryList(challenges: List<SimpleStartedChallenge>) {
        val categories = buildList {
            add(Category.ALL)
            addAll(
                challenges
                    .map { it.category }
                    .distinct()
                    .filterNot { it == Category.ALL },
            )
        }

        _uiState.update { it.copy(categories = categories) }
    }

    fun processAction(action: HomeUiAction) {
        when (action) {
            HomeUiAction.OnRetryClick -> retryOnError()
            HomeUiAction.OnCloseCompletedChallengeNotification -> clearRecentCompletedChallenges()
            HomeUiAction.OnDismissTierUpAnimation -> dismissTierUpAnimation()
            is HomeUiAction.OnCategorySelect -> updateSelectedCategory(action.category)
            is HomeUiAction.OnSortOrderSelect -> updateSortOrder(action.sortOrder)
        }
    }

    private fun retryOnError() {
        if (timeSyncState.value.isError()) timeSyncTrigger.tryEmit(Unit)
        if (challengeSyncState.value.isError()) challengeSyncState.restart()
        if (tierState.value.isError()) tierState.restart()
        if (userName.value.isError()) userName.restart()
        if (unViewedNotificationState.value.isError()) unViewedNotificationState.restart()
    }

    private fun clearRecentCompletedChallenges() = viewModelScope.launch {
        challengePreferencesRepository.clearRecentCompletedChallenges()
    }

    private fun dismissTierUpAnimation() = viewModelScope.launch {
        val animation = uiState.value.tierUpAnimation ?: return@launch

        _uiState.update { it.copy(tierUpAnimation = null) }
        delay(TIER_UP_ANIMATION_CLOSE_DELAY)
        challengePreferencesRepository.setLocalTier(animation.to)
    }

    private fun updateSelectedCategory(category: Category) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    private fun updateSortOrder(sortOrder: SortOrder) = viewModelScope.launch {
        challengePreferencesRepository.setSortOrder(sortOrder)
    }

    companion object {
        // 연속적인 승급시 중간 상태값(null)이 간혈적으로 씹히는 이슈 방지
        private const val TIER_UP_ANIMATION_CLOSE_DELAY = 100L

        // 완료된 챌린지 및 티어 승급 감지 시 과도한 서버 동기화 요청 방지
        private const val SYNC_AFTER_LOCAL_UPDATE_DELAY = 1000L
    }
}
