package com.yjy.common.designsystem.extensions

import com.yjy.common.designsystem.R
import com.yjy.model.challenge.SortOrder

fun SortOrder.getDisplayNameResId(): Int {
    return when (this) {
        SortOrder.LATEST -> R.string.common_designsystem_sort_order_latest
        SortOrder.OLDEST -> R.string.common_designsystem_sort_order_oldest
        SortOrder.TITLE -> R.string.common_designsystem_sort_order_title
        SortOrder.HIGHEST_RECORD -> R.string.common_designsystem_sort_order_highest_record
        SortOrder.LOWEST_RECORD -> R.string.common_designsystem_sort_order_lowest_record
    }
}
