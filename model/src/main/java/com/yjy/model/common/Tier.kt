package com.yjy.model.common

import java.time.Duration
import kotlin.math.ceil

enum class Tier(val requireSeconds: Long) {
    UNSPECIFIED(-1),
    IRON(0),
    BRONZE(Duration.ofDays(3).seconds),
    SILVER(Duration.ofDays(7).seconds),
    GOLD(Duration.ofDays(14).seconds),
    PLATINUM(Duration.ofDays(30).seconds),
    DIAMOND(Duration.ofDays(60).seconds),
    MASTER(Duration.ofDays(100).seconds),
    LEGEND(Duration.ofDays(180).seconds),
    ;

    companion object {
        private const val SECONDS_PER_DAY = 24 * 60 * 60L

        val highestTier: Tier = entries.last { it != UNSPECIFIED }

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

        fun getTierProgress(currentTier: Tier, recordInSeconds: Long): TierProgress {
            if (currentTier == LEGEND) { return TierProgress(remainingDays = 0, progress = 1f) }

            val nextTier = getNextTier(currentTier)
            val remainingSeconds = nextTier.requireSeconds - recordInSeconds
            val remainingDays = ceil(remainingSeconds.toDouble() / (SECONDS_PER_DAY)).toInt()
            val progress = recordInSeconds.toFloat() / nextTier.requireSeconds

            return TierProgress(
                remainingDays = remainingDays.coerceAtLeast(0),
                progress = progress.coerceIn(0f, 1f),
            )
        }
    }
}

data class TierProgress(
    val remainingDays: Int,
    val progress: Float,
)
