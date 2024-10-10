package com.yjy.core.common.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<State, Event>(
    initialState: State,
) : ViewModel() {

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _uiEvent = Channel<Event>()
    val uiEvent: Flow<Event> = _uiEvent.receiveAsFlow()

    protected fun sendEvent(event: Event) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    protected fun updateState(reducer: State.() -> State) {
        _uiState.update { it.reducer() }
    }
}
