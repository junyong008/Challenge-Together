package com.yjy.data.challenge.impl.mapper

import com.yjy.model.challenge.Category

// 이전 버전과의 호환을 위해 과거와 동일한 String으로 변환하여 전송.
private const val ALL_CATEGORY = "ic_trop"
private const val QUIT_SMOKING = "ic_smoke"
private const val QUIT_DRINKING = "ic_drink"
private const val QUIT_PORN = "ic_porno"
private const val QUIT_GAMING = "ic_game"
private const val QUIT_YOUTUBE = "ic_youtube"
private const val QUIT_SNS = "ic_sns"
private const val QUIT_GLUTEN = "ic_bakery"
private const val QUIT_GAMBLING = "ic_casino"
private const val QUIT_DRUGS = "ic_drug"
private const val QUIT_CAFFEINE = "ic_coffee"
private const val QUIT_EATING_OUT = "ic_fastfood"
private const val QUIT_SHOPPING = "ic_shop"
private const val QUIT_SWEARING = "ic_cuss"
private const val QUIT_SMARTPHONE = "ic_cell"

internal fun Category.toRequestString(): String = when (this) {
    Category.ALL -> ALL_CATEGORY
    Category.QUIT_SMOKING -> QUIT_SMOKING
    Category.QUIT_DRINKING -> QUIT_DRINKING
    Category.QUIT_PORN -> QUIT_PORN
    Category.QUIT_GAMING -> QUIT_GAMING
    Category.QUIT_YOUTUBE -> QUIT_YOUTUBE
    Category.QUIT_SNS -> QUIT_SNS
    Category.QUIT_GLUTEN -> QUIT_GLUTEN
    Category.QUIT_GAMBLING -> QUIT_GAMBLING
    Category.QUIT_DRUGS -> QUIT_DRUGS
    Category.QUIT_CAFFEINE -> QUIT_CAFFEINE
    Category.QUIT_EATING_OUT -> QUIT_EATING_OUT
    Category.QUIT_SHOPPING -> QUIT_SHOPPING
    Category.QUIT_SWEARING -> QUIT_SWEARING
    Category.QUIT_SMARTPHONE -> QUIT_SMARTPHONE
}

internal fun String.toCategory(): Category = when (this) {
    ALL_CATEGORY -> Category.ALL
    QUIT_SMOKING -> Category.QUIT_SMOKING
    QUIT_DRINKING -> Category.QUIT_DRINKING
    QUIT_PORN -> Category.QUIT_PORN
    QUIT_GAMING -> Category.QUIT_GAMING
    QUIT_YOUTUBE -> Category.QUIT_YOUTUBE
    QUIT_SNS -> Category.QUIT_SNS
    QUIT_GLUTEN -> Category.QUIT_GLUTEN
    QUIT_GAMBLING -> Category.QUIT_GAMBLING
    QUIT_DRUGS -> Category.QUIT_DRUGS
    QUIT_CAFFEINE -> Category.QUIT_CAFFEINE
    QUIT_EATING_OUT -> Category.QUIT_EATING_OUT
    QUIT_SHOPPING -> Category.QUIT_SHOPPING
    QUIT_SWEARING -> Category.QUIT_SWEARING
    QUIT_SMARTPHONE -> Category.QUIT_SMARTPHONE
    else -> throw IllegalArgumentException("Unknown category: $this")
}
