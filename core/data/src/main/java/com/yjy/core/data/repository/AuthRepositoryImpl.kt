package com.yjy.core.data.repository

import com.yjy.core.common.network.NetworkResult
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
) : AuthRepository {

    override suspend fun login(email: String, password: String): NetworkResult<Unit> {
        TODO("Not yet implemented")
    }
}