package com.yjy.feature.premium.model

sealed interface PremiumUiEvent {
    data object PurchaseSuccess : PremiumUiEvent
    data object PurchaseFailure : PremiumUiEvent
}
