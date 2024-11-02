package com.yjy.common.network

sealed interface NetworkResult<out T> {

    data class Success<T>(val data: T) : NetworkResult<T>

    sealed interface Failure : NetworkResult<Nothing> {
        data class HttpError(val code: Int, val message: String?, val body: String) : Failure
        data class NetworkError(val throwable: Throwable) : Failure
        data class UnknownApiError(val throwable: Throwable) : Failure
    }
}

inline fun <T, E> handleNetworkResult(
    result: NetworkResult<T>,
    onSuccess: (T) -> E,
    onHttpError: (Int) -> E,
    onNetworkError: () -> E,
    onUnknownError: () -> E,
): E {
    return when (result) {
        is NetworkResult.Success -> onSuccess(result.data)
        is NetworkResult.Failure.HttpError -> onHttpError(result.code)
        is NetworkResult.Failure.NetworkError -> onNetworkError()
        is NetworkResult.Failure.UnknownApiError -> onUnknownError()
    }
}

inline fun <T> NetworkResult<T>.onSuccess(
    action: (value: T) -> Unit,
): NetworkResult<T> {
    if (this is NetworkResult.Success) action(data)
    return this
}

inline fun <T> NetworkResult<T>.onFailure(
    action: (error: NetworkResult.Failure) -> Unit,
): NetworkResult<T> {
    if (this is NetworkResult.Failure) action(this)
    return this
}

inline fun <T, R> NetworkResult<T>.map(
    transform: (T) -> R,
): NetworkResult<R> = when (this) {
    is NetworkResult.Success -> NetworkResult.Success(transform(data))
    is NetworkResult.Failure -> this
}
