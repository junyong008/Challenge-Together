package com.yjy.feature.changepassword.model

sealed interface ChangePasswordUiAction {
    data object OnBackClick : ChangePasswordUiAction
    data object OnCancelExit : ChangePasswordUiAction
    data object OnConfirmExit : ChangePasswordUiAction
    data class OnPasswordUpdated(val password: String) : ChangePasswordUiAction
    data object OnChangeClick : ChangePasswordUiAction
    data object OnCancelChange : ChangePasswordUiAction
    data class OnConfirmChange(val password: String) : ChangePasswordUiAction
}
