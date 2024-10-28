package com.yjy.common.designsystem.extensions

import com.yjy.common.designsystem.R
import com.yjy.model.Tier

fun Tier.getDisplayNameResId(): Int {
    return when (this) {
        Tier.UNSPECIFIED -> R.string.common_designsystem_tier_unspecified
        Tier.IRON -> R.string.common_designsystem_tier_iron_description
        Tier.BRONZE -> R.string.common_designsystem_tier_bronze_description
        Tier.SILVER -> R.string.common_designsystem_tier_silver_description
        Tier.GOLD -> R.string.common_designsystem_tier_gold_description
        Tier.PLATINUM -> R.string.common_designsystem_tier_platinum_description
        Tier.DIAMOND -> R.string.common_designsystem_tier_diamond_description
        Tier.MASTER -> R.string.common_designsystem_tier_master_description
        Tier.LEGEND -> R.string.common_designsystem_tier_legend_description
    }
}
