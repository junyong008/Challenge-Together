package com.yjy.feature.changename.model

data class ChangeNameUiState(
    val name: String = "",
    val isChangingName: Boolean = false,
    val isNameLengthValid: Boolean = false,
    val isNameHasOnlyConsonantOrVowel: Boolean = false,
)
