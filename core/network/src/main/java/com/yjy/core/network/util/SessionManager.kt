package com.yjy.core.network.util

interface SessionManager {
    suspend fun getSessionToken(): String?
    suspend fun setSessionToken(token: String?)
}
