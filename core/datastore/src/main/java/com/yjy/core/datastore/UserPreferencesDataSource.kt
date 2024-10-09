package com.yjy.core.datastore

interface UserPreferencesDataSource {
    suspend fun isLoggedIn(): Boolean
    suspend fun setLoggedIn(loggedIn: Boolean)
}
