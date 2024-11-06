package com.yjy.navigation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.platform.time.TimeMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(timeMonitor: TimeMonitor) : ViewModel() {

    val isManualTime = timeMonitor.isAutoTime
        .map(Boolean::not)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false,
        )
}