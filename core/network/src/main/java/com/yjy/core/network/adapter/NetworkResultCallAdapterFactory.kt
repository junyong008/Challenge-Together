package com.yjy.core.network.adapter

import com.yjy.core.common.network.NetworkResult
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class NetworkResultCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) return null
        if (returnType !is ParameterizedType) return null

        val wrapperType = getParameterUpperBound(0, returnType)
        if (getRawType(wrapperType) != NetworkResult::class.java) return null
        if (wrapperType !is ParameterizedType) return null

        val bodyType = getParameterUpperBound(0, wrapperType)
        return NetworkResultCallAdapter<Any>(bodyType)
    }
}
