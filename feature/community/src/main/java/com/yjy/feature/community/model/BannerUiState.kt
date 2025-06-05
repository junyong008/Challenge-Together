package com.yjy.feature.community.model

import com.yjy.model.community.Banner

sealed interface BannerUiState {
    data class Success(val banners: List<Banner>) : BannerUiState
    data object Error : BannerUiState
    data object Loading : BannerUiState
}

fun BannerUiState.isLoading(): Boolean = this is BannerUiState.Loading
fun BannerUiState.isError(): Boolean = this is BannerUiState.Error
fun BannerUiState.getBannerOrNull(): List<Banner>? = (this as? BannerUiState.Success)?.banners
