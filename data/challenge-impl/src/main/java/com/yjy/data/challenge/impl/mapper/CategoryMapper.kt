package com.yjy.data.challenge.impl.mapper

import com.yjy.model.challenge.Category

// 이전 버전과의 호환을 위해 과거와 동일한 String으로 변환하여 전송.
internal fun Category.toRequestString(): String = when (this) {
    Category.ALL -> "ic_trop"
    Category.QUIT_SMOKING -> "ic_smoke"
    Category.QUIT_DRINKING -> "ic_drink"
    Category.QUIT_PORN -> "ic_porno"
    Category.QUIT_GAMING -> "ic_game"
    Category.QUIT_YOUTUBE -> "ic_youtube"
    Category.QUIT_SNS -> "ic_sns"
    Category.QUIT_GLUTEN -> "ic_bakery"
    Category.QUIT_GAMBLING -> "ic_casino"
    Category.QUIT_DRUGS -> "ic_drug"
    Category.QUIT_CAFFEINE -> "ic_coffee"
    Category.QUIT_EATING_OUT -> "ic_fastfood"
    Category.QUIT_SHOPPING -> "ic_shop"
    Category.QUIT_SWEARING -> "ic_cuss"
    Category.QUIT_SMARTPHONE -> "ic_cell"
}
