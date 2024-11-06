package com.yjy.feature.home

import androidx.lifecycle.viewModelScope
import com.yjy.common.core.base.BaseViewModel
import com.yjy.common.core.constants.TimeConst.SECONDS_PER_DAY
import com.yjy.common.core.extensions.restartableStateIn
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.data.user.api.UserRepository
import com.yjy.domain.GetStartedChallengesUseCase
import com.yjy.feature.home.model.ChallengeSyncUiState
import com.yjy.feature.home.model.HomeUiAction
import com.yjy.feature.home.model.HomeUiEvent
import com.yjy.feature.home.model.HomeUiState
import com.yjy.feature.home.model.TierUpAnimationState
import com.yjy.feature.home.model.UnViewedNotificationUiState
import com.yjy.feature.home.model.UserNameUiState
import com.yjy.feature.home.model.isError
import com.yjy.feature.home.model.isSuccess
import com.yjy.model.challenge.StartedChallenge
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.Mode
import com.yjy.model.challenge.core.SortOrder
import com.yjy.model.challenge.core.TargetDays
import com.yjy.model.common.Tier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getStartedChallengesUseCase: GetStartedChallengesUseCase,
    private val userRepository: UserRepository,
    private val challengeRepository: ChallengeRepository,
) : BaseViewModel<HomeUiState, HomeUiEvent>(initialState = HomeUiState()) {

    // 로딩 처리 없이 데이터를 최신화 하기 위한 트리거
    private val syncTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val userNameTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val notificationTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    // 동기화 간 자연스러운 전환을 위한 리스트 캐시
    private var lastProcessedChallenges: List<StartedChallenge> = emptyList()

    val challengeSyncState = merge(
        syncTrigger.map { SyncTrigger.Manual },
        challengeRepository.timeChangedFlow.map { SyncTrigger.TimeChanged },
    ).onStart {
        emit(SyncTrigger.Initial)
    }.onEach {
        Timber.d("syncChallenges triggered by: $it")
    }.flatMapLatest { trigger ->
        flow {
            when (trigger) {
                SyncTrigger.Initial -> ChallengeSyncUiState.Loading.Initial
                SyncTrigger.Manual -> ChallengeSyncUiState.Loading.Manual
                SyncTrigger.TimeChanged -> ChallengeSyncUiState.Loading.TimeSync
            }.let { emit(it) }

            challengeRepository.syncChallenges()
                .onSuccess { emit(ChallengeSyncUiState.Success) }
                .onFailure { emit(ChallengeSyncUiState.Error) }
        }
    }.restartableStateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = ChallengeSyncUiState.Loading.Initial,
    )

    val startedChallenges = combine(
        getStartedChallengesUseCase(),
        challengeSyncState,
    ) { challenges, syncState ->
        when (syncState) {
            ChallengeSyncUiState.Success -> challenges
            else -> lastProcessedChallenges
        }
    }.onEach { challenges ->
        handleStartedChallenges(challenges)
        lastProcessedChallenges = challenges
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    val waitingChallenges = challengeRepository.waitingChallenges
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    val recentCompletedChallenges = challengeRepository.recentCompletedChallengeTitles
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    val currentTier = challengeRepository.currentTier
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Tier.UNSPECIFIED,
        )

    val sortOrder = challengeRepository.sortOrder
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SortOrder.LATEST,
        )

    val userName = userNameTrigger
        .onStart { emit(Unit) }
        .flatMapLatest {
            flow {
                userRepository.getUserName()
                    .onSuccess { emit(UserNameUiState.Success(it)) }
                    .onFailure { emit(UserNameUiState.Error) }
            }
        }
        .restartableStateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = UserNameUiState.Loading,
        )

    val unViewedNotificationState = notificationTrigger
        .onStart { emit(Unit) }
        .flatMapLatest {
            flow {
                userRepository.getUnViewedNotificationCount()
                    .onSuccess { emit(UnViewedNotificationUiState.Success(it)) }
                    .onFailure { emit(UnViewedNotificationUiState.Error) }
            }
        }
        .restartableStateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = UnViewedNotificationUiState.Loading,
        )

    private suspend fun handleStartedChallenges(challenges: List<StartedChallenge>) {
        if (challenges.isEmpty()) return
        val unCompletedChallenges = challenges.filterNot { it.isCompleted }

        updateTier(challenges)
        updateTierProgress(unCompletedChallenges)
        checkNewlyCompletedChallenges(unCompletedChallenges)
        updateCurrentBestRecord(unCompletedChallenges)
        updateCategoryList(unCompletedChallenges)
    }

    private fun checkNewlyCompletedChallenges(challenges: List<StartedChallenge>) {
        if (challenges.any { it.isCompleted() }) {
            syncTrigger.tryEmit(Unit)
        }
    }

    private fun StartedChallenge.isCompleted(): Boolean {
        return when (val targetDays = targetDays) {
            is TargetDays.Fixed -> (currentRecordInSeconds ?: 0) / SECONDS_PER_DAY >= targetDays.days
            is TargetDays.Infinite -> false
        }
    }

    private suspend fun updateTier(challenges: List<StartedChallenge>) {
        if (uiState.value.tierUpAnimation != null) return
        val currentTierBestRecord = challenges
            .filter { it.mode == Mode.CHALLENGE }
            .getBestRecord()

        val calculatedTier = Tier.getCurrentTier(currentTierBestRecord)
        val savedTier = currentTier.value

        if (savedTier == Tier.UNSPECIFIED || calculatedTier < savedTier) {
            challengeRepository.setCurrentTier(calculatedTier)
        } else if (calculatedTier > savedTier) {
            val animation = TierUpAnimationState(from = savedTier, to = Tier.getNextTier(savedTier))
            updateState { copy(tierUpAnimation = animation) }
        }
    }

    private fun updateTierProgress(challenges: List<StartedChallenge>) {
        val currentTierBestRecord = challenges
            .filter { it.mode == Mode.CHALLENGE }
            .getBestRecord()

        val tierProgress = Tier.getTierProgress(
            currentTier = currentTier.value,
            recordInSeconds = currentTierBestRecord,
        )

        updateState {
            copy(
                tierProgress = tierProgress.progress,
                remainDayForNextTier = tierProgress.remainingDays,
            )
        }
    }

    private fun updateCurrentBestRecord(challenges: List<StartedChallenge>) {
        val currentBestRecord = challenges.getBestRecord()
        updateState { copy(currentBestRecordInSeconds = currentBestRecord) }
    }

    private fun List<StartedChallenge>.getBestRecord(): Long {
        return this.maxOfOrNull { it.currentRecordInSeconds ?: 0 } ?: 0
    }

    private fun updateCategoryList(challenges: List<StartedChallenge>) {
        val categories = buildList {
            add(Category.ALL)
            addAll(
                challenges
                    .map { it.category }
                    .distinct()
                    .filterNot { it == Category.ALL },
            )
        }

        updateState { copy(categories = categories) }
    }

    fun processAction(action: HomeUiAction) {
        when (action) {
            HomeUiAction.OnScreenLoad -> updateData()
            HomeUiAction.OnRetryClick -> retryOnError()
            HomeUiAction.OnCloseCompletedChallengeNotification -> clearRecentCompletedChallenges()
            HomeUiAction.OnDismissTierUpAnimation -> dismissTierUpAnimation()
            HomeUiAction.OnSortOrderClick -> showSortOrderBottomSheet()
            HomeUiAction.OnDismissSortOrder -> dismissSortOrderBottomSheet()
            is HomeUiAction.OnCategorySelect -> updateSelectedCategory(action.category)
            is HomeUiAction.OnSortOrderSelect -> updateSortOrder(action.sortOrder)
        }
    }

    private fun updateData() {
        if (challengeSyncState.value.isSuccess()) syncTrigger.tryEmit(Unit)
        if (unViewedNotificationState.value.isSuccess()) notificationTrigger.tryEmit(Unit)
        if (userName.value.isSuccess()) userNameTrigger.tryEmit(Unit)
    }

    private fun retryOnError() {
        if (challengeSyncState.value.isError()) challengeSyncState.restart()
        if (unViewedNotificationState.value.isError()) unViewedNotificationState.restart()
        if (userName.value.isError()) userName.restart()
    }

    private fun clearRecentCompletedChallenges() = viewModelScope.launch {
        challengeRepository.clearRecentCompletedChallenges()
    }

    private fun dismissTierUpAnimation() = viewModelScope.launch {
        val animation = uiState.value.tierUpAnimation ?: return@launch

        challengeRepository.setCurrentTier(animation.to)
        updateState { copy(tierUpAnimation = null) }
    }

    private fun showSortOrderBottomSheet() {
        updateState { copy(shouldShowSortOrderBottomSheet = true) }
    }

    private fun dismissSortOrderBottomSheet() {
        updateState { copy(shouldShowSortOrderBottomSheet = false) }
    }

    private fun updateSelectedCategory(category: Category) {
        updateState { copy(selectedCategory = category) }
    }

    private fun updateSortOrder(sortOrder: SortOrder) = viewModelScope.launch {
        challengeRepository.setSortOrder(sortOrder)
    }
}

private sealed interface SyncTrigger {
    data object Initial : SyncTrigger
    data object Manual : SyncTrigger
    data object TimeChanged : SyncTrigger
}
