package com.yjy.core.network.datasource

import com.yjy.core.common.network.NetworkResult

interface AuthDataSource {
    suspend fun checkEmailDuplicate(email: String): NetworkResult<Unit>
}