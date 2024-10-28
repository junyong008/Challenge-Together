package com.yjy.data.user.impl.repository

import com.yjy.common.network.NetworkResult
import com.yjy.common.network.map
import com.yjy.data.network.datasource.UserDataSource
import com.yjy.data.user.api.UserRepository
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : UserRepository {

    override suspend fun getUserName(): NetworkResult<String> =
        userDataSource.getUserName().map { it.userName }

    override suspend fun getUnViewedNotificationCount(): NetworkResult<Int> =
        userDataSource.getUnViewedNotificationCount().map { it.unViewedNotificationCount }
}