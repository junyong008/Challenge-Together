package com.yjy.data.challenge.impl.mapper

import com.yjy.data.datastore.api.ChallengePreferences
import com.yjy.model.challenge.SortOrder

fun SortOrder.toProto(): ChallengePreferences.SortOrder = when (this) {
    SortOrder.LATEST -> ChallengePreferences.SortOrder.SORT_ORDER_LATEST
    SortOrder.OLDEST -> ChallengePreferences.SortOrder.SORT_ORDER_OLDEST
    SortOrder.TITLE -> ChallengePreferences.SortOrder.SORT_ORDER_TITLE
    SortOrder.HIGHEST_RECORD -> ChallengePreferences.SortOrder.SORT_ORDER_HIGHEST_RECORD
    SortOrder.LOWEST_RECORD -> ChallengePreferences.SortOrder.SORT_ORDER_LOWEST_RECORD
}

fun ChallengePreferences.SortOrder.toModel(): SortOrder = when (this) {
    ChallengePreferences.SortOrder.SORT_ORDER_LATEST -> SortOrder.LATEST
    ChallengePreferences.SortOrder.SORT_ORDER_OLDEST -> SortOrder.OLDEST
    ChallengePreferences.SortOrder.SORT_ORDER_TITLE -> SortOrder.TITLE
    ChallengePreferences.SortOrder.SORT_ORDER_HIGHEST_RECORD -> SortOrder.HIGHEST_RECORD
    ChallengePreferences.SortOrder.SORT_ORDER_LOWEST_RECORD -> SortOrder.LOWEST_RECORD
    ChallengePreferences.SortOrder.SORT_ORDER_UNSPECIFIED,
    ChallengePreferences.SortOrder.UNRECOGNIZED -> SortOrder.LATEST
}
