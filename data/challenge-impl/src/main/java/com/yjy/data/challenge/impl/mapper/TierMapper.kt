package com.yjy.data.challenge.impl.mapper

import com.yjy.data.datastore.api.ChallengePreferences
import com.yjy.model.common.Tier

fun Tier.toProto(): ChallengePreferences.Tier = when (this) {
    Tier.UNSPECIFIED -> ChallengePreferences.Tier.TIER_UNSPECIFIED
    Tier.IRON -> ChallengePreferences.Tier.TIER_IRON
    Tier.BRONZE -> ChallengePreferences.Tier.TIER_BRONZE
    Tier.SILVER -> ChallengePreferences.Tier.TIER_SILVER
    Tier.GOLD -> ChallengePreferences.Tier.TIER_GOLD
    Tier.PLATINUM -> ChallengePreferences.Tier.TIER_PLATINUM
    Tier.DIAMOND -> ChallengePreferences.Tier.TIER_DIAMOND
    Tier.MASTER -> ChallengePreferences.Tier.TIER_MASTER
    Tier.LEGEND -> ChallengePreferences.Tier.TIER_LEGEND
}

fun ChallengePreferences.Tier.toModel(): Tier = when (this) {
    ChallengePreferences.Tier.TIER_IRON -> Tier.IRON
    ChallengePreferences.Tier.TIER_BRONZE -> Tier.BRONZE
    ChallengePreferences.Tier.TIER_SILVER -> Tier.SILVER
    ChallengePreferences.Tier.TIER_GOLD -> Tier.GOLD
    ChallengePreferences.Tier.TIER_PLATINUM -> Tier.PLATINUM
    ChallengePreferences.Tier.TIER_DIAMOND -> Tier.DIAMOND
    ChallengePreferences.Tier.TIER_MASTER -> Tier.MASTER
    ChallengePreferences.Tier.TIER_LEGEND -> Tier.LEGEND
    ChallengePreferences.Tier.TIER_UNSPECIFIED,
    ChallengePreferences.Tier.UNRECOGNIZED -> Tier.UNSPECIFIED
}
