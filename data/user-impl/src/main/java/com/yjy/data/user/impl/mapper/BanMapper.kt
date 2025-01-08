package com.yjy.data.user.impl.mapper

import com.yjy.data.network.response.user.CheckBanResponse
import com.yjy.model.common.Ban
import com.yjy.model.common.ReportReason

internal fun CheckBanResponse.toModel() = Ban(
    reason = ReportReason.entries.find { it.name == reason } ?: ReportReason.DISTURBING_BEHAVIOR,
    endAt = endDateTime.toLocalDateTime(),
)
