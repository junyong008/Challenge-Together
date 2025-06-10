package com.yjy.data.challenge.impl.mapper

import com.yjy.data.network.response.challenge.GetResetInfoResponse
import com.yjy.data.network.response.challenge.ResetRecordResponse
import com.yjy.model.challenge.ResetInfo
import com.yjy.model.challenge.ResetRecord

internal fun GetResetInfoResponse.toModel() = ResetInfo(
    isCompleted = isCompleted,
    resetRecords = resetRecords.map { it.toModel() },
)

internal fun ResetRecordResponse.toModel() = ResetRecord(
    id = resetRecordId,
    resetDateTime = resetDateTime.toLocalDateTime(),
    recordInSeconds = recordInSeconds,
    content = content,
    isCurrent = false,
)
