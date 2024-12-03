package com.yjy.feature.completedchallenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.domain.GetCompletedChallengesUseCase
import com.yjy.model.challenge.base.Challenge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CompletedChallengesViewModel @Inject constructor(
    getCompletedChallengesUseCase: GetCompletedChallengesUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val completedChallenges = getCompletedChallengesUseCase()
        .combine(searchQuery) { challenges, query ->
            if (query.isEmpty()) {
                challenges
            } else {
                challenges.filter { it.matchesQuery(query) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    private fun Challenge.matchesQuery(query: String) =
        title.contains(query, ignoreCase = true) || description.contains(query, ignoreCase = true)

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
