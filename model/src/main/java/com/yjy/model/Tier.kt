package com.yjy.model

import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

enum class Tier(val requireSeconds: Long) {
    IRON(0),
    BRONZE(Duration.ofDays(3).seconds),
    SILVER(Duration.ofDays(7).seconds),
    GOLD(Duration.ofDays(14).seconds),
    PLATINUM(Duration.ofDays(30).seconds),
    DIAMOND(Duration.ofDays(60).seconds),
    MASTER(Duration.ofDays(100).seconds),
    LEGEND(Duration.ofDays(180).seconds);

    companion object {
        fun getCurrentTier(lastResetDate: LocalDateTime): Tier {
            val secondsSinceReset = ChronoUnit.SECONDS.between(lastResetDate, LocalDateTime.now())
            return entries.last { tier ->
                secondsSinceReset >= tier.requireSeconds
            }
        }

        fun getHighestTier(highestRecord: Long): Tier {
            return entries.last { tier ->
                highestRecord >= tier.requireSeconds
            }
        }
    }
}
