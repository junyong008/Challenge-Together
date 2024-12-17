package com.yjy.data.challenge.impl.mapper

import com.yjy.data.network.response.challenge.GetRecordsResponse
import com.yjy.model.challenge.UserRecord

internal fun GetRecordsResponse.toModel() = UserRecord(
    tryCount = tryCount,
    successCount = successCount,
    resetCount = resetCount,
    bestRecordInSeconds = recordInSeconds,
)
