package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.challenge.AddChallengeRequest
import com.yjy.data.network.request.challenge.EditChallengeTitleDescriptionRequest
import com.yjy.data.network.response.challenge.AddChallengeResponse
import com.yjy.data.network.response.challenge.GetMyChallengesResponse
import com.yjy.data.network.response.challenge.GetRecordsResponse
import com.yjy.data.network.service.ChallengeTogetherService
import javax.inject.Inject

internal class ChallengeDataSourceImpl @Inject constructor(
    private val challengeTogetherService: ChallengeTogetherService,
) : ChallengeDataSource {

    override suspend fun addChallenge(request: AddChallengeRequest): NetworkResult<AddChallengeResponse> =
        challengeTogetherService.addChallenge(request)

    override suspend fun editChallengeCategory(challengeId: Int, category: String): NetworkResult<Unit> =
        challengeTogetherService.editChallengeCategory(challengeId, category)

    override suspend fun editChallengeTitleDescription(
        request: EditChallengeTitleDescriptionRequest,
    ): NetworkResult<Unit> = challengeTogetherService.editChallengeTitleDescription(request)

    override suspend fun editChallengeTargetDays(challengeId: Int, targetDays: String): NetworkResult<Unit> =
        challengeTogetherService.editChallengeGoal(challengeId, targetDays)

    override suspend fun getRecords(): NetworkResult<GetRecordsResponse> =
        challengeTogetherService.getRecords()

    override suspend fun getMyChallenges(): NetworkResult<GetMyChallengesResponse> =
        challengeTogetherService.getMyChallenges()
}
