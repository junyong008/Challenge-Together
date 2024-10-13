package com.yjy.data.datastore.api

import kotlinx.coroutines.flow.Flow

interface UserPreferencesDataSource {
    val isLoggedIn: Flow<Boolean>
    suspend fun getLoggedIn(): Boolean
    suspend fun setLoggedIn(loggedIn: Boolean)
}
