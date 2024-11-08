package com.yjy.model.challenge.core

sealed class TargetDays {
    data object Infinite : TargetDays()
    data class Fixed(val days: Int) : TargetDays()

    fun toDays(): Int = when (this) {
        is Fixed -> days
        Infinite -> Int.MAX_VALUE
    }
}
