package com.yjy.data.challenge.impl.util

import com.yjy.data.datastore.api.ChallengePreferencesDataSource
import com.yjy.data.network.util.TimeDiffManager
import javax.inject.Inject

internal class TimeDiffManagerImpl @Inject constructor(
    private val challengePreferencesDataSource: ChallengePreferencesDataSource,
) : TimeDiffManager {

    override suspend fun setTimeDiff(diff: Long) {
        challengePreferencesDataSource.setTimeDiff(diff)
    }
}
