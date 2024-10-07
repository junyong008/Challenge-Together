package com.yjy.core.data.repository

import com.yjy.core.common.network.NetworkResult
import com.yjy.core.network.datasource.AuthDataSource
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
) : AuthRepository {

    override suspend fun login(email: String, password: String): NetworkResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun checkEmailDuplicate(email: String): NetworkResult<Unit> =
        authDataSource.checkEmailDuplicate(email)
}
