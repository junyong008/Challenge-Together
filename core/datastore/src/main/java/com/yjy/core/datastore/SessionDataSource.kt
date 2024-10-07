package com.yjy.core.datastore

interface SessionDataSource {
    suspend fun getToken(): String?
    suspend fun setToken(token: String?)
}
