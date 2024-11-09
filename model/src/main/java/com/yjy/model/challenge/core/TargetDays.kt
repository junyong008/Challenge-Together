package com.yjy.model.challenge.core

sealed class TargetDays {
    data object Infinite : TargetDays()
    data class Fixed(val days: Int) : TargetDays()

    fun toDays(): Int = when (this) {
        is Fixed -> days
        Infinite -> Int.MAX_VALUE
    }

    fun toRouteString(): String = when (this) {
        is Fixed -> days.toString()
        Infinite -> "Infinite"
    }

    companion object {
        fun fromRouteString(value: String): TargetDays =
            if (value == "Infinite") Infinite
            else Fixed(value.toInt())
    }
}
