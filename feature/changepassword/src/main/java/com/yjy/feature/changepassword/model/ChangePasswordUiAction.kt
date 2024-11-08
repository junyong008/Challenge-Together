package com.yjy.feature.changepassword.model

sealed interface ChangePasswordUiAction {
    data class OnPasswordUpdated(val password: String) : ChangePasswordUiAction
    data class OnConfirmChange(val password: String) : ChangePasswordUiAction
}
