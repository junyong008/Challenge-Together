package com.yjy.feature.home

import com.yjy.common.core.base.BaseViewModel
import com.yjy.feature.home.model.HomeUiAction
import com.yjy.feature.home.model.HomeUiEvent
import com.yjy.feature.home.model.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

) : BaseViewModel<HomeUiState, HomeUiEvent>(initialState = HomeUiState()) {

    fun processAction(action: HomeUiAction) {
        when (action) {
            else -> Unit
        }
    }
}