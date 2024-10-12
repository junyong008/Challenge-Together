package com.yjy.data.datastore.api

import kotlinx.coroutines.flow.Flow

interface SessionDataSource {
    val isSessionTokenAvailable: Flow<Boolean>
    suspend fun getToken(): String?
    suspend fun setToken(token: String?)
}
