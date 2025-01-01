package com.yjy.feature.together

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yjy.data.challenge.api.WaitingChallengeRepository
import com.yjy.feature.together.model.TogetherUiEvent
import com.yjy.model.challenge.core.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TogetherViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val waitingChallengeRepository: WaitingChallengeRepository,
) : ViewModel() {

    private val category = savedStateHandle.getStateFlow<Category?>("category", null)

    private val _uiEvent = Channel<TogetherUiEvent>()
    val uiEvent: Flow<TogetherUiEvent> = _uiEvent.receiveAsFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isGlobalActive = MutableStateFlow(false)
    val isGlobalActive = _isGlobalActive.asStateFlow()

    private val _currentLanguageCode = MutableStateFlow("")

    val waitingChallenges = combine(
        category.filterNotNull(),
        searchQuery,
        _isGlobalActive,
        _currentLanguageCode,
    ) { category, query, isGlobal, languageCode ->
        Triple(query, category, if (!isGlobal) languageCode else "")
    }.flatMapLatest { (query, category, languageCode) ->
        waitingChallengeRepository.getTogetherChallenges(
            query = query,
            category = category,
            languageCode = languageCode,
        )
    }.cachedIn(viewModelScope).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = PagingData.empty(),
    )

    private fun sendEvent(event: TogetherUiEvent) {
        viewModelScope.launch { _uiEvent.send(event) }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleGlobalMode() {
        _isGlobalActive.value = !_isGlobalActive.value
        if (_isGlobalActive.value) {
            sendEvent(TogetherUiEvent.GlobalOn)
        } else {
            sendEvent(TogetherUiEvent.GlobalOff)
        }
    }

    fun updateLanguageCode(languageCode: String) {
        _currentLanguageCode.value = languageCode
    }
}
