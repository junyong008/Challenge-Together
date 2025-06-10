package com.yjy.feature.themesetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjy.data.user.api.UserRepository
import com.yjy.feature.themesetting.model.ThemeSettingUiAction
import com.yjy.feature.themesetting.model.ThemeSettingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeSettingViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val themeState = userRepository.isDarkTheme
        .map {
            ThemeSettingUiState.Success(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeSettingUiState.Loading,
        )

    fun processAction(action: ThemeSettingUiAction) {
        when (action) {
            ThemeSettingUiAction.OnSelectSystemTheme -> setTheme(null)
            ThemeSettingUiAction.OnSelectDarkTheme -> setTheme(true)
            ThemeSettingUiAction.OnSelectLightTheme -> setTheme(false)
        }
    }

    private fun setTheme(isDarkTheme: Boolean?) {
        viewModelScope.launch {
            userRepository.setDarkTheme(isDarkTheme)
        }
    }
}
