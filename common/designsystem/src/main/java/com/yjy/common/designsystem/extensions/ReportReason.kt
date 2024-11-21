package com.yjy.common.designsystem.extensions

import com.yjy.common.designsystem.R
import com.yjy.model.common.ReportReason

fun ReportReason.getDisplayNameResId(): Int {
    return when (this) {
        ReportReason.COMMERCIAL_CONTENT -> R.string.common_designsystem_report_reason_commercial_content
        ReportReason.INAPPROPRIATE_CONTENT -> R.string.common_designsystem_report_reason_inappropriate_content
        ReportReason.DISTURBING_BEHAVIOR -> R.string.common_designsystem_report_reason_disturbing_behavior
        ReportReason.DEFAMATION -> R.string.common_designsystem_report_reason_defamation
        ReportReason.INCORRECT_INFO -> R.string.common_designsystem_report_reason_incorrect_info
    }
}
