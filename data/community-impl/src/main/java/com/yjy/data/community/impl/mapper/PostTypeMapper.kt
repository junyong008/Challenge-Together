package com.yjy.data.community.impl.mapper

import com.yjy.data.database.model.CommunityPostType
import com.yjy.model.community.SimpleCommunityPostType

private const val TYPE_ALL = "all"
private const val TYPE_BOOKMARKED = "bookmarked"
private const val TYPE_AUTHORED = "authored"
private const val TYPE_COMMENTED = "commented"

fun String.toCommunityPostType() = when (this) {
    TYPE_ALL -> CommunityPostType.ALL
    TYPE_BOOKMARKED -> CommunityPostType.BOOKMARKED
    TYPE_COMMENTED -> CommunityPostType.COMMENTED
    TYPE_AUTHORED -> CommunityPostType.AUTHORED
    else -> throw IllegalArgumentException("Unknown Post Type: $this")
}

fun SimpleCommunityPostType.toInternalModel() = when (this) {
    SimpleCommunityPostType.ALL -> CommunityPostType.ALL
    SimpleCommunityPostType.BOOKMARKED -> CommunityPostType.BOOKMARKED
    SimpleCommunityPostType.COMMENTED -> CommunityPostType.COMMENTED
    SimpleCommunityPostType.AUTHORED -> CommunityPostType.AUTHORED
}
