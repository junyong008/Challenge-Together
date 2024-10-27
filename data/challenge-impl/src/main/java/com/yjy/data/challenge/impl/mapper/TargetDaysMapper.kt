package com.yjy.data.challenge.impl.mapper

import com.yjy.model.challenge.TargetDays

// 이전 버전과의 호환을 위해 36500을 무제한으로 설정.
private const val INFINITE_TARGET_DAYS = 36500

internal fun TargetDays.toRequestString(): String = when (this) {
    is TargetDays.Infinite -> INFINITE_TARGET_DAYS.toString()
    is TargetDays.Fixed -> days.toString()
}

internal fun Int.toTargetDays(): TargetDays = if (this == INFINITE_TARGET_DAYS) {
    TargetDays.Infinite
} else {
    TargetDays.Fixed(days = this)
}
