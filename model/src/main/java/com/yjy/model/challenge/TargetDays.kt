package com.yjy.model.challenge

sealed class TargetDays {
    data object Infinite : TargetDays()
    data class Fixed(val days: Int) : TargetDays()
}
