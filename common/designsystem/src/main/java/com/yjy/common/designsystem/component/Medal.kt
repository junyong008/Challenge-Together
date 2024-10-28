package com.yjy.common.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yjy.common.designsystem.R
import com.yjy.common.designsystem.icon.ChallengeTogetherIcons
import com.yjy.model.Tier

@Composable
fun RibbonMedal(
    tier: Tier,
    modifier: Modifier = Modifier,
) {
    if (tier == Tier.UNSPECIFIED) {
        Box(modifier = modifier)
        return
    }

    StableImage(
        drawableResId = when (tier) {
            Tier.IRON -> ChallengeTogetherIcons.IronRibbon
            Tier.BRONZE -> ChallengeTogetherIcons.BronzeRibbon
            Tier.SILVER -> ChallengeTogetherIcons.SilverRibbon
            Tier.GOLD -> ChallengeTogetherIcons.GoldRibbon
            Tier.PLATINUM -> ChallengeTogetherIcons.PlatinumRibbon
            Tier.DIAMOND -> ChallengeTogetherIcons.DiamondRibbon
            Tier.MASTER -> ChallengeTogetherIcons.MasterRibbon
            Tier.LEGEND -> ChallengeTogetherIcons.LegendRibbon
            else -> error("Unreachable")
        },
        descriptionResId = getTierDescriptionResId(tier),
        modifier = modifier,
    )
}

@Composable
fun CircleMedal(
    tier: Tier,
    modifier: Modifier = Modifier,
) {
    if (tier == Tier.UNSPECIFIED) {
        Box(modifier = modifier)
        return
    }

    StableImage(
        drawableResId = when (tier) {
            Tier.IRON -> ChallengeTogetherIcons.IronMedal
            Tier.BRONZE -> ChallengeTogetherIcons.BronzeMedal
            Tier.SILVER -> ChallengeTogetherIcons.SilverMedal
            Tier.GOLD -> ChallengeTogetherIcons.GoldMedal
            Tier.PLATINUM -> ChallengeTogetherIcons.PlatinumMedal
            Tier.DIAMOND -> ChallengeTogetherIcons.DiamondMedal
            Tier.MASTER -> ChallengeTogetherIcons.MasterMedal
            Tier.LEGEND -> ChallengeTogetherIcons.LegendMedal
            else -> error("Unreachable")
        },
        descriptionResId = getTierDescriptionResId(tier),
        modifier = modifier,
    )
}

@Composable
private fun getTierDescriptionResId(tier: Tier): Int {
    return when (tier) {
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
