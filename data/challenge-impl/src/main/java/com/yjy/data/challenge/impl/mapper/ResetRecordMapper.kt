package com.yjy.data.challenge.impl.mapper

import com.yjy.data.network.response.GetResetRecordResponse
import com.yjy.model.challenge.ResetRecord

internal fun GetResetRecordResponse.toModel() = ResetRecord(
    id = resetRecordId,
    resetDateTime = resetDateTime.toLocalDateTime(),
    recordInSeconds = recordInSeconds,
    content = content,
)
