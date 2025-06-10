package com.yjy.feature.themesetting.model

sealed interface ThemeSettingUiState {
    data object Loading : ThemeSettingUiState
    data class Success(val isDarkTheme: Boolean?) : ThemeSettingUiState
}
