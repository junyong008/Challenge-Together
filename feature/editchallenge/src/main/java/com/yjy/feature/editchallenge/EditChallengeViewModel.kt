package com.yjy.feature.editchallenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.common.network.onFailure
import com.yjy.common.network.onSuccess
import com.yjy.data.challenge.api.ChallengeRepository
import com.yjy.feature.editchallenge.model.EditChallengeUiAction
import com.yjy.feature.editchallenge.model.EditChallengeUiEvent
import com.yjy.feature.editchallenge.model.EditChallengeUiState
import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.TargetDays
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditChallengeViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditChallengeUiState())
    val uiState: StateFlow<EditChallengeUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<EditChallengeUiEvent>()
    val uiEvent: Flow<EditChallengeUiEvent> = _uiEvent.receiveAsFlow()

    private fun sendEvent(event: EditChallengeUiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    fun processAction(action: EditChallengeUiAction) {
        when (action) {
            is EditChallengeUiAction.OnEditCategory -> editCategory(action.challengeId, action.category)
            is EditChallengeUiAction.OnEditTargetDays -> editTargetDays(action.challengeId, action.targetDays)
            is EditChallengeUiAction.OnEditTitle -> {
                editTitleDescription(action.challengeId, action.title, action.description)
            }
        }
    }

    private fun editCategory(challengeId: Int, category: Category) = viewModelScope.launch {
        _uiState.update { it.copy(isEditing = true) }

        challengeRepository.editChallengeCategory(challengeId, category)
            .onSuccess { sendEvent(EditChallengeUiEvent.EditSuccess) }
            .onFailure { sendEvent(EditChallengeUiEvent.EditFailure) }
        _uiState.update { it.copy(isEditing = false) }
    }

    private fun editTitleDescription(
        challengeId: Int,
        title: String,
        description: String,
    ) = viewModelScope.launch {
        _uiState.update { it.copy(isEditing = true) }

        challengeRepository.editChallengeTitleDescription(challengeId, title, description)
            .onSuccess { sendEvent(EditChallengeUiEvent.EditSuccess) }
            .onFailure { sendEvent(EditChallengeUiEvent.EditFailure) }
        _uiState.update { it.copy(isEditing = false) }
    }

    private fun editTargetDays(challengeId: Int, targetDays: TargetDays) = viewModelScope.launch {
        _uiState.update { it.copy(isEditing = true) }

        challengeRepository.editChallengeTargetDays(challengeId, targetDays)
            .onSuccess { sendEvent(EditChallengeUiEvent.EditSuccess) }
            .onFailure { sendEvent(EditChallengeUiEvent.EditFailure) }
        _uiState.update { it.copy(isEditing = false) }
    }
}
