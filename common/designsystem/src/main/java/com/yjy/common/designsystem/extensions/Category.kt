package com.yjy.common.designsystem.extensions

import com.yjy.common.designsystem.R
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.model.challenge.Category

fun Category.getDisplayNameResId(): Int {
    return when (this) {
        Category.ALL -> R.string.common_designsystem_category_all
        Category.QUIT_SMOKING -> R.string.common_designsystem_category_quit_smoking
        Category.QUIT_DRINKING -> R.string.common_designsystem_category_quit_drinking
        Category.QUIT_PORN -> R.string.common_designsystem_category_quit_porn
        Category.QUIT_GAMING -> R.string.common_designsystem_category_quit_gaming
        Category.QUIT_YOUTUBE -> R.string.common_designsystem_category_quit_youtube
        Category.QUIT_SNS -> R.string.common_designsystem_category_quit_sns
        Category.QUIT_GLUTEN -> R.string.common_designsystem_category_quit_gluten
        Category.QUIT_GAMBLING -> R.string.common_designsystem_category_quit_gambling
        Category.QUIT_DRUGS -> R.string.common_designsystem_category_quit_drugs
        Category.QUIT_CAFFEINE -> R.string.common_designsystem_category_quit_caffeine
        Category.QUIT_EATING_OUT -> R.string.common_designsystem_category_quit_eating_out
        Category.QUIT_SHOPPING -> R.string.common_designsystem_category_quit_shopping
        Category.QUIT_SWEARING -> R.string.common_designsystem_category_quit_swearing
        Category.QUIT_SMARTPHONE -> R.string.common_designsystem_category_quit_smartphone
    }
}

fun Category.getIconResId(): Int {
    return when (this) {
        Category.ALL -> ChallengeTogetherIcons.All
        Category.QUIT_SMOKING -> ChallengeTogetherIcons.QuitSmoking
        Category.QUIT_DRINKING -> ChallengeTogetherIcons.QuitDrinking
        Category.QUIT_PORN -> ChallengeTogetherIcons.QuitPorn
        Category.QUIT_GAMING -> ChallengeTogetherIcons.QuitGaming
        Category.QUIT_YOUTUBE -> ChallengeTogetherIcons.QuitYoutube
        Category.QUIT_SNS -> ChallengeTogetherIcons.QuitSns
        Category.QUIT_GLUTEN -> ChallengeTogetherIcons.QuitGluten
        Category.QUIT_GAMBLING -> ChallengeTogetherIcons.QuitGambling
        Category.QUIT_DRUGS -> ChallengeTogetherIcons.QuitDrugs
        Category.QUIT_CAFFEINE -> ChallengeTogetherIcons.QuitCaffeine
        Category.QUIT_EATING_OUT -> ChallengeTogetherIcons.QuitEatingOut
        Category.QUIT_SHOPPING -> ChallengeTogetherIcons.QuitShopping
        Category.QUIT_SWEARING -> ChallengeTogetherIcons.QuitSwearing
        Category.QUIT_SMARTPHONE -> ChallengeTogetherIcons.QuitSmartphone
    }
}
