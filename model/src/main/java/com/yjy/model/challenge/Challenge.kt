package com.yjy.model.challenge

sealed class Challenge(
    open val id: String,
    open val title: String,
    open val description: String,
    open val targetDays: TargetDays,
    open val participants: List<Participant>,
    open val category: Category,
)
