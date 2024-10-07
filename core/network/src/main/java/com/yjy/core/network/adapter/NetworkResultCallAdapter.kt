package com.yjy.core.network.adapter

import com.yjy.core.common.network.NetworkResult
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type

internal class NetworkResultCallAdapter<T>(
    private val responseType: Type,
) : CallAdapter<T, Call<NetworkResult<T>>> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<T>): Call<NetworkResult<T>> = NetworkResultCall(call, responseType)
}

private class NetworkResultCall<T>(
    private val delegate: Call<T>,
    private val responseType: Type,
) : Call<NetworkResult<T>> {

    override fun enqueue(callback: Callback<NetworkResult<T>>) {
        delegate.enqueue(object : Callback<T> {

            private fun Response<T>.toNetworkResult(): NetworkResult<T> {
                if (!isSuccessful) {
                    return NetworkResult.Failure.HttpError(
                        code = code(),
                        message = message(),
                        body = errorBody().toString()
                    )
                }

                if (body() == null && responseType == Unit::class.java) {
                    @Suppress("UNCHECKED_CAST")
                    return NetworkResult.Success(Unit as T)
                }

                if (body() == null) {
                    return NetworkResult.Failure.UnknownApiError(
                        IllegalStateException("Response Code: ${code()} / body: Null")
                    )
                }

                return NetworkResult.Success(data = body()!!)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                callback.onResponse(
                    this@NetworkResultCall,
                    Response.success(response.toNetworkResult())
                )
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val error = if (t is IOException) {
                    NetworkResult.Failure.NetworkError(t)
                } else {
                    NetworkResult.Failure.UnknownApiError(t)
                }
                callback.onResponse(this@NetworkResultCall, Response.success(error))
            }
        })
    }

    override fun execute(): Response<NetworkResult<T>> = throw NotImplementedError()
    override fun clone(): Call<NetworkResult<T>> = NetworkResultCall(delegate.clone(), responseType)
    override fun request(): Request = delegate.request()
    override fun timeout(): Timeout = delegate.timeout()
    override fun isExecuted(): Boolean = delegate.isExecuted
    override fun isCanceled(): Boolean = delegate.isCanceled
    override fun cancel() {
        delegate.cancel()
    }
}