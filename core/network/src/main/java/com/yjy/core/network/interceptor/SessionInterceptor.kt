package com.yjy.core.network.interceptor

import com.yjy.core.common.network.HttpStatusCodes.UNAUTHORIZED
import com.yjy.core.network.util.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

internal class SessionInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val savedSessionToken = runBlocking { sessionManager.getSessionToken() }
        savedSessionToken?.let {
            requestBuilder.addHeader("X-Session-ID", it)
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == UNAUTHORIZED) {
            clearSession()
        } else {
            val newToken = response.headers["X-Session-ID"]
            updateSessionToken(newToken)
        }

        return response
    }

    private fun clearSession() = runBlocking {
        sessionManager.setSessionToken(null)
    }

    private fun updateSessionToken(newToken: String?) = runBlocking {
        newToken?.let { sessionManager.setSessionToken(it) }
    }
}
