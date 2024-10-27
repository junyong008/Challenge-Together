package com.yjy.data.challenge.impl.util

import com.yjy.data.datastore.api.UserPreferencesDataSource
import com.yjy.data.network.util.TimeDiffManager
import javax.inject.Inject

internal class TimeDiffManagerImpl @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource,
) : TimeDiffManager {

    override suspend fun setTimeDiff(diff: Long) {
        userPreferencesDataSource.setTimeDiff(diff)
    }
}
