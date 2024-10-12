package com.yjy.data.auth.impl.util

import com.yjy.data.datastore.api.SessionDataSource
import com.yjy.data.network.util.SessionManager
import javax.inject.Inject

internal class SessionManagerImpl @Inject constructor(
    private val sessionDataSource: SessionDataSource,
) : SessionManager {

    override suspend fun getSessionToken(): String? = sessionDataSource.getToken()

    override suspend fun setSessionToken(token: String?) {
        sessionDataSource.setToken(token)
    }
}
