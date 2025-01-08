package com.yjy.feature.changename.model

sealed interface ChangeNameUiAction {
    data object OnRetryClick : ChangeNameUiAction
    data class OnChangeClick(val name: String) : ChangeNameUiAction
    data class OnNameUpdated(val name: String) : ChangeNameUiAction
}
