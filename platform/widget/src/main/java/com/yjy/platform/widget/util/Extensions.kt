package com.yjy.platform.widget.util

import com.yjy.common.core.constants.TimeConst.SECONDS_PER_DAY
import com.yjy.model.challenge.SimpleStartedChallenge
import com.yjy.model.challenge.core.TargetDays

fun SimpleStartedChallenge.calculateProgressPercentage(): Float? {
    return when (val targetDays = this.targetDays) {
        is TargetDays.Fixed -> {
            val targetSeconds = targetDays.days * SECONDS_PER_DAY
            (currentRecordInSeconds.toFloat() / targetSeconds).coerceIn(0f, 1f)
        }
        TargetDays.Infinite -> null
    }
}
