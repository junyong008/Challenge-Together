package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.response.GetNameResponse
import com.yjy.data.network.response.GetUnViewedNotificationCountResponse
import com.yjy.data.network.service.ChallengeTogetherService
import javax.inject.Inject

internal class UserDataSourceImpl @Inject constructor(
    private val challengeTogetherService: ChallengeTogetherService,
) : UserDataSource {

    override suspend fun getUserName(): NetworkResult<GetNameResponse> =
        challengeTogetherService.getUserName()

    override suspend fun getUnViewedNotificationCount(): NetworkResult<GetUnViewedNotificationCountResponse> =
        challengeTogetherService.getUnViewedNotificationCount()
}
