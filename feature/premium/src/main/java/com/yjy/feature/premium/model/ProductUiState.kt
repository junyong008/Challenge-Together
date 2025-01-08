package com.yjy.feature.premium.model

sealed interface ProductUiState {
    data object Loading : ProductUiState
    data object NotAvailable : ProductUiState
    data class Ready(val product: Product) : ProductUiState
}

fun ProductUiState.isReady(): Boolean = this is ProductUiState.Ready
fun ProductUiState.isLoading(): Boolean = this is ProductUiState.Loading
fun ProductUiState.getProductOrNull(): Product? = (this as? ProductUiState.Ready)?.product
