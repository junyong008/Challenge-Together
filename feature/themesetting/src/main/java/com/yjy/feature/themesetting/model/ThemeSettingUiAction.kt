package com.yjy.feature.themesetting.model

sealed interface ThemeSettingUiAction {
    data object OnSelectSystemTheme : ThemeSettingUiAction
    data object OnSelectDarkTheme : ThemeSettingUiAction
    data object OnSelectLightTheme : ThemeSettingUiAction
}
