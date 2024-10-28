package com.yjy.model

import java.time.Duration

enum class Tier(val requireSeconds: Long) {
    UNSPECIFIED(-1),
    IRON(0),
    BRONZE(Duration.ofDays(3).seconds),
    SILVER(Duration.ofDays(7).seconds),
    GOLD(Duration.ofDays(14).seconds),
    PLATINUM(Duration.ofDays(30).seconds),
    DIAMOND(Duration.ofDays(60).seconds),
    MASTER(Duration.ofDays(100).seconds),
    LEGEND(Duration.ofDays(180).seconds);

    companion object {
        fun getNextTier(current: Tier): Tier {
            return when (current) {
                IRON -> BRONZE
                BRONZE -> SILVER
                SILVER -> GOLD
                GOLD -> PLATINUM
                PLATINUM -> DIAMOND
                DIAMOND -> MASTER
                MASTER -> LEGEND
                LEGEND -> error("LEGEND tier cannot be next")
                UNSPECIFIED -> error("UNSPECIFIED tier cannot be next")
            }
        }

        fun getCurrentTier(recordInSeconds: Long): Tier {
            return entries.last { tier ->
                recordInSeconds >= tier.requireSeconds
            }
        }
    }
}
