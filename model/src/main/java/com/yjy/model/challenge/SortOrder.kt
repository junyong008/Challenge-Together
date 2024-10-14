package com.yjy.model.challenge

import com.yjy.model.R

enum class SortOrder(val displayNameResId: Int) {
    LATEST(R.string.model_sort_order_latest),
    OLDEST(R.string.model_sort_order_oldest),
    TITLE(R.string.model_sort_order_title),
    HIGHEST_RECORD(R.string.model_sort_order_highest_record),
    LOWEST_RECORD(R.string.model_sort_order_lowest_record);
}
