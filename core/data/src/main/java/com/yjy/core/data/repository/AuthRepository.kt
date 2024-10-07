package com.yjy.core.data.repository

import com.yjy.core.common.network.NetworkResult

interface AuthRepository {
    suspend fun login(email: String, password: String): NetworkResult<Unit>
    suspend fun checkEmailDuplicate(email: String): NetworkResult<Unit>
}
