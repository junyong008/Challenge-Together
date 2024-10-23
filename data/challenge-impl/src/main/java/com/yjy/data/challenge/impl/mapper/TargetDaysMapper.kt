package com.yjy.data.challenge.impl.mapper

import com.yjy.model.challenge.TargetDays

// 이전 버전과의 호환을 위해 36500을 무제한으로 설정.
internal fun TargetDays.toRequestString(): String = when (this) {
    is TargetDays.Infinite -> "36500"
    is TargetDays.Fixed -> days.toString()
}
