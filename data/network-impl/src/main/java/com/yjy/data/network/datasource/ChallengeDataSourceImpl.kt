package com.yjy.data.network.datasource

import com.yjy.common.network.NetworkResult
import com.yjy.data.network.request.AddChallengeRequest
import com.yjy.data.network.request.EditChallengeTitleDescriptionRequest
import com.yjy.data.network.request.ResetChallengeRequest
import com.yjy.data.network.response.AddChallengeResponse
import com.yjy.data.network.response.GetMyChallengesResponse
import com.yjy.data.network.response.GetStartedChallengeDetailResponse
import com.yjy.data.network.service.ChallengeTogetherService
import javax.inject.Inject

internal class ChallengeDataSourceImpl @Inject constructor(
    private val challengeTogetherService: ChallengeTogetherService,
) : ChallengeDataSource {

    override suspend fun addChallenge(request: AddChallengeRequest): NetworkResult<AddChallengeResponse> =
        challengeTogetherService.addChallenge(request)

    override suspend fun resetStartedChallenge(request: ResetChallengeRequest): NetworkResult<Unit> =
        challengeTogetherService.resetChallenge(request)

    override suspend fun deleteStartedChallenge(challengeId: String): NetworkResult<Unit> =
        challengeTogetherService.deleteStartedChallenge(challengeId)

    override suspend fun editChallengeCategory(challengeId: String, category: String): NetworkResult<Unit> =
        challengeTogetherService.editChallengeCategory(challengeId, category)

    override suspend fun editChallengeTitleDescription(
        request: EditChallengeTitleDescriptionRequest,
    ): NetworkResult<Unit> =
        challengeTogetherService.editChallengeTitleDescription(request)

    override suspend fun editChallengeTargetDays(challengeId: String, targetDays: String): NetworkResult<Unit> =
        challengeTogetherService.editChallengeGoal(challengeId, targetDays)

    override suspend fun getMyChallenges(): NetworkResult<GetMyChallengesResponse> =
        challengeTogetherService.getMyChallenges()

    override suspend fun getStartedChallengeDetail(
        challengeId: String,
    ): NetworkResult<GetStartedChallengeDetailResponse> =
        challengeTogetherService.getStartedChallengeDetail(challengeId)
}
