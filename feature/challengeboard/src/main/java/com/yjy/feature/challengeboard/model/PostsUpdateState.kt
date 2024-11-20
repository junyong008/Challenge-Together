package com.yjy.feature.challengeboard.model

sealed interface PostsUpdateState {
    data object Connected : PostsUpdateState
    data object Loading : PostsUpdateState
    data object Error : PostsUpdateState
}

fun PostsUpdateState.isLoading(): Boolean = this is PostsUpdateState.Loading
fun PostsUpdateState.isError(): Boolean = this is PostsUpdateState.Error
