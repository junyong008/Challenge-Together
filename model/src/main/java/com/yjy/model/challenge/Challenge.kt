package com.yjy.model.challenge

sealed class Challenge(
    open val id: String,
    open val title: String,
    open val description: String,
    open val category: Category,
    open val targetDays: TargetDays,
    open val currentParticipantCount: Int?,
    open val maxParticipantCount: Int?,
)
