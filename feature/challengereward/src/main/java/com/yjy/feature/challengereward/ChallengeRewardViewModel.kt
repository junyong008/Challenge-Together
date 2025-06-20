package com.yjy.feature.challengereward

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.core.extensions.restartableStateIn
import com.yjy.data.challenge.api.StartedChallengeRepository
import com.yjy.feature.challengereward.model.ChallengeRewardUiAction
import com.yjy.feature.challengereward.model.ChallengeRewardUiState
import com.yjy.feature.challengereward.model.RewardInfoUiState
import com.yjy.model.challenge.reward.Reward
import com.yjy.model.challenge.reward.RewardInfo
import com.yjy.model.challenge.reward.RewardUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class ChallengeRewardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    startedChallengeRepository: StartedChallengeRepository,
) : ViewModel() {

    private val challengeId = savedStateHandle.getStateFlow<Int?>("challengeId", null)

    private val _uiState = MutableStateFlow(ChallengeRewardUiState())
    val uiState: StateFlow<ChallengeRewardUiState> = _uiState.asStateFlow()

    val rewardInfoState = challengeId
        .filterNotNull()
        .flatMapLatest { id ->
            flow {
                emit(RewardInfoUiState.Success(
                    RewardInfo(
                        challengeId = 1,
                        rewardUnit = RewardUnit.Money,
                        amount = 10000.0,
                        amountPerSec = 2.7,
                        rewards = listOf(
                            Reward(id = 1, name = "Reward 1", price = 50000.0, relateUrl = "", hasPurchased = false),
                            Reward(id = 2, name = "Reward 2", price = 200.0, relateUrl = "https://www.naver.com/asfas2e152d23a1s35d1a35w", hasPurchased = false),
                            Reward(id = 3, name = "Reward 3", price = 300.0, relateUrl = "", hasPurchased = true),
                        ),
                    )
                ))
            }
        }.restartableStateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0),
            initialValue = RewardInfoUiState.Loading,
        )

    fun processAction(action: ChallengeRewardUiAction) {
        when (action) {
            ChallengeRewardUiAction.OnRetryClick -> retryOnError()
            is ChallengeRewardUiAction.OnAddRewardClick -> addReward(action.name, action.price, action.url)
            is ChallengeRewardUiAction.OnEditAmountClick -> editAmount(action.challengeId, action.amount)
            is ChallengeRewardUiAction.OnPurchaseRewardClick -> purchaseReward(action.reward)
            is ChallengeRewardUiAction.OnCancelPurchaseRewardClick -> cancelPurchaseReward(action.reward)
            is ChallengeRewardUiAction.OnDeleteRewardClick -> deleteReward(action.reward)
            is ChallengeRewardUiAction.OnResetClick -> reset(action.challengeId)
            is ChallengeRewardUiAction.OnEditRewardClick -> editReward(action.reward)
        }
    }

    private fun retryOnError() {
        rewardInfoState.restart()
    }

    private fun addReward(name: String, price: Double, url: String) {
        // TODO: Add reward
    }

    private fun editAmount(challengeId: Int, amount: Double) {
        // TODO: Edit amount
    }

    private fun purchaseReward(reward: Reward) {
        // TODO: Purchase reward
    }

    private fun cancelPurchaseReward(reward: Reward) {

    }

    private fun deleteReward(reward: Reward) {

    }

    private fun reset(challengeId: Int) {

    }

    private fun editReward(reward: Reward) {

    }
}
