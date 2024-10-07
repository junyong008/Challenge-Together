package com.yjy.core.data.util

import com.yjy.core.datastore.SessionDataSource
import com.yjy.core.network.util.SessionManager
import javax.inject.Inject

internal class SessionManagerImpl @Inject constructor(
    private val sessionDataSource: SessionDataSource,
) : SessionManager {

    override suspend fun getSessionToken(): String? = sessionDataSource.getToken()

    override suspend fun setSessionToken(token: String?) {
        sessionDataSource.setToken(token)
    }
}
