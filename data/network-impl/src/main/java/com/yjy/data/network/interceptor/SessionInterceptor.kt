package com.yjy.data.network.interceptor

import com.yjy.common.network.HttpStatusCodes.SESSION_EXPIRED
import com.yjy.data.network.util.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

internal class SessionInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val savedSessionToken = runBlocking { sessionManager.getSessionToken() }
        if (savedSessionToken != null) {
            requestBuilder.addHeader("X-Session-ID", savedSessionToken)
            Timber.d("sendSessionToken: $savedSessionToken")
        }

        val response = chain.proceed(requestBuilder.build())
        val newToken = response.headers["X-Session-ID"]

        if (response.code == SESSION_EXPIRED) {
            clearSession()
            Timber.d("clearSessionToken")
        }

        if (!newToken.isNullOrBlank()) {
            updateSessionToken(newToken)
            Timber.d("newSessionToken: $newToken")
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
