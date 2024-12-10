package com.yjy.feature.together

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yjy.data.challenge.api.WaitingChallengeRepository
import com.yjy.model.challenge.core.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TogetherViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val waitingChallengeRepository: WaitingChallengeRepository,
) : ViewModel() {

    private val category = savedStateHandle.getStateFlow<Category?>("category", null)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val waitingChallenges = category
        .filterNotNull()
        .combine(searchQuery) { category, query ->
            Pair(query, category)
        }
        .flatMapLatest { (query, category) ->
            waitingChallengeRepository.getTogetherChallenges(query, category)
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty(),
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
