package com.yjy.data.network.util

interface SessionManager {
    suspend fun getSessionToken(): String?
    suspend fun setSessionToken(token: String?)
}
